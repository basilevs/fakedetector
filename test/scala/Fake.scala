import fake.defender._
import fake.util._

import java.io.{BufferedReader, File, FileWriter, InputStreamReader, StringReader}
import java.nio.charset.Charset
import java.nio.file.{Path, Paths}
import java.net.{URL, URLConnection}

val hash = "FNFRK6TMBUPBSZCSNXT3GQOQMZAQCDXS5GWSZRQ"
println("Fake hash")
assert(new TTHHash(hash) == new TTHHash(hash))
assert(new TTHHash(hash) != new TTHHash(hash.replace('Q', 'T')))

val data = """AHJZPQFJUXA5YQZ72EKCANIQUQHJ7XGKRNGMTJI	Гадкий я DVD	Лестница Якоба	03.09.2010 21:55:09	SergeT
V53BQXWZJNKBBUUOHKCWO7R66K25EU5MV32NQMI	Интерны (31 серия)	Интерны 28	06.09.2010 16:29:51	ARTchi
"""

val fakes = new FakeTableParser(new BufferedReader(new StringReader(data))).toArray

assert(fakes(1).hash.toString == "V53BQXWZJNKBBUUOHKCWO7R66K25EU5MV32NQMI")
assert(fakes(1).wrongName.matches("Интерны блах блах (31 серия).avi"))
assert(!fakes(1).wrongName.matches("Интерны 31 серия.avi"))

assert(fakes(0).matches(new HashedFile("DVD Гадкий я.mkv", new TTHHash("AHJZPQFJUXA5YQZ72EKCANIQUQHJ7XGKRNGMTJI"))))
assert(!fakes(0).matches(new HashedFile("Гадкий я.mkv", new TTHHash("AHJZPQFJUXA5YQZ72EKCANIQUQHJ7XGKRNGMTJI"))))

val url = new URL("http://magnetida.ru/fakes/faketext.asp")
val conn = url.openConnection;
conn.connect
val length = conn.getContentLengthLong
if(length > 0) {
	val atlFakes = new FakeTableParser(new BufferedReader(new InputStreamReader(conn.getInputStream, Charset.forName("WINDOWS-1251")))).toArray
	println("Read %d fakes from atlantida".format(atlFakes.length))
} else {
	println("No data from atlantida")
}

package fake.util

import scala.util.matching.Regex

import java.net.URL
import java.io.{BufferedReader, InputStreamReader}
import java.nio.charset.Charset

trait FakeStorage {
	def remove(fake:Fake)
	def add(fake:Fake)
}

class FakeTableParser(reader: BufferedReader) extends Iterator[Fake] {
	var current: Fake = read
	private def read = {
		val line = reader.readLine
		if (line == null) {
			null
		} else {
			val fields = FakeTableParser.tabSeparator.split(line)
			try {
				new Fake(TTHHash(fields(0)), new DcNamePattern(fields(1)), fields(2), fields(3)+fields(4)) 
			} catch {
				case e: Throwable => throw new Exception("Failed to parse: "+line, e)
			}
		}
	}
	def hasNext = current != null
	def next = {
		val rv = current
		current = read
		rv
	}
}

class FakeTable(url: URL, charset:Charset, storage: FakeStorage) {
	def reader = {
		new BufferedReader(new InputStreamReader(url.openStream, charset))
	}
	def this(url:URL, storage:FakeStorage) = this(url, FakeTableParser.getCharset(url), storage)
	val thread = new Thread() {
		override def run {
			new FakeTableParser(reader).foreach(storage.add)
			Thread.sleep(60000)
		}
	}
	thread.setDaemon(true)
	thread.start
}

object FakeTableParser {
	def getCharset(url:URL) = {
		val conn = url.openConnection;
		conn.connect
		Charset.forName(conn.getContentEncoding())
	}
	val tabSeparator = new Regex("\t")
	val spaceSeparator = new Regex("\t")	
}
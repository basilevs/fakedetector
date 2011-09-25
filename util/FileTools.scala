package fake.util;
import java.nio.file.{Path, Files}
import java.nio.charset.Charset
import java.io.{Reader, BufferedReader, InputStream, InputStreamReader}

object FileTools {
	def readWholeFile(path: Path, charset:Charset) = {
		val r=Files.newBufferedReader(path, charset)
		readBuffered(r)
	}
	def readReader(reader: Reader) = readBuffered(new BufferedReader(reader))
	def readBuffered(reader: BufferedReader) = {
		val sb = new StringBuffer()
		while (reader.ready) {
			if (sb.length > 0)
				sb.append("\n")
			sb.append(reader.readLine)
		}
		sb.toString
	}
	def readStream(stream: InputStream, charset: Charset) = readReader(new InputStreamReader(stream, charset))
	def printFileInfo(path:Path) {
		println("File %s, size %d, modified %s".format(path.toString, Files.size(path), Files.getLastModifiedTime(path)))
		println("Content:")
		println(readWholeFile(path, Charset.defaultCharset))
	}

}

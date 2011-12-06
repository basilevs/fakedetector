package fake.defender

import scala.util.control.Exception.{ignoring, ultimately}
import scala.xml.{NodeSeq, Node, XML, Utility}
import scala.actors.Actor

import java.nio.charset.Charset.defaultCharset
import java.nio.charset.Charset
import java.nio.file.{StandardWatchEventKinds, WatchService, Path, Paths, Files, ClosedWatchServiceException}
import java.io.{Reader, InputStream}


import fake.util._

object QueueParser {
	def unescape(s:String): String = {
		val rv = new StringBuilder
		Utility.unescape(s, rv)
		rv.toString
	}
	def parseDownload(node:NodeSeq): HashedFile = {
		val path = (node \ "@Target").text
		val name = path.substring(path.lastIndexOf("\\")+1)
		new HashedFile(name, new TTHHash((node \ "@TTH").text))
	}
	def parseDownloads(elem:Node): Iterable[HashedFile] = {
		if (elem.label != "Downloads")
			throw new ParseError("Invalid XML entry "+elem)
		(elem \ "Download").map(parseDownload)
		
	}
	def parse(reader: Reader) = parseDownloads(XML.load(reader))
	def parse(stream: InputStream) = parseDownloads(XML.load(stream))
}

class QueueWatcher(path: Path, hashedFilesReceiver:(HashedFile) => Unit) {
	val watcher = FileWatcher(path, ()=>this.onChange)
	onChange
	private def onChange {
		if (Files.size(path) > 0) {
			val fs=Files.newInputStream(path)
			QueueParser.parse(fs).foreach(hashedFilesReceiver(_))
		}
	}
}


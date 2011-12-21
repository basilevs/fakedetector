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
	def parseDownload(node:NodeSeq, source:HashedFileSource): HashedFile = {
		val path = (node \ "@Target").text
		val name = path.substring(path.lastIndexOf("\\")+1)
		new HashedFile(name, new TTHHash((node \ "@TTH").text), Paths.get(path), source)
	}
	def parseDownloads(elem:Node, source:HashedFileSource): Iterable[HashedFile] = {
		if (elem.label != "Downloads")
			throw new ParseError("Invalid XML entry "+elem)
		(elem \ "Download").map(parseDownload(_, source))
		
	}
	def parse(reader: Reader, source:HashedFileSource) = parseDownloads(XML.load(reader), source)
	def parse(stream: InputStream, source:HashedFileSource) = parseDownloads(XML.load(stream), source)
}

class QueueWatcher(val path: Path) extends HashedFileSource {
	override def hashCode = path.hashCode
	override def equals(that:Any) = that match {
		case qw: QueueWatcher => qw.path == path
		case _ => false
	}
	val watcher = FileWatcher(path, ()=>this.onChange)
	onChange
	private def onChange {
		if (Files.size(path) > 0) {
			val fs=Files.newInputStream(path)
			QueueParser.parse(fs, this).foreach(onFile)
		}
	}
}


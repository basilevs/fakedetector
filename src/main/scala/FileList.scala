package fake.defender

import scala.xml.{Node, XML, SpecialNode}
import java.io.InputStream
import java.nio.file.{Files, Path, Paths}

import org.apache.tools.bzip2.CBZip2InputStream

import fake.util.{TTHHash, HashedFile, ParseError}



class FileListParser(shares:Map[String, Path])  {
	//<File Name="ASPNET.msp" Size="3010560" TTH="PJH6ZVX773KKSR62EJAWJ5CDLSUVHB5OBMU5T6Q"/>
	def parseFile(path:Path, node: Node) = {
		val name = (node \ "@Name").text
		val rv = new HashedFile(name, new TTHHash((node \ "@TTH").text), path.resolve(name))
		println(rv)
		rv
	}
	def truncate(s:String) = s.substring(0, Math.min(50, s.length))
	//<Directory Name="dotNetFramework">
	def parseDirectory(path:Path, node: Node): Iterable[HashedFile] = {
		val dirPath = path.resolve((node \ "@Name").text)
		val subItemSeqs = for (item <- node.nonEmptyChildren if !item.isInstanceOf[SpecialNode]) yield {
			println("Directory:"+dirPath)
			try {
				item.label match {
					case "Directory" => parseDirectory(dirPath, item)
					case "File" => Seq(parseFile(dirPath, item))
				}
			} catch {
				case e: Throwable => throw new ParseError("Failed to parse directory entry "+truncate(item.toString), e)
			}
		}		
		subItemSeqs.flatten
	}
	//<FileListing Version="1" CID="QDM5KPQWER4HYOCXMSFFW6DOULLU5SUROMDMLCQ" Base="/" Generator="DC++ 0.7091">
	def parseListing(node:Node) = {
		if (node.label != "FileListing")
			throw new ParseError("Invalid XML entry "+node)
		println("Filelisting")
		val subItemSeqs = for (item <- (node \ "Directory")) yield {
			val name = (node \ "@Name").text
			try {
				val path = shares.get(name)
				if (path.isEmpty) {
					println("Directory:"+name)
					parseDirectory(Paths.get(""), item)
				} else {
					println("Directory:"+path.get)
					parseDirectory(path.get, item)
				}
			} catch {
				case e: Throwable => throw new ParseError("Failed to parse directory entry "+truncate(item.toString), e)
			}
		}
		subItemSeqs.flatten
	}
	def parseStream(stream: InputStream): Iterable[HashedFile] = parseListing(XML.load(stream))
	def parseBzippedStream(stream: InputStream) = {
		if (stream.read != 0x5a || stream.read != 0x42) {
			throw new ParseError("Bzipped stream has invalid header")
		}
		parseStream(new CBZip2InputStream(stream))
	}
	def parseFile(path: Path) = parseBzippedStream(Files.newInputStream(path))
}

object SettingsParser {
/*
<DCPlusPlus>
	<Share>
		<Directory Virtual="software">\\pebble.local\software\</Directory>
		<Directory Virtual="anime">\\pebble.local\anime\</Directory>
		<Directory Virtual="music">\\pebble.local\music\</Directory>
	</Share>
</DCPlusPlus>
*/
	def parse(node:Node):Map[String, Path] = {
		(for (share <- (node \\ "Share"); dir <- (share \ "Directory")) yield {
			((dir \ "@Virtual").text, Paths.get(dir.text))
		}).toMap
	}
	def parseStream(stream: InputStream): Map[String, Path] = parse(XML.load(stream))
}

class FileListWatcher(path: Path, settings: Path, hashedFilesReceiver:(HashedFile) => Unit) {
	val watcher = new FileWatcher(path, onChange)
	val shares = SettingsParser.parseStream(Files.newInputStream(settings))
	onChange
	private def onChange() {
		new FileListParser(shares).parseFile(path).foreach(hashedFilesReceiver(_))
	}
}

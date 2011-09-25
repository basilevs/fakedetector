package fake.defender

import java.io.InputStream
import java.nio.file.{Files, Path}

import org.apache.tools.bzip2.CBZip2InputStream

import fake.util.HashedFile



object FileListParser {
	def parseStream(stream: InputStream): Iterable[HashedFile] = {
		//TODO
		return Seq[HashedFile]()
	}
	def parseBzippedStream(stream: InputStream) = parseStream(new CBZip2InputStream(stream))
	def parseFile(path: Path) = parseBzippedStream(Files.newInputStream(path))
}

class FileListWatcher(path: Path, hashedFilesReceiver:(HashedFile) => Unit) {
	val watcher = new FileWatcher(path, onChange)
	private def onChange() {
		FileListParser.parseFile(path).foreach(hashedFilesReceiver(_))
	}
}

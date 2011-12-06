package fake.defender;

import scala.util.control.Exception.{ignoring, ultimately}
import scala.collection.JavaConversions
import scala.collection.mutable.{Set, HashMap, MultiMap}

import java.nio.file.{StandardWatchEventKinds, WatchService, WatchKey, Path, ClosedWatchServiceException}
import java.nio.file.Files.size
import java.io.Closeable

object FileWatcher {
	case class Listener(path:Path, callback: ()=>Unit)
	def apply(path:Path, onChange: ()=>Unit) = {
		val rv = new FileWatcher(path.getFileSystem.newWatchService)
		rv.watch(path, onChange)
		rv
	}
}

class FileWatcher(watchService: WatchService) extends Closeable with Set[FileWatcher.Listener] {
	import FileWatcher._
	val listeners = new HashMap[WatchKey, Set[Listener]]() with MultiMap[WatchKey, Listener]
	private def key(elem: Listener) = {
		elem.path.getParent.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
	}
	override def contains(elem: Listener) = {
		listeners.entryExists(key(elem), _ == elem)
	}
	override def iterator = listeners.iterator.flatMap(_._2)
	override def +=(elem: Listener) = {
		listeners.addBinding(key(elem), elem)
		this
	}
	override def -=(elem: Listener) = {
		listeners.removeBinding(key(elem), elem)
		this
	}
	
	def watch(path:Path, callback: () => Unit) = {
		var rv = new Listener(path, callback)
		this += rv
		rv
	}
	val thread = new Thread {
		override def run {
			ultimately{watchService.close}
			ignoring(classOf[InterruptedException], classOf[ClosedWatchServiceException]) {
				do {
					val key = watchService.take()
					val directoryListeners = listeners(key)
					for (val event <- JavaConversions.asScalaIterable(key.pollEvents)) {
						val eventPath = event.context.asInstanceOf[Path]
//						println("Path %s modified. New size: %d".format(path.toString(), path.toFile.length))
						for (val listener <- directoryListeners) {
							if (listener.path.getFileName == eventPath) {
								try {
										listener.callback()
								} catch {
									case e:Throwable => e.printStackTrace
								}
							}
						}
					}
					if (!key.reset)
						return
				} while (true) 
			}
		}
	}
	override def close {
		watchService.close
	}
	thread.setDaemon(true)
	thread.start
} // class FileWatcher
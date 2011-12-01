package fake.defender

import scala.util.control.Exception.{ignoring, ultimately}
import scala.collection.JavaConversions

import java.nio.file.{StandardWatchEventKinds, WatchService, Path, ClosedWatchServiceException}
import java.io.Closeable

class FileWatcher(path:Path, callback: () => Unit) extends Closeable {
	val service = path.getFileSystem.newWatchService
	val thread = new Thread {
		override def run {
			val parent = path.getParent
			val key = parent.register(service, StandardWatchEventKinds.ENTRY_MODIFY)
			println("Waiting for %s".format(path.toString))
			ultimately{key.cancel}
			ignoring(classOf[InterruptedException], classOf[ClosedWatchServiceException]) {
				do {
					service.take()
					for (val event <- JavaConversions.collectionAsScalaIterable(key.pollEvents)) {
						val eventPath = event.context.asInstanceOf[Path]
//						println("Path %s modified. New size: %d".format(path.toString(), path.toFile.length))
						if (path == parent.resolve(eventPath) && path.toFile.length>0) {
							try {
								callback()
							} catch {
								case e:Throwable => e.printStackTrace
							}
						}
					}
				} while (key.reset) 
			}
		}
	}
	override def close {
		service.close
	}
	thread.setDaemon(true)
	thread.start
}

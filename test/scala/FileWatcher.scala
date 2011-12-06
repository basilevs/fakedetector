//Tests if we can organize two-way map with WatchKey and callbacks
import java.nio.file.{StandardWatchEventKinds, WatchService, WatchKey, Path, ClosedWatchServiceException}
import java.nio.file.{Files, Paths}
import java.io.Closeable
val paths = Array("path1", "path2", "path1") map (p => Paths.get(p).toAbsolutePath)
paths map println
val service = paths(0).getFileSystem.newWatchService
def register(path:Path) = {
	path.register(service, StandardWatchEventKinds.ENTRY_MODIFY)
}
val keys:Map[WatchKey, Path] = Map(paths map (p => (register(p), p)) : _* )

assert(keys.size == 2) 




import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.FileAttribute;

class NioModifiedProblem {
	public static void println(String str) {
		System.out.println(str);
	}
	public static void printFileInfo(Path path) {
		try {
			println(String.format("File %s, size %d, modified %s", path, Files.size(path), Files.getLastModifiedTime(path)));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		String data = "Some not too long string goes here. Goes. Goes.";
		try {
			final Path path = Files.createTempFile("nioProblem", ".tmp", new FileAttribute[0]);
			path.toFile().deleteOnExit();
			println("Created");
			printFileInfo(path);
		
			Thread thread = new Thread() {
				public void run() {
					try {
						final Path parent = path.getParent();
						final WatchService service = parent.getFileSystem().newWatchService();
						WatchKey key = parent.register(service, StandardWatchEventKinds.ENTRY_MODIFY);
						try {
							while (true) {
								for (WatchEvent<?> event : service.take().pollEvents()){
									Path modifiedPath = parent.resolve((Path)event.context());
									println("Path "+modifiedPath+" modified EVENT."); // This is printed only once, on file opening.
									printFileInfo(modifiedPath);
								}
							}
						} catch (ClosedWatchServiceException e) {
							println("Service closed");
						}
					} catch (Throwable e) {
						e.printStackTrace();
					} finally {
						println("Watcher thread exiting");
					}
				}
			};
			thread.setDaemon(true);
			thread.start();

			Thread.sleep(1000);
			Writer fw = Files.newBufferedWriter(path, Charset.forName("UTF-8"));
			println("Opened");
			printFileInfo(path);

			Thread.sleep(1000);
			fw.write(data);
			println("Written");
			printFileInfo(path);
			
			fw.close();
			println("Closed");
			printFileInfo(path);
			
			Thread.sleep(1000);
			println("Sleeped");
			printFileInfo(path);
			return;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

// Java(TM) SE Runtime Environment (build 1.7.0-b147)
// Created
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 0, modified 2011-09-14T16:20:06.782Z
// Opened
// Path C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp modified EVENT.
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 0, modified 2011-09-14T16:20:07.807Z
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 0, modified 2011-09-14T16:20:07.807Z
// Written
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 0, modified 2011-09-14T16:20:07.807Z
// Closed
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 47, modified 2011-09-14T16:20:08.81Z
// Sleeped
// File C:\Users\b\AppData\Local\Temp\nioProblem190636654560972941.tmp, size 47, modified 2011-09-14T16:20:08.81Z


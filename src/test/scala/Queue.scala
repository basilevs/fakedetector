import scala.actors.Actor.{actor, receive}
import scala.actors.DaemonActor
import org.scalatest.FunSuite

import java.io.{File, FileWriter, StringReader}

import fake.defender._
import fake.util._


class QueueSuite extends FunSuite {
val data ="""
<Downloads Version="2.22">
<Download Target="\\pebble.local\anime\NieA under 7\NieA_under_7_TV_[06_of_13]_[ru_jp]_[Suzaku_&amp;_AnimeReactor_Ru].mkv" Size="415436119" Priority="3" Added="1315632928" TTH="NSYCUU4QSTWCIBTKZET32G3UEEUNSBJFQ4UC4FA" AutoPriority="0" MaxSegments="7">
  <Source CID="FNFRK6TMBUPBSZCSNXT3GQOQMZAQCDXS5GWSZRQ" Nick="MoonRainbow" /> 
</Download>
</Downloads>
"""
	test("memory parsing") {
		val parsed:Array[HashedFile] = QueueParser.parse(new StringReader(data)).toArray
		assert(parsed.length == 1)
		println (parsed(0).name)
		assert(parsed(0).name == "NieA_under_7_TV_[06_of_13]_[ru_jp]_[Suzaku_&_AnimeReactor_Ru].mkv")
		println (parsed(0).hash)
		assert(parsed(0).hash.toString == "NSYCUU4QSTWCIBTKZET32G3UEEUNSBJFQ4UC4FA")
	}

	test("on file change") {
		val file = File.createTempFile("queue", ".tmp")
		file.deleteOnExit
		var event = false
		val hashedFilePrinter = new DaemonActor {
			def act {
				while (true) receive {
					case hf:HashedFile => {
						assert(hf.hash.toString == "NSYCUU4QSTWCIBTKZET32G3UEEUNSBJFQ4UC4FA")
						event = true
					}
					case _ => println("No match!")
				}
			}
		}
		hashedFilePrinter.start
		val qw = new fake.defender.QueueWatcher(file.toPath, hashedFilePrinter.!)
		Thread.sleep(100)
		val fw = new FileWriter(file)
		fw.write(data)
		fw.close
		Thread.sleep(100)
		assert(event)
	}
}
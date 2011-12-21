package fake.defender;

import fake.util._
import scala.collection.mutable.{Set, HashSet, SetProxy}

// Connects fake sources and file sources
// Emits fake reports


class Controller(onReport: (FakeReport) => Unit) {
	private class FilesSet extends SetProxy[HashedFileSource] {
		private val files = new HashSet[HashedFileSource]()
		val self = files
		override def += (files:HashedFileSource) = {
			files.onFile = onFile
			super.+=(files)
		}
		override def -=(files:HashedFileSource) = {
			files.onFile = null
			super.-=(files)
		}
	}
	val fakeSources =  new HashSet[FakeSource] 
	val fakes = new FakeSourceCascade(fakeSources)
	private val _files = new FilesSet()	
	val reports = new HashSet[FakeReport]()
	def onFile(file:HashedFile) {
		for(report <- fakes.find(file)) {
			if (reports.add(report))
				onReport(report)
		}
	}
	def files: Set[HashedFileSource] = _files
}
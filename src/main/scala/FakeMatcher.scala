package fake.defender

import scala.collection.mutable.HashSet
import fake.util._

// Filters hashed files through a set of fakes and notifies receiver with ReportedFake
class FakeMatcher(fakes:FakeSource, receiver: ReportedFake=>Unit) {
	val reported = new HashSet[ReportedFake]()
	def report(r:ReportedFake) {
		//This is not typesafe, but of low importance
		if (! reported.contains(r))
			receiver(r)
	}
	def process(file: HashedFile) {
		fakes.matches(file).filter(_.matches(file)).foreach(fake => report(new ReportedFake(file, fake)))
	}
}
package fake.defender

import scala.collection.mutable.HashMap
import fake.util._

// Filters hashed files through a set of fakes and notifies receiver with ReportedFake
class FakeMatcher(receiver: ReportedFake=>Unit) extends FakeStorage {
	val fakes = new HashMap[Hash, Fake]()
	def process(file: HashedFile) {
		val fakeOption =  fakes.get(file.hash)
		if (!fakeOption.isEmpty) {
			if (fakeOption.get.matches(file)) {
				receiver(new ReportedFake(file, fakeOption.get))
			}
		}
	}
	def update(toAdd:Iterator[Fake], toDelete:Iterator[Fake]) {
		fakes ++ toAdd.map(x=>(x.hash, x))
		fakes -- toDelete.map(_.hash)
	}
}
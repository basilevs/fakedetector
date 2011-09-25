package fake.defender

import fake.util._

// Filters hashed files through a set of fakes and notifies receiver with ReportedFake
class FakeMatcher(fakes: Map[Hash, Fake], receiver: ReportedFake=>Unit) {
	def process(file: HashedFile) {
		val fakeOption =  fakes.get(file.hash)
		if (!fakeOption.isEmpty) {
			if (fakeOption.get.matches(file)) {
				receiver(new ReportedFake(file, fakeOption.get))
			}
		}
	}
}
package fake.util

import scala.util.matching.Regex
import scala.ref.WeakReference
import scala.collection.mutable.HashSet

case class Fake(hash: Hash, wrongName:NamePattern, name:String, comment: String = null, origin:FakeSource = null) {
	override def hashCode = hash.hashCode ^ wrongName.hashCode
	def matches(file: HashedFile) = file.hash == hash && wrongName.matches(file.name)
}

case class FakeReport(file: HashedFile, fake: Fake) {}

trait FileMatcher {
	def find(file: HashedFile): Option[FakeReport]
}

trait FakeSource extends Iterable[Fake]  with FileMatcher {
	def find(file: HashedFile): Option[FakeReport] = find( fake => fake.matches(file) ).map(fake => new FakeReport(file, fake))
}

class FakeSourceCascade(sources: Iterable[FakeSource]) extends FakeSource {
	override def iterator = sources.iterator.flatten
	override def find(file: HashedFile): Option[FakeReport] = sources.flatMap(_.find(file).iterator).headOption
}

trait NamePattern {
	def matches(name:String): Boolean
}

case class RegexNamePattern(pattern: Regex) extends NamePattern {
	def matches(name:String): Boolean = pattern.findFirstMatchIn(name).isDefined
	override def toString = pattern.toString
}

//A set of substrings that may be matched in any order
case class DcNamePattern(words: Set[String]) extends NamePattern {
	def this(pattern:String) = this(DcNamePattern.separator.split(pattern).toSet)
	def matches(name: String) = !words.exists(name.indexOf(_) < 0)
} 

object DcNamePattern {
	val separator = new Regex(" +")
}
package fake.util

import scala.util.matching.Regex
import scala.ref.WeakReference
import scala.collection.mutable.HashSet

case class Fake(hash: Hash, wrongName:NamePattern, name:String, comment: String = null, origin:FakeSource = null) {
	def matches(file: HashedFile) = file.hash == hash && wrongName.matches(file.name)
}


trait FakeSource extends Iterable[Fake] {
	def matches(file: HashedFile): Option[Fake] = find( fake => fake.matches(file) )
}

class FakeSourceCascade(val sources:Set[FakeSource]) extends FakeSource {
	def iterator = sources.iterator.flatten
	override def matches(file: HashedFile) = sources.flatMap(_.matches(file)).headOption
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
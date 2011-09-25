package fake.util

import scala.util.matching.Regex

case class Fake(hash: Hash, wrongName:NamePattern, name:String, comment: String = null) {
	def matches(file: HashedFile) = file.hash == hash && wrongName.matches(file.name)
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
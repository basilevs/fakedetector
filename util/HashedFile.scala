package fake.util

import java.nio.file.Path

trait Hash {}

case class TTHHash(hash:String) extends Hash {
	override def toString = hash
	override def equals(that: Any) = hash == that.asInstanceOf[TTHHash].hash
}

case class HashedFile(name:String, hash:Hash, path:Path) {
	def this(name:String, hash:Hash) = this(name, hash, null)
	override def toString = "HashedFile("+name+", "+hash+")"
}

 
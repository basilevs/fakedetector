package fake.util

trait Hash {}

case class TTHHash(hash:String) extends Hash {
	override def toString = hash
	override def equals(that: Any) = hash == that.asInstanceOf[TTHHash].hash
}

case class HashedFile(name:String, hash:Hash) {
	override def toString = "HashedFile("+name+", "+hash+")"
}
 
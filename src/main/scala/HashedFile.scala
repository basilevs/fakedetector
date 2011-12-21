package fake.util

import java.nio.file.Path

trait Hash {}

case class TTHHash(hash:String) extends Hash {
	override def toString = hash
	override def equals(that: Any) = {
		hash == that.asInstanceOf[TTHHash].hash
	}
}

case class HashedFile(name:String, hash:Hash, path:Path, origin:HashedFileSource = null) {
	override def hashCode = name.hashCode ^ hash.hashCode ^ path.hashCode
	override def equals(that:Any) = that match {
		case HashedFile(tname, thash, tpath, torigin) => (thash == hash) && (tname == name) && (tpath == tpath)
		case _ => false
	}
	def this(name:String, hash:Hash) = this(name, hash, null)
	override def toString = "HashedFile("+name+", "+hash+")"
}

trait HashedFileSource {
	var onFile: (HashedFile) => Unit = null
}
 
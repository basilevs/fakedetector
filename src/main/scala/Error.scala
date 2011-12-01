package fake.util

class Error(message:String, cause: Throwable) extends Exception(message, cause) {
	def this(message:String) = this(message, null)
}

class ParseError(message1:String, cause1: Throwable) extends Error(message1, cause1) {
	def this(message:String) = this(message, null)
}

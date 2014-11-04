import scala.collection.mutable.Map

object LastScan {

	var _data: Map[String, Long] = load

	def put(key: String, value: Long): Unit = ???

	def get(key: String): Long = ???

	private def load: Map[String, Long] = ???

	private def dump: Unit = ???

}
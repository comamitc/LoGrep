import java.io.File
import java.io.PrintWriter

import scala.io.Source

import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)

object LastScan {

  val filePath = "./data/hist.dat"
  var _data: Map[String, Long] = load

  // put data in mutable collection and save it to file
  def put(key: String, value: Long): Unit = {
    _data = _data + ((key, value))
    dump
  }

  def get(key: String): Long = _data.getOrElse(key, 0)

  private def load: Map[String, Long] = {
    val f = new File(filePath)
    if (f.exists) {
      val t = Source.fromFile(f).mkString
      if (t.length > 0) t.parseJson.convertTo[Map[String, Long]]
      else Map[String, Long]()
    } else Map[String, Long]()
  }

  private def dump: Unit = {
    val writer = new PrintWriter(new File(filePath))
    writer.write(_data.toJson.toString)
    writer.close()
  }

}
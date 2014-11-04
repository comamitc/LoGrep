import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports.DateTimeFormat

import uniform.config.UniformConfig

trait Line {
  val date: DateTime
}

case object NoLine extends Line {
  val date: DateTime = null
}

case class LogLine(line: String) extends Line {

  val fromFormat = DateTimeFormat
    .forPattern(UniformConfig.get("date_format"))

  def fromTomcat(t: String): DateTime = fromFormat.parseDateTime(t)

  val date = fromTomcat(line.slice(0, 19))

  override def toString() = line + "\n"

}
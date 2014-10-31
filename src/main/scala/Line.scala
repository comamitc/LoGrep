import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports.DateTimeFormat

case class Line(line: String) {

	val fromFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")

	def fromTomcat(t: String): DateTime = fromFormat.parseDateTime(t)

	val date = fromTomcat(line.slice(0, 19))
}
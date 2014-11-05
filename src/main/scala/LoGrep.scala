import scala.util.matching.Regex
import java.io.File
import java.io.FileNotFoundException

import uniform.io.TextFile
import uniform.sys.FileSystem
import uniform.config.UniformConfig

object LoGrep {

  val anyThing = ".*".r
  private val newLine = UniformConfig.get("new_line").r
  private val ignoreLine = UniformConfig.get("ignore_line").r

  // get config variable; if not default to false
  private val RECORD_HISTORY: Boolean = UniformConfig.get("record_history") match {
    case "true" => true
    case _ => false
  }

  // use hist configuration with a default mode of false
  private val useHist = UniformConfig.get("use_hist") match {
    case "true" => true
    case _ => false
  }

  def sortLines(a: Line, b: Line) = (a.date).compareTo(b.date) < 0

  /**
   * 	 Function passed to TextFile for each line to process
   */
  def printMatchedString(term: Regex, lastScan: Long)(line: String): Line = term findFirstIn line match {
    case Some(str) => {
      val l = new LogLine(line)
      if (lastScan < l.date.getMillis) l
      else NoLine
    }
    case None => NoLine
  }

  /**
   * 	 Using a Regex search and a File iterate and find all
   */
  def parseFile(term: Regex)(f: File): List[Line] = {
    val filePath = f.getAbsolutePath
    val func = printMatchedString(term, LastScan.get(filePath)) _
    val lines = new TextFile(filePath, newLine, ignoreLine)
      .getLines(func)
      .filter(_ != NoLine)
    if (lines.size > 0 && RECORD_HISTORY)
      LastScan.put(filePath, lines.maxBy(_.date.getMillis).date.getMillis)

    // return lines
    lines
  }

  /**
   * 	With a filePattern get a list of files that match the criteria
   * 	parse each file applying a stdout function if file contents match searchTerm
   */
  def processFiles(term: Regex = anyThing, filePat: String = "."): Unit = {
    val func = parseFile(term) _
    val fs = new FileSystem(filePat)
    val files = fs.listFiles

    fs.listFiles
      .foldLeft(List[Line]())((acc, file) => {
        func(file) ::: acc
      })
      .sortWith(sortLines)
      .foreach(println)
  }

  /**
   * Entry point for the program
   *
   * TODO: case insensitive, recursive directory
   */
  def main(args: Array[String]): Unit = {
    args.size match {
      case 0 => processFiles()
      case 1 => processFiles(args(0).r)
      case _ => processFiles(args(0).r, args(1))
    }
  }
}
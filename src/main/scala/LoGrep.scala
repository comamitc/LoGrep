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

  /**
   * 	 Function passed to TextFile for each line to process
   */
  def printMatchedString(term: Regex)(line: String): Unit = term findFirstIn line match {
    case Some(str) => println(line)
    case None => {} // do nothing
  }

  /**
   * 	 Using a Regex search and a File iterate and find all
   */
  def parseFile(term: Regex)(f: File): Unit = {
    val func = printMatchedString(term) _
    new TextFile(f.getAbsolutePath, newLine, ignoreLine)
      .getLines(func)
  }

  /**
   * 	With a filePattern get a list of files that match the criteria
   * 	parse each file applying a stdout function if file contents match searchTerm
   */
  def processFiles(term: Regex = anyThing, filePat: String = "."): Unit = {
    val func = parseFile(term) _
    val fs = new FileSystem(filePat)
    fs.listFiles foreach func
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
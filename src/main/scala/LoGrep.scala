import scala.util.matching.Regex
import java.io.File
import java.io.FileNotFoundException

import io.TextFile

object LoGrep {

  val newLine = "(^\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*)".r
  val ignoreLine = "(^\\s*\\t*\\r*)".r

  /**
   * Converts a File or a Directory to a List[File]
   *
   * TODO: handle filter out non 'text/plain' formats
   */
  def fileListAdapter(file: File): List[File] = {
    if (file.exists()) {
      if (file.isDirectory()) file.listFiles().toList()
      else if (file.isFile()) List(file)
      else throw new Exception("System type unknown for: " + file.getPath())
    } else {
      throw new FileNotFoundException(file.getPath() + " not found")
    }
  }

  /**
   * 	 Function passed to TextFile for each line to process
   */
  def printMatchedString(term: Regex)(line: String): Unit = term findFirstIn line match {
    case Some(str) => println(line)
    case None => {} // do nothing
  }

  /**
  *	 Using a Regex search and a File iterate and find all
  */
  def parseFile(term: Regex, f: File): Unit = {
    val func = printMatchedString(term)
    new TextFile(f.getCononicalPath, newLine, ignoreLine)
    	.getLines(func)
  }

  /**
   *
   */
  def processFiles(term: Regex = ".*".r, filePat: String = "."): Unit = {
    def inner(list: List[File]): Unit = list match {
      case head :: Nil => parseFile(f)
      case head :: tail => {
        parseFile(head)
        inner(tail)
      }
      case Nil => throw new Exception("Unknown state for processor.")
    }
    inner(fileListAdapter(new File(filePat)))
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
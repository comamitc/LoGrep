import scala.util.matching.Regex
import java.io.File
import java.nio.file.Files
import java.io.FileNotFoundException

import io.TextFile

object LoGrep {

	val anyThing = ".*".r
  val newLine = "(^\\d{4}-\\d{2}-\\d{2}[ T]\\d{2}:\\d{2}:\\d{2}.*)".r
  val ignoreLine = "(^\\s*\\t*\\r*)".r

  def isFile(file: File, wildCard: String) =
  	(file.isFile() && file.getName().matches(wildCard)/* && Files.probeContentType(file.toPath) == "text/plain"*/)
  /**
   * Converts a File or a Directory to a List[File]
   *
   * TODO: handle filter out non 'text/plain' formats
   */
  def fileListAdapter(file: File, wildCard: String): List[File] = {
    if (file.exists()) {
      if (file.isDirectory())
        file.listFiles()
          .foldLeft(Nil: List[File])((acc, f) => fileListAdapter(f, wildCard) ::: acc) // recursion by default
      else if (isFile(file, wildCard)) List(file)
      else Nil
    } else {
      throw new FileNotFoundException(file.getPath() + " not found")
    }
  }

  def assembleWildCard(wildCard: String): String =
  	wildCard.split('*')
  		.map(x => if (x.isEmpty) "(.*)" else "(" + x + ")")
  		.mkString("(.*)")

  def assemblePath(list: List[String]) =
  	if (list.isEmpty || list.head.isEmpty) "./"
  	else list.foldLeft("")((acc, s) => s + '/' + acc)

  // TODO: handle filepat wildcards
  def normalizeFilePattern(pat: String): List[File] = {
  	if (pat.contains('*')) {
  		val paths = pat.split(Array('\\', '/')).toList.reverse
  		fileListAdapter(new File(assemblePath(paths.tail)),
  			assembleWildCard(paths.head))
  	} else fileListAdapter(new File(pat), "(.*)")
  }

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
   *
   */
  def processFiles(term: Regex = anyThing, filePat: String = "."): Unit = {
    val func = parseFile(term) _
    def inner(list: List[File]): Unit = list match {
      case head :: Nil => func(head)
      case head :: tail => func(head); inner(tail);
      case _ => {} // do nothing on an empty list of files
    }
    inner(normalizeFilePattern(filePat))
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
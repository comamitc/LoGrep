package io

import java.io.FileNotFoundException
import java.io.IOException

import scala.annotation.tailrec
import scala.io.Source
import scala.util.matching.Regex

class ConfigException(m: String) extends Exception(m)

/**
 * `FileOps` is a protected trait that establishes the basis for the
 *  file parsers.
 *
 *  @param <T> specifies the `Type` of line iterator contents that will be produced
 */
sealed trait FileOps {
  /**
   * @param filename 	String path to file to open and read
   * @return 			scala.io.Source opened file
   */
  def open(filename: java.io.File): Source = {
    try {
      Source.fromFile(filename)
    } catch {
      case ex: FileNotFoundException =>
        throw new ConfigException("No File: " + filename)
      case ex: IOException =>
        throw new ConfigException("Had an IOException trying to read file: " + filename)
      case ex: Exception =>
        throw new ConfigException("Some weird shit happened while trying to open: " + filename)
    }
  }

  /**
   * `ensure` build an if / else function that we can pass a test condition to.  This
   *  is mostly used for code simplification further down.
   *
   * @param f 			function to test if true. @return Boolean
   * @param truth		function to execute on true `f` evaluation
   * @param lie			function to execute on false `f` evaluation
   * @return			Type `Z` will be returned from truth or lie
   */
  def ensure[Z](f: => Boolean)(truth: => Z, lie: => Z) = if (f) truth else lie

}

/**
 * TxtFile is for Plain Text parsing of a file.
 *
 * @param filename		path and name of file to be parsed
 * @param newline
 * @param comment
 */
sealed class TextFile[A](filename: java.io.File, newline: Regex, comment: Regex) extends FileOps {

  /** Private value that extracts `\?n` delimited lines from a file. */
  val ctx: Iterator[String] = this.open(filename).getLines

  def this(filename: java.io.File) = this(filename, "(^[\\w]+.*[:=].*$)".r, "(^[#|//].*)".r)

  def this(filename: java.io.File, newline: Regex) = this(filename, newline, "(^[#|//].*)".r)

  /** auxiliary constructor when only supplying a filename: String */
  def this(filename: String) = this(new java.io.File(filename), "(^[\\w]+.*[:=].*$)".r, "(^[#|//].*)".r)

  /** auxiliary constructor when only supplying a filename: String, newline: Regex */
  def this(filename: String, newline: Regex) = this(new java.io.File(filename), newline, "(^[#|//].*)".r)

  def this(filename: String, newLine: Regex, comment: Regex) = this(new java.io.File(filename), newLine, comment)

  /* This function slows down string processing considerably*/
  private def trim(s: String): String =
    s.replaceAll("(^\\s*\\t*\\r*)|(\\s*\\t*\\r*$)|(\\s*\\t*\\r*//.*$)|(\\s*\\t*\\r*#.*$)", "")

  /**
   * Fake function for doing nothing when text processing
   * just needs to be straight extraction
   */
  private def returnSelf(s: String) = s

  /**
   * Will generate ThrowAwayIterator[List[String]]
   *
   * Overload method to handle no function passing to getLines.
   */
  def getLines(): List[String] = this.getLines[String](returnSelf)

  /**
   * Will generate ThrowAwayIterator[List[A]] from Iterator[String]
   *
   * @param func		String => A	: Applied function to do text pre-processing. Used to
   * 								  increase efficiency in processing and keep passes through lines 0(n)
   * @return 			A			:
   */
  def getLines[A](func: String => A): List[A] = {
    @tailrec
    def inner(fileLine: String, logLine: String = null, acc: List[A]): List[A] = fileLine match {
      case newline(_) =>
        if (!ctx.hasNext && logLine != null) func(logLine) :: func(fileLine) :: acc
        else if (!ctx.hasNext) func(fileLine) :: acc
        else if (logLine == null) inner(ctx.next, fileLine, acc)
        else inner(ctx.next, fileLine, func(logLine) :: acc)
      case comment(_) =>
        if (!ctx.hasNext) acc
        else inner(ctx.next, logLine, acc)
      case _ =>
        if (!ctx.hasNext && logLine != null) func(logLine + "\n" + fileLine) :: acc
        else if (!ctx.hasNext) acc
        else if (logLine == null) inner(ctx.next, logLine, acc)
        else inner(ctx.next, (logLine + "\n" + fileLine), acc)
    }
    ensure(ctx.hasNext)(inner(ctx.next, null, Nil), Nil)
  }

}
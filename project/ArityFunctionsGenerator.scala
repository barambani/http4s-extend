package Templates

import sbt._

/**
  * This code is inspired by the Boilerplate generator of typelevel.cats. It can be found at the following link
  *
  * https://github.com/typelevel/cats/blob/master/project/Boilerplate.scala
 */

object ArityFunctionsGenerator {

  private val templates: Seq[Template] = Seq(
    ParEffectfulArityFunctions,
    ParEffectfulAritySyntax
  )

  /**
    * Function to generate the files. It also writes them to disk
    * as side effect
    *
    * @return a sequence of generated files
    */
  def run: Int => File => Seq[File] =
    maxArity => root => templates map {
      t =>
        val file = t.moduleFile(root)
        IO.write(file, t.expandTo(maxArity))
        file
    }
}

private[Templates] trait Template {
  def moduleFile: File => File
  def expandTo: Int => String
}

private[Templates] final case class BlockExpansions(private val upToArity: Int) {

  lazy val `sym A0..An-1`       = arityRange map (n => s"A$n")
  lazy val `sym fa0..fan-1`     = arityRange map (n => s"fa$n")
  lazy val `sym a0..an-1`       = arityRange map (n => s"a$n")
  lazy val `sym F[A0]..F[An-1]` = arityRange map (n => s"F[A$n]")

  lazy val arityS = upToArity.toString

  lazy val `A0..An-1`   = `sym A0..An-1` mkString ", "
  lazy val `a0..an-1`   = `sym a0..an-1` mkString ", "
  lazy val `fa0..fan-1` = `sym fa0..fan-1` mkString ", "

  lazy val `fa0: =>F[A0]..fan-1: =>F[An-1]` = `sym fa0..fan-1` zip `sym F[A0]..F[An-1]` map { case (fa, f) => s"$fa: =>$f" } mkString ", "
  lazy val `(a0..(a1..(an-2, an-1)`         = rightAssociativeExpansionOf(`sym a0..an-1`)("")

  def rightAssociativeExpansionOf: Seq[String] => String => String =
    symbols => prefix =>
      if(symbols.size <= 2) ""
      else symbols.dropRight(2).foldRight(s"(${symbols.dropRight(1).last}, ${symbols.last})")((an, exp) => s"($an, $prefix$exp)")

  private def arityRange: Range = 0 until upToArity
}

private[Templates] object ContentHelpers {

  import scala.StringContext._

  implicit final class StringHelpers(val sc: StringContext) extends AnyVal {

    def static(args: String*): String =
      trimLines(args) mkString "\n"

    private def trimLines(args: Seq[String]): Array[String] = {

      val interpolated = sc.standardInterpolator(treatEscapes, args)
      val rawLines = interpolated split '\n'

      rawLines map {
        _ dropWhile (_.isWhitespace)
      }
    }
  }

  implicit final class FunctionHelpers(val f: Int => String) extends AnyVal {
    def expandTo(maxArity: Int, skip: Int): String =
      (1 + skip to maxArity) map f mkString "\n"
  }
}

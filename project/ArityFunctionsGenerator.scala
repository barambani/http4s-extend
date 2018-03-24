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
    arity => root => templates map {
      t =>
        val file = t.moduleFile(root)
        IO.write(file, (expand compose t.content)(arity))
        file
    }

  private def expand: String => String = identity
}

private[Templates] trait Template {
  def moduleFile: File => File
  def content: Int => String
}

private[Templates] object ContentHelpers {

  import scala.StringContext._

  implicit final class Static(val sc: StringContext) extends AnyVal {
    def static(args: Any*): String = {
      val interpolated = sc.standardInterpolator(treatEscapes, args)
      val rawLines = interpolated split '\n'
      val trimmedLines = rawLines map { _ dropWhile (_.isWhitespace) }
      trimmedLines mkString "\n"
    }
  }
}

package Templates

import sbt._
import ContentHelpers._

private[Templates] object ParEffectfulArityFunctions extends Template {
  def moduleFile: File => File =
    _ / "ParEffectfulArityFunctions.scala"

  def content: Int => String =
    arity => {

      val top =
        static"""package http4s.extend
          |
          |private[extend] trait ParEffectfulArityFunctions {
          |
          |  def parMap2[F[_], A1, A2, R](fa: =>F[A1], fb: =>F[A2])(f: (A1, A2) => R)(implicit ev: ParEffectful[F]): F[R] =
          |    ev.parMap2(fa, fb)(f)
          |
          |  def parTupled2[F[_], A1, A2](fa: =>F[A1], fb: =>F[A2])(implicit ev: ParEffectful[F]): F[(A1, A2)] =
          |    ev.parMap2(fa, fb)(Tuple2.apply)"""

      val bottom = static"""}"""

      s"""$top
         |$bottom""".stripMargin
    }
}
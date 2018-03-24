package Templates

import sbt._
import ContentHelpers._

private[Templates] object ParEffectfulArityFunctions extends Template {
  def moduleFile: File => File =
    _ / "ParEffectfulArityFunctions.scala"

  def expandFor: Int => String =
    arity => {

      val top =
        static"""package http4s.extend
          |
          |private[extend] trait ParEffectfulArityFunctions {
          |
          |  def parMap2[F[_], A1, A2, R](fa1: =>F[A1], fa2: =>F[A2])(f: (A1, A2) => R)(implicit ev: ParEffectful[F]): F[R] =
          |    ev.parMap2(fa1, fa2)(f)
          |
          |  def parTupled2[F[_], A1, A2](fa1: =>F[A1], fa2: =>F[A2])(implicit ev: ParEffectful[F]): F[(A1, A2)] =
          |    ev.parMap2(fa1, fa2)(Tuple2.apply)"""

      val bottom = static"""}"""

      val parMap =
        repeat"""def parMap4[F[_] : ParEffectful, A1, A2, A3, A4, R](fa1: =>F[A1], fa2: =>F[A2], fa3: =>F[A3], fa4: =>F[A4])(f: (A1, A2, A3, A4) => R): F[R] =
          |  ParEffectful.parMap2(fa1, ParEffectful.parTupled2(fa2, ParEffectful.parTupled2(fa3, fa4))) { case (a1, (a2, (a3, a4))) => f(a1, a2, a3, a4) }
        """(arity, 2)

      /*
      def parMap4[F[_] : ParEffectful, A1, A2, A3, A4, R](fa1: =>F[A1], fa2: =>F[A2], fa3: =>F[A3], fa4: =>F[A4])(f: (A1, A2, A3, A4) => R): F[R] =
        ParEffectful.parMap2(fa1, ParEffectful.parTupled2(fa2, ParEffectful.parTupled2(fa3, fa4))) { case (a1, (a2, (a3, a4))) => f(a1, a2, a3, a4) }

      def parTupled4[F[_] : ParEffectful, A1, A2, A3, A4](fa1: =>F[A1], fa2: =>F[A2], fa3: =>F[A3], fa4: =>F[A4]): F[(A1, A2, A3, A4)] =
        parMap4(fa1, fa2, fa3, fa4)(Tuple4.apply)
       */

      s"""$top
         |$bottom""".stripMargin
    }
}
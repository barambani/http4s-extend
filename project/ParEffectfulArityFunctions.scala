package Templates

import sbt._
import ContentHelpers._

private[Templates] object ParEffectfulArityFunctions extends Template {

  def moduleFile: File => File =
    _ / "ParEffectfulArityFunctions.scala"

  def expandTo: Int => String =
    maxArity => {

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

      def repeatedBlock: Int => String =
        arity => {

          val expansion = BlockExpansions(arity)
          import expansion._

          lazy val `(a0..ParEffectful.parTupled2(a1..ParEffectful.parTupled2(an-2, an-1)` =
            rightAssociativeExpansionOf(`sym fa0..fan-1`)("ParEffectful.parTupled2")

          static"""
             |  def parMap$arityS[F[_] : ParEffectful, ${`A0..An-1`}, R](${`fa0: =>F[A0]..fan-1: =>F[An-1]`})(f: (${`A0..An-1`}) => R): F[R] =
             |    ParEffectful.parMap2${`(a0..ParEffectful.parTupled2(a1..ParEffectful.parTupled2(an-2, an-1)`} { case ${`(a0..(a1..(an-2, an-1)`} => f(${`a0..an-1`}) }
             |
             |  def parTupled$arityS[F[_] : ParEffectful, ${`A0..An-1`}](${`fa0: =>F[A0]..fan-1: =>F[An-1]`}): F[(${`A0..An-1`})] =
             |    parMap${arity.toString}(${`fa0..fan-1`})(Tuple$arityS.apply)""".stripMargin
        }

      val dynamic = repeatedBlock.expandTo(maxArity, 2)

      val bottom = static"""}"""

      s"""$top
         |$dynamic
         |$bottom""".stripMargin
    }
}
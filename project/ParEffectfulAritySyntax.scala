package Templates

import Templates.BlockSyntax._
import sbt._

private[Templates] object ParEffectfulAritySyntax extends Template {
  def moduleFile: File => File =
    _ / "syntax" / "ParEffectfulAritySyntax.scala"

  def expandTo: Int => String =
    maxArity => {

      val syntaxTraitTop =
        static"""package http4s.extend.syntax
          |
          |import http4s.extend.ParEffectful
          |
          |private[syntax] trait ParEffectfulAritySyntax {"""

      def syntaxArityBlock: Int => String =
        arity => {

          val expansion = BlockMembersExpansions(arity)
          import expansion._

          static"""|  implicit def parEffectfulSyntax$arityS[F[_], ${`A0..An-1`}](t$arityS: (${`F[A0]..F[An-1]`})) = new Tuple${arityS}ParEffectfulOps(t$arityS)""".stripMargin
        }

      val syntaxTraitBottom = static"""}"""

      def opsArityBlock: Int => String =
        arity => {

          val expansion = BlockMembersExpansions(arity)
          import expansion._

          lazy val `t._1..t._n` =
            List.fill(arity)(s"t$arityS") zip `sym _1.._n` map { case (t, n) => s"$t.$n" } mkString ", "

          static"""
            |private[syntax] final class Tuple${arityS}ParEffectfulOps[F[_], ${`A0..An-1`}](t$arityS: (${`F[A0]..F[An-1]`})) {
            |  def parMap[R](f: (${`A0..An-1`}) => R)(implicit F: ParEffectful[F]): F[R] = ParEffectful.parMap$arityS(${`t._1..t._n`})(f)
            |  def parTupled(implicit F: ParEffectful[F]): F[(${`A0..An-1`})] = ParEffectful.parTupled$arityS(${`t._1..t._n`})
            |}""".stripMargin
        }

      s"""$syntaxTraitTop
         |${syntaxArityBlock.expandedTo(maxArity, skip = 1)}
         |$syntaxTraitBottom
         |${opsArityBlock.expandedTo(maxArity, skip = 1)}""".stripMargin
    }
}

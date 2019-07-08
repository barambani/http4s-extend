package BoilerplateGeneration

import BoilerplateGeneration.BlockSyntax._
import sbt._

private[BoilerplateGeneration] object ParallelEffectSyntaxAccumulateErrorTests extends Template {

  def moduleFile: File => File =
    _ / "syntax" / "ParallelEffectSyntaxAccumulateErrorTests.scala"

  def expandTo: Int => String =
    maxArity => {

      val staticTop =
        static"""package http4s.extend.test
          |
          |import cats.effect.IO
          |import cats.instances.unit._
          |import cats.laws._
          |import cats.laws.discipline._
          |import cats.syntax.semigroup._
          |import http4s.extend.syntax.parallelEffect._
          |import org.scalacheck.Prop
          |
          |import scala.concurrent.duration._
          |
          |final class ParallelEffectSyntaxAccumulateErrorTests extends Fixtures.MinimalSuite {
          |
          |  val timeout = 1.seconds"""

      val staticBottom = static"""}"""

      def testArityBlock: Int => String =
        arity => {

          val expansion = BlockMembersExpansions(arity)
          import expansion._

          lazy val `sym e0..en-1` = arityRange map (n => s"e$n")

          lazy val `e0: Throwable..en-1: Throwable` =
            `sym e0..en-1` map (e => s"$e: Throwable") mkString ", "

          lazy val `ioEff.fail[Int](e0)..ioEff.fail[Int](en-1)` =
            `sym e0..en-1` map (e => s"IO.raiseError[Int]($e)") mkString ", "

          lazy val `scalazTaskEff.fail[Int](e0)..scalazTaskEff.fail[Int](en-1)` =
            `sym e0..en-1` map (e => s"ScalazTask.fail[Int]($e)") mkString ", "

          lazy val `(e1 combine e2) ... combine en-1` =
            leftAssociativeExpansionOf(`sym e0..en-1`)("")(" combine ")

          lazy val `(_: Int, ... , _: Int)` =
            `sym _, ... , _` map (s => s"$s: Int") mkString ("(", ", ", ")")

          static"""
            |  test("$arityS io errors are accumulated by parallelMap") {
            |    Prop.forAll { (${`e0: Throwable..en-1: Throwable`}) => {
            |      (${`ioEff.fail[Int](e0)..ioEff.fail[Int](en-1)`}).parallelMap(timeout){ ${`(_, ... , _)`} => () } <-> IO.raiseError[Unit]${`(e1 combine e2) ... combine en-1`}
            |    }}
            |  }""".stripMargin
        }

      s"""$staticTop
         |${testArityBlock.expandedTo(maxArity, skip = 1)}
         |$staticBottom""".stripMargin
    }
}

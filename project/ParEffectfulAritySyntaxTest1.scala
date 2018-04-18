package Templates

import Templates.BlockSyntax._
import sbt._

private[Templates] object ParEffectfulAritySyntaxTest1 extends Template {

  def moduleFile: File => File =
    _ / "syntax" / "ParEffectfulSyntaxAccumulateErrorTests.scala"

  def expandTo: Int => String =
    maxArity => {

      val staticTop =
        static"""package http4s.extend.test
          |
          |import cats.Semigroup
          |import cats.effect.IO
          |import cats.effect.laws.util.TestContext
          |import cats.effect.util.CompositeException
          |import cats.instances.unit._
          |import cats.laws._
          |import cats.laws.discipline._
          |import cats.syntax.semigroup._
          |import http4s.extend.Effectful
          |import http4s.extend.syntax.parEffectful._
          |import http4s.extend.test.Fixtures.MinimalSuite
          |import org.scalacheck.Prop
          |import scalaz.concurrent.{Task => ScalazTask}
          |
          |final class ParEffectfulSyntaxAccumulateErrorTests extends MinimalSuite {
          |
          |  implicit val C = TestContext()
          |
          |  implicit def throwableSemigroup: Semigroup[Throwable] =
          |    new Semigroup[Throwable]{
          |      def combine(x: Throwable, y: Throwable): Throwable =
          |        CompositeException(x, y, Nil)
          |    }
          |
          |  def ioEff = Effectful[Throwable, IO]
          |  def scalazTaskEff = Effectful[Throwable, ScalazTask]"""

      val staticBottom = static"""}"""

      def testArityBlock: Int => String =
        arity => {

          val expansion = BlockMembersExpansions(arity)
          import expansion._

          lazy val `sym e0..en-1` = arityRange map (n => s"e$n")

          lazy val `e0: Throwable..en-1: Throwable` =
            `sym e0..en-1` map (e => s"$e: Throwable") mkString ", "

          lazy val `ioEff.fail[Int](e0)..ioEff.fail[Int](en-1)` =
            `sym e0..en-1` map (e => s"ioEff.fail[Int]($e)") mkString ", "

          lazy val `scalazTaskEff.fail[Int](e0)..scalazTaskEff.fail[Int](en-1)` =
            `sym e0..en-1` map (e => s"scalazTaskEff.fail[Int]($e)") mkString ", "

          lazy val `(e1 combine e2) ... combine en-1` =
            leftAssociativeExpansionOf(`sym e0..en-1`)("")(" combine ")

          static"""
            |  test("$arityS io errors are accumulated by parMap") {
            |    Prop.forAll { (${`e0: Throwable..en-1: Throwable`}) => {
            |      (${`ioEff.fail[Int](e0)..ioEff.fail[Int](en-1)`}).parMap{ ${`(_, ... , _)`} => () } <-> ioEff.fail[Unit](${`(e1 combine e2) ... combine en-1`})
            |    }}
            |  }
            |
            |  test("$arityS scalaz task errors are accumulated by parMap") {
            |    Prop.forAll { (${`e0: Throwable..en-1: Throwable`}) => {
            |      (${`scalazTaskEff.fail[Int](e0)..scalazTaskEff.fail[Int](en-1)`}).parMap{ ${`(_, ... , _)`} => () } <-> scalazTaskEff.fail[Unit](${`(e1 combine e2) ... combine en-1`})
            |    }}
            |  }""".stripMargin
        }

      s"""$staticTop
         |${testArityBlock.expandedTo(maxArity, skip = 1)}
         |$staticBottom""".stripMargin
    }
}

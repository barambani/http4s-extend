package http4s.extend.test

import cats.effect.IO
import cats.effect.laws.discipline.arbitrary._
import cats.instances.double._
import cats.instances.either._
import cats.instances.int._
import cats.instances.string._
import cats.instances.unit._
import cats.laws._
import cats.laws.discipline._
import cats.{Eq, MonadError}
import http4s.extend.test.Fixtures.MinimalSuite
import http4s.extend.test.laws.checks.EffectfulLawsChecks
import http4s.extend.util.ThrowableInstances
import http4s.extend.{Effectful, ExceptionDisplay}
import org.scalacheck.Arbitrary.arbDouble
import org.scalacheck.{Arbitrary, Prop}

final class EffectfulDiscipline extends MinimalSuite with ThrowableInstances {

  effectfulDisciplineOf[IO]("IO")

  private def effectfulDisciplineOf[F[_]](effectName: String)(
    implicit
      ev1: Effectful[ExceptionDisplay, F],
      ev2: Effectful[Throwable, F],
      ev4: MonadError[F, Throwable],
      ev5: MonadError[F, ExceptionDisplay],
      AFD: Arbitrary[F[Double]],
      AFI: Arbitrary[F[Int]],
      EFD: Eq[F[Double]],
      EFI: Eq[F[Int]],
      EFU: Eq[F[Unit]],
      EFS: Eq[F[String]],
      EED: Eq[F[Either[ExceptionDisplay, Double]]],
      EEI: Eq[F[Either[Throwable, Int]]]) = {

    checkAll(
      s"Effectful[ExceptionDisplay, $effectName]",
      EffectfulLawsChecks[ExceptionDisplay, F].effectful[Double]
    )

    checkAll(
      s"Effectful[Throwable, $effectName]",
      EffectfulLawsChecks[Throwable, F].effectful[Int]
    )

    test(s"Effectful[Throwable, $effectName]: throw a Throwable in delay is raiseError"){
      Prop.forAll {
        e: Throwable =>
          Effectful[Throwable, F].delay[Int](throw e) <-> MonadError[F, Throwable].raiseError[Int](e)
      }
    }

    test(s"Effectful[Throwable, $effectName]: throw a Throwable in suspend is raiseError"){
      Prop.forAll {
        e: Throwable =>
          Effectful[Throwable, F].suspend[String](throw e) <-> MonadError[F, Throwable].raiseError[String](e)
      }
    }

    test(s"Effectful[Throwable, $effectName]: propagate errors through bind (suspend)"){
      Prop.forAll {
        e: Throwable => {

          val monadError = MonadError[F, Throwable]
          val fa = monadError.flatMap(Effectful[Throwable, F].delay[Double](throw e))(x => monadError.pure[Double](x))

          fa <-> monadError.raiseError[Double](e)
        }
      }
    }
  }
}
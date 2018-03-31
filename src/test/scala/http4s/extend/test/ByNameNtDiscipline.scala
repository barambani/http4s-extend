//package http4s.extend.test
//
//import cats.effect.IO
//import cats.effect.laws.discipline.ConcurrentEffectTests
//import cats.effect.laws.discipline.arbitrary._
//import cats.effect.laws.util.TestContext
//import cats.instances.double._
//import cats.instances.either._
//import cats.instances.int._
//import cats.instances.string._
//import cats.instances.tuple._
//import cats.instances.unit._
//import http4s.extend.ByNameNt._
//import http4s.extend.test.Fixtures.MinimalSuite
//import org.scalacheck.Arbitrary.arbDouble
//
//import scala.concurrent.Future
//
//
//
//final class ByNameNtDiscipline  extends MinimalSuite {
//
//  implicit val C = TestContext()
//
////  def bbbb: ScalazTask[Int] = ???
////  def cccc: MonixTask[Double] = ???
//
//  {
//    def aaaa[A](): Future[A] = ???
//    implicit def io[A](implicit ev: Future ~~> IO): IO[A] = ev(aaaa())
//
//    checkAll(
//      "ConcurrentEffect[IO]",
//      ConcurrentEffectTests[IO].concurrentEffect[String, Int, Double]
//    )
//  }
//}

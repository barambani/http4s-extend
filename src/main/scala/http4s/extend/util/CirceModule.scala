package http4s.extend.util

import cats.syntax.either._
import io.circe.{Decoder, Encoder}

object CirceModule {

  def stringEncoder[A](f: A => String): Encoder[A] =
    Encoder.encodeString.contramap[A](f)

  def stringDecoder[A, B](f: String => A): Decoder[A] =
    stringDecoderMap(f)(identity)

  def stringDecoderMap[A, B](f: String => A)(g: A => B): Decoder[B] =
    Decoder.decodeString emap {
      str => Either.catchNonFatal(f(str)) leftMap (_ => s"Cannot parse $str to Long") map g
    }
}

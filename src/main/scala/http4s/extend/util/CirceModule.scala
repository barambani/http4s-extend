package http4s.extend.util

import cats.Show
import cats.syntax.either._
import io.circe.{Decoder, Encoder}

object CirceModule {

  /**
    * Gives a Circe Encoder for the type `A` when a `Show` instance
    * is available for it
    *
    * @return An encoder for `A`
    */
  def encoderFor[A](implicit ev: Show[A]): Encoder[A] =
    Encoder.encodeString.contramap[A](ev.show)

  /**
    * Gives a Circe Decoder for the type `A` when a way to go from String to `A`
    * is provided
    *
    * @return A decoder for `A`
    */
  def decoderFor[A]: (String => A) => Decoder[A] =
    f => mappedDecoderFor(f)(identity)

  /**
    * Gives a Circe Decoder for `A` that maps the successful decoded value with `f`
    *
    * @return A decoder for `A` that maps the result to `B` in case of successful decoding
    */
  def mappedDecoderFor[A, B]: (String => A) => (A => B) => Decoder[B] =
    ff => f => Decoder.decodeString emap {
      str => Either.catchNonFatal[A](ff(str)) leftMap (_ => s"Cannot parse $str to Long") map f
    }
}
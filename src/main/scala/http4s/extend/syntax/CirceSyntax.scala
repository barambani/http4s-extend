package http4s.extend.syntax

import http4s.extend.util.CirceModule._
import io.circe.{Decoder, Encoder}

trait CirceSyntax {
  implicit def circeEncoderSyntax[A](f: A => String): StringEncodingOps[A] = new StringEncodingOps(f)
  implicit def circeDecoderSyntax[A](f: String => A): StringDecodingOps[A] = new StringDecodingOps(f)
}

private[syntax] final class StringEncodingOps[A](f: A => String) {
  def encoder: Encoder[A] = encoderFor(f)
}

private[syntax] final class StringDecodingOps[A](f: String => A) {
  def decoder: Decoder[A] = decoderFor(f)
  def decoderMap[B](g: A => B): Decoder[B] = decoderMapFor(f)(g)
}
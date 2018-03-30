package http4s.extend.syntax

import http4s.extend.util.CirceModule._
import io.circe.Decoder

private[syntax] trait CirceSyntax {
  implicit def circeDecoderSyntax[A](f: String => A): StringDecodingOps[A] = new StringDecodingOps(f)
}

private[syntax] final class StringDecodingOps[A](f: String => A) {
  def decoder: Decoder[A] = decoderFor(f)
  def decoderMap[B](g: A => B): Decoder[B] = mappedDecoderFor(f)(g)
}
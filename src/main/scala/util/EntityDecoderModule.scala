package http4s.extend.util

import http4s.extend.ErrorConversion
import http4s.extend.instances.SyncInstances._
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object EntityDecoderModule {

  def eitherEntityDecoder[E : ErrorConversion[Throwable, ?], A : Decoder]: EntityDecoder[Either[E, ?], A] =
    jsonOf[Either[E, ?], A]
}

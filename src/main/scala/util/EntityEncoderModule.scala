package http4s.extend.util

import http4s.extend.ErrorConversion
import http4s.extend.instances.SyncInstances._
import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object EntityEncoderModule {

  def eitherEntityEncoder[E : ErrorConversion[Throwable, ?], A : Encoder]: EntityEncoder[Either[E, ?], A] =
    jsonEncoderOf[Either[E, ?], A]
}

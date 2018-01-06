package com.gilt.lib.util

import com.gilt.lib.ErrorConversion
import com.gilt.lib.instances.SyncInstances._
import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

object EntityEncoderModule {

  def eitherEntityEncoder[E : ErrorConversion[Throwable, ?], A : Encoder]: EntityEncoder[Either[E, ?], A] =
    jsonEncoderOf[Either[E, ?], A]
}

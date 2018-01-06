package com.gilt.lib.util

import com.gilt.lib.ErrorConversion
import com.gilt.lib.instances.SyncInstances._
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object EntityDecoderModule {

  def eitherEntityDecoder[E, A : Decoder](implicit EC: ErrorConversion[Throwable, E]): EntityDecoder[Either[E, ?], A] =
    jsonOf[Either[E, ?], A]
}
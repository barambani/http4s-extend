package http4s.extend.syntax

import http4s.extend.util.MonixTaskModule
import monix.eval.Task

trait MonixTaskModuleSyntax {

  implicit final class TaskModuleOps[A](aTask: => Task[A]) {

    def adaptError[E](errM: Throwable => E): Task[Either[E, A]] =
      MonixTaskModule.adaptError(aTask)(errM)
  }
}

object MonixTaskModuleSyntax extends MonixTaskModuleSyntax

package http4s.extend.test.types

import http4s.extend._
import http4s.extend.test.Fixtures.MinimalSuite

final class AndBooleanDiscipline extends MinimalSuite {

  test("typed AND correct evaluation"){

    import AndBoolean._

    (mk(true)   =&&= mk(true))  should be(mk(true))
    (mk(true)   =&&= mk(false)) should be(mk(false))
    (mk(false)  =&&= mk(true))  should be(mk(false))
    (mk(false)  =&&= mk(false)) should be(mk(false))
  }
}

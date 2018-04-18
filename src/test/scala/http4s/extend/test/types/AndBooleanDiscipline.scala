package http4s.extend.test.types

import http4s.extend._
import http4s.extend.test.Fixtures.MinimalSuite

final class AndBooleanDiscipline extends MinimalSuite {

  test("typed AND correct evaluation"){
    (AndBoolean(true)   =&&= AndBoolean(true))  should be(AndBoolean(true))
    (AndBoolean(true)   =&&= AndBoolean(false)) should be(AndBoolean(false))
    (AndBoolean(false)  =&&= AndBoolean(true))  should be(AndBoolean(false))
    (AndBoolean(false)  =&&= AndBoolean(false)) should be(AndBoolean(false))
  }
}

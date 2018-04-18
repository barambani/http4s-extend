package http4s.extend.test.types

import http4s.extend.OrBoolean
import http4s.extend.test.Fixtures.MinimalSuite

final class OrBooleanDiscipline extends MinimalSuite {

  test("typed AND correct evaluation"){
    (OrBoolean(true)  =||= OrBoolean(true))  should be(OrBoolean(true))
    (OrBoolean(true)  =||= OrBoolean(false)) should be(OrBoolean(false))
    (OrBoolean(false) =||= OrBoolean(true))  should be(OrBoolean(false))
    (OrBoolean(false) =||= OrBoolean(false)) should be(OrBoolean(false))
  }
}

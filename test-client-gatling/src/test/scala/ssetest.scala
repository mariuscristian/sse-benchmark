import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration.DurationInt

class ssetest extends Simulation {

  val httpProtocol = http.baseUrl("http://localhost:9000")

  val scn = scenario("ServerSentEvents")
    .exec(
      sse("GET messages")
        .connect("/stream/date")
        .await(1)(
          sse.checkMessage("ConnectionCheck").matching(substring("connection established"))
            .check(bodyString.saveAs("InitialMessage"))
        )
    )
    .exec(
      sse("SetCheck").setCheck
        .await(20.seconds)(
          sse.checkMessage("checkMessage")
            .matching(substring("Flux"))
            .check(bodyString.saveAs("PostedMessage"))
        )
    )
    .pause(5)
    .exec(sse("Close").close)


  setUp(scn.inject(rampUsers(10).during(10.seconds))).protocols(httpProtocol)

}
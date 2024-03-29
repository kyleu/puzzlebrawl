package utils.web

import com.codahale.metrics.Meter
import play.api.http.Status
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import utils.Logging
import utils.metrics.Instrumented

import scala.concurrent.Future

object PlayLoggingFilter extends Filter with Logging with Instrumented {
  val prefix = "puzzlebrawl.requests."

  val knownStatuses = Seq(
    Status.OK, Status.BAD_REQUEST, Status.FORBIDDEN, Status.NOT_FOUND,
    Status.CREATED, Status.TEMPORARY_REDIRECT, Status.INTERNAL_SERVER_ERROR, Status.CONFLICT,
    Status.UNAUTHORIZED, Status.NOT_MODIFIED
  )

  lazy val statusCodes: Map[Int, Meter] = knownStatuses.map(s => s -> metricRegistry.meter(prefix + s.toString)).toMap

  lazy val requestsTimer = metricRegistry.timer(s"${prefix}requestTimer")
  lazy val activeRequests = metricRegistry.counter(s"${prefix}activeRequests")
  lazy val otherStatuses = metricRegistry.meter(s"${prefix}other")

  def apply(nextFilter: (RequestHeader) => Future[Result])(request: RequestHeader): Future[Result] = {
    val startTime = System.nanoTime
    val context = requestsTimer.time()
    activeRequests.inc()

    def logCompleted(request: RequestHeader, result: Result): Unit = {
      activeRequests.dec()
      context.stop()
      statusCodes.getOrElse(result.header.status, otherStatuses).mark()
    }

    nextFilter(request).transform(
      result => {
        logCompleted(request, result)
        if (request.path.startsWith("/assets")) {
          result
        } else {
          val requestTime = (System.nanoTime - startTime) / 1000000
          log.info(s"${result.header.status} (${requestTime}ms): ${request.method} ${request.uri}")
          result.withHeaders("X-Request-Time-Ms" -> requestTime.toString)
        }
      },
      exception => {
        logCompleted(request, Results.InternalServerError)
        exception
      }
    )
  }
}

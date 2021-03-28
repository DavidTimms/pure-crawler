import cats.effect._
import org.http4s._
import org.http4s.implicits._
import org.http4s.client.blaze._
import org.http4s.client._
import scala.concurrent.ExecutionContext.global

case class Crawler() {
  def crawl(pageUri: Uri): IO[WebPage] =
    BlazeClientBuilder[IO](global).resource.use { httpClient =>
      httpClient.expect[String](pageUri).map(WebPage.fromHtml)
    }
}

import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.blaze._
import org.http4s.client.middleware.FollowRedirect

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._

case class Crawler(maxDepth: Int) {
  def crawl(startingUri: String): IO[WebPage] = {
    for {
      parsedUri <- Uri.fromString(startingUri) match {
        case Left(_) => IO.raiseError(new Error("Invalid starting URI"))
        case Right(uri) => IO.pure(uri)
      }
      page <- clientBuilder.resource.use { httpClient =>
        val httpClientWithRedirects = FollowRedirect(maxRedirects = 5)(httpClient)
        crawlPage(httpClientWithRedirects, parsedUri)
      }
    } yield page
  }

  private def clientBuilder: BlazeClientBuilder[IO] =
      BlazeClientBuilder[IO](global)
        .withConnectTimeout(Duration(2, SECONDS))
        .withRequestTimeout(Duration(5, SECONDS))
        .withIdleTimeout(Duration(2, SECONDS))

  private def crawlPage(httpClient: Client[IO], pageUri: Uri, depth: Int = 0): IO[WebPage] = {
    for {
      // _ <- IO.println(s"requesting: $pageUri")
      html <- httpClient.expect[String](pageUri)
      // _ <- IO.println(s"received: $pageUri")
      page = WebPage.fromHtml(pageUri, html)
      _ <- IO.println("  ".repeat(depth) + "- " + page.title.getOrElse("(no title)"))
      links = crawlableLinks(page)
      _ <- {
        if (depth < maxDepth)
          links.parTraverse { uri => crawlPage(httpClient, uri, depth + 1).attempt }
        else
          IO.unit
      }
    } yield page
  }

  private def crawlableLinks(webPage: WebPage): List[Uri] =
    webPage
      .links
      .map(Uri.fromString)
      // Skip any links which failed to parse:
      .flatMap(_.toSeq)
      // Skip any links which failed to parse:
      .map(Uri.resolve(webPage.uri, _))

}

import cats.implicits._
import cats.effect._
import org.http4s._
import org.http4s.client.Client
import org.http4s.client.blaze._

import scala.concurrent.ExecutionContext.global

case class Crawler(maxDepth: Int) {
  def crawl(startingUri: String): IO[WebPage] = {
    for {
      parsedUri <- Uri.fromString(startingUri) match {
        case Left(_) => IO.raiseError(new Error("Invalid starting URI"))
        case Right(uri) => IO.pure(uri)
      }
      page <- BlazeClientBuilder[IO](global).resource.use { httpClient =>
        crawlPage(httpClient, parsedUri)
      }
    } yield page
  }

  def crawlPage(httpClient: Client[IO], pageUri: Uri, depth: Int = 0): IO[WebPage] = {
    // TODO follow redirects
    // TODO handle failed requests
    for {
      page <- httpClient.expect[String](pageUri).map(WebPage.fromHtml)
      _ <- IO.println("  ".repeat(depth) + "- " + page.title.getOrElse("(no title)"))
      links = crawlableLinks(page)
      _ <- {
        if (depth < maxDepth)
          links.parTraverse { uri => crawlPage(httpClient, uri, depth + 1) }
        else
          IO.unit
      }

    } yield page
  }

  private def crawlableLinks(webPage: WebPage): List[Uri] =
    webPage
      .links
      .map(Uri.fromString)
      .flatMap(_.toSeq)
      // TODO convert relative links to absolute
      .filter(uri => uri.scheme.isDefined)
}

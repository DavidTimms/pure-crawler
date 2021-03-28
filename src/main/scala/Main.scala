import cats.implicits._
import cats.effect._
import org.http4s._

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    args.headOption match {
      case Some(startingUri) =>
        for {
          parsedUri <- Uri.fromString(startingUri) match {
            case Left(parseFailure) => IO.raiseError(new Error("Invalid starting URI"))
            case Right(uri) => IO.pure(uri)
          }
          webPage <- Crawler().crawl(parsedUri)
          _ <- IO.println(webPage)
        } yield ExitCode.Success
      case None =>
        IO(Console.err.println("ERROR: No starting URI provided"))
          .as(ExitCode.Error)
    }
  }
}

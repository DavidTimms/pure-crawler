import cats.implicits._
import cats.effect._
import org.http4s._

object Main extends IOApp {
  // TODO take max-depth as an argument
  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(startingUri) =>
        Crawler(maxDepth = 2).crawl(startingUri)
          .as(ExitCode.Success)
      case None =>
        IO(Console.err.println("ERROR: No starting URI provided"))
          .as(ExitCode.Error)
    }
}

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    args.headOption match {
      case Some(startingUrl) =>
        IO.println(s"Crawling URL: $startingUrl")
          .as(ExitCode.Success)
      case None =>
        IO(Console.err.println("ERROR: No starting URL provided"))
          .as(ExitCode.Error)
    }
  }
}

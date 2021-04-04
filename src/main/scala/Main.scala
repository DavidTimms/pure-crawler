import cats.implicits._
import cats.effect._
import org.http4s._

object Main extends IOApp {
  case class Params(maxDepth: Int)

  val DefaultParams = Params(
    maxDepth = 2
  )

  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(startingUri) => {
        val params = parseParams(args.tail)
        for {
          pageCounter <- Ref.of[IO, Int](0)
          _ <- Crawler(
          maxDepth = params.maxDepth,
          pageCounter = pageCounter
          ).crawl(startingUri)

          totalPagesCrawled <- pageCounter.get
          _ <- IO.println(s"\nSuccessfully crawled $totalPagesCrawled pages.\n")
        } yield ExitCode.Success
      }
      case None =>
        IO(Console.err.println("ERROR: No starting URI provided"))
          .as(ExitCode.Error)
    }

  def parseParams(args: List[String], params: Params = DefaultParams): Params =
    args match {
      case "--max-depth" :: value :: tail =>
        parseParams(
          tail,
          params.copy(maxDepth = value.toIntOption.getOrElse(params.maxDepth))
        )
      case _ :: tail =>
        parseParams(tail, params)
      case Nil =>
        params
    }
}
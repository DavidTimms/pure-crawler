case class WebPage(title: Option[String], links: List[String])

object WebPage {
  def fromHtml(html: String): WebPage =
    WebPage(extractTitle(html), extractLinks(html))

  private val TitleRegex = """<\s*title[^>]*>(.*)<\/\s*title\s*>""".r

  private def extractTitle(html: String): Option[String] =
    TitleRegex.findFirstMatchIn(html).map(_.group(1))

  private val LinkRegex = """<\s*a[^>]*href=("[^"]+"|'[^']+')[^>]*>""".r

  private def extractLinks(html: String): List[String] =
    LinkRegex.findAllMatchIn(html)
      .map(_.group(1))
      .map(link => link.slice(1, link.length - 1))
      .toList
}

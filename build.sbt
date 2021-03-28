ThisBuild / scalaVersion := "2.13.3"

lazy val pureCrawler = (project in file("."))
  .settings(
    name := "Pure Crawler",
    libraryDependencies ++= Seq(
//      "org.scalatest"              %% "scalatest"                % "3.2.6"  % Test,
      "org.typelevel"              %% "cats-core"                % "2.4.2",
      "org.typelevel"              %% "cats-effect"              % "3.0.0-RC3",
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps",
      "-language:higherKinds")
  )

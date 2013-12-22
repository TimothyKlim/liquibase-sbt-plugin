import sbt._
import sbt.Keys._

object PluginBuild extends Build {
  val Organization = "com.github.timothyklim"
  val Version = "0.1-SNAPSHOT"
  val ScalaVersion = "2.10.3"

  val appName = "liquibase-sbt-plugin"

  lazy val root = Project(appName, file("."), settings = defaultSettings)

  lazy val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    sbtPlugin := true,
    crossPaths := false,
    organizationName := "com.github.timothyklim",
    organizationHomepage := Some(url("https://github.com/timothyklim/liquibase-sbt-plugin")),
    libraryDependencies ++= Seq(
      "org.liquibase" % "liquibase-core" % "3.0.7"
    ),
    publishTo := Some(Resolver.file("timothyklim.github.com", file(Path.userHome + "/Development/timothyklim.github.com/repo")))
  )

  lazy val defaultSettings = buildSettings ++ Seq(
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")
  )
}

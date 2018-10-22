scalaVersion in ThisBuild := "2.12.3"
organization in ThisBuild := "xawd"

lazy val senjinn = (project in file("."))
    .settings(
        name := "Senjinn",
        libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    )

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"
val catsVersion = "2.10.0" //"2.9.0"
lazy val root = (project in file("."))
  .settings(
    name := "cats-by-example",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion
    )
  )

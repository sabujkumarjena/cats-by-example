ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"
val catsVersion = "2.10.0" //"2.9.0"
val scalaTestVersion = "3.2.18"
lazy val root = (project in file("."))
  .settings(
    name := "cats-by-example",
    resolvers ++= Seq(
      "Artima Maven Repository" at "https://repo.artima.com/releases"
    ),
   // addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.3" cross CrossVersion.full),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,


      // Small set of basic type classes
      "org.typelevel" %% "cats-kernel" % catsVersion,
      //  Laws for testing type class instances.
      "org.typelevel" %% "cats-laws" % catsVersion,
      //Free structures such as the free monad, and supporting type classes.
      "org.typelevel" %% "cats-free" % catsVersion,
      //lib for writing tests for type class instances using laws.
      "org.typelevel" %% "cats-testkit" % catsVersion,
      // Type classes to represent algebraic structures.
      "org.typelevel" %% "algebra" % catsVersion,
      //  Cats instances and classes which are not lawful.
      "org.typelevel" %% "alleycats-core" % catsVersion,

      "org.typelevel" %% "discipline-core" % "1.7.0", //"1.5.0"
      "org.typelevel" %% "discipline-scalatest" % "2.3.0",

       "org.scalactic" %% "scalactic" % scalaTestVersion,
       "org.scalatest" %% "scalatest" % scalaTestVersion % "test"


    )
  )

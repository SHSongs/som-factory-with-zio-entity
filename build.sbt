import Dependencies._

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / version := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "som-factory",
    libraryDependencies ++= zio.all ++ cats.all ++
      Seq(
        "io.suzaku" %% "boopickle" % "1.4.0"
      ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

addCompilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)

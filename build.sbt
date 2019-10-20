import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val projectSettings = Seq(
  name := "number-series-solver",
  organization := "com.github.mkroli",
  scalaVersion := "2.13.1"
)

lazy val projectDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0",
    "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
    "com.github.scopt" %% "scopt" % "4.0.0-RC2"
  )
)

lazy val projectBuildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "com.github.mkroli"
)

lazy val projectAssemblySettings = Seq(
  mainClass in assembly := Some("com.github.mkroli.NumberSeriesSolverWindow")
)

lazy val projectReleaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    setNextVersion,
    commitNextVersion
  )
)

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(projectSettings)
  .settings(projectDependencies)
  .settings(projectBuildInfoSettings)
  .settings(projectAssemblySettings)

import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

lazy val projectSettings = Seq(
  name := "number-series-solver",
  organization := "com.github.mkroli",
  scalaVersion := "2.10.2"
)

lazy val projectDependencies = Seq(
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-swing" % "2.10.2",
    "com.github.scopt" %% "scopt" % "2.1.0"
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

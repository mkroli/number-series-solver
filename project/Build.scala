import sbt._
import sbt.Keys._
import sbtrelease._
import sbtrelease.ReleasePlugin._
import sbtrelease.ReleasePlugin.ReleaseKeys._
import sbtrelease.ReleaseStateTransformations._
import sbtassembly.Plugin._
import sbtassembly.Plugin.AssemblyKeys._
import sbtbuildinfo.Plugin._

object Build extends sbt.Build {
  lazy val projectSettings = Seq(
    name := "number-series-solver",
    organization := "com.github.mkroli",
    scalaVersion := "2.10.2")

  lazy val projectDependencies = Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-swing" % "2.10.2",
      "com.github.scopt" %% "scopt" % "2.1.0"))

  lazy val projectBuildInfoSettings = Seq(
    sourceGenerators in Compile <+= buildInfo,
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.github.mkroli")

  lazy val projectAssemblySettings = Seq(
    mainClass := Some("com.github.mkroli.NumberSeriesSolverWindow"))

  lazy val projectReleaseSettings = Seq(
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion))

  lazy val numberSeriesSolver = Project(
    id = "number-series-solver",
    base = file("."),
    settings = Defaults.defaultSettings ++
      projectSettings ++
      projectDependencies ++
      projectAssemblySettings ++
      assemblySettings ++
      buildInfoSettings ++
      projectBuildInfoSettings ++
      releaseSettings ++
      projectReleaseSettings)
}

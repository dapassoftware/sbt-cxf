
lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "0.1.0-SNAPSHOT",
  organization in ThisBuild := "io.dapas.sbt"
)

lazy val `sbt-cxf` = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    commonSettings,
    sbtPlugin := true,
    name := "sbt-cxf",
    description := "sbt plugin to generate CXF java classes from WSDLs",
    licenses := Seq("Apache-2.0" -> url("https://github.com/dapassoftware/sbt-cxf/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked"),
    publishArtifact in (Compile, packageBin) := true,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Compile, packageSrc) := true
  )

sbtPlugin := true
scalaVersion := "2.10.4"

organization := "io.dapas.sbt"
name := "sbt-cxf"
version := "0.1.0-SNAPSHOT"

publishMavenStyle := true
pomIncludeRepository := { _ => false }
publishArtifact in (Compile, packageBin) := true
publishArtifact in (Test, packageBin) := false
publishArtifact in (Compile, packageDoc) := false
publishArtifact in (Compile, packageSrc) := true

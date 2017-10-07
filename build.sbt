lazy val commonSettings: Seq[Setting[_]] = Seq(
  version in ThisBuild := "0.1.0-SNAPSHOT",
  organization in ThisBuild := "io.dapas.sbt"
)

lazy val `sbt-cxf` = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    commonSettings,
    publishSettings,
    name := "sbt-cxf",
    sbtPlugin := true,
    scalaVersion := "2.10.4",
    description := "SBT plugin to generate CXF java classes from WSDLs using wsdl2java",
    licenses := Seq("Apache-2.0" -> url("https://github.com/dapassoftware/sbt-cxf/blob/master/LICENSE")),
    scalacOptions := Seq("-deprecation", "-unchecked")
  )

lazy val publishSettings: Seq[Setting[_]] = Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  publishArtifact in(Compile, packageBin) := true,
  publishArtifact in(Test, packageBin) := false,
  publishArtifact in(Compile, packageDoc) := false,
  publishArtifact in(Compile, packageSrc) := true
)

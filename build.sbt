lazy val `sbt-cxf` = project in file(".")

organization := "io.dapas"
name := "sbt-cxf"
description := "SBT plugin to generate CXF java classes from WSDLs using wsdl2java"

homepage := Some(url("https://github.com/dapassoftware/sbt-cxf"))
licenses += "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0.html")
scmInfo := Some(ScmInfo(
  browseUrl = url("https://github.com/dapassoftware/sbt-cxf"),
  connection = "git@github.com:dapassoftware/sbt-cxf.git"
))
organizationName := "Dapas Software B.V."
organizationHomepage := Some(url("https://www.dapas.io/"))
developers := List(
  Developer(
    id = "barredijkstra",
    name = "Barre Dijkstra",
    email = "barre.dijkstra@dapas.io",
    url = url("https://github.com/barredijkstra")
  ),
  Developer(
    id = "nightwhistler",
    name = "Alex Kuiper",
    email = "alex.kuiper@dapas.io",
    url = url("https://github.com/nightwhistler")
  )
)

crossSbtVersions := Vector("0.13.16", "1.0.2")
sbtPlugin := true
publishMavenStyle := false
scalacOptions += "-deprecation"

val tagName = Def.setting {
  s"v${if (releaseUseGlobalVersion.value) (version in ThisBuild).value else version.value}"
}
val tagOrHash = Def.setting {
  if (isSnapshot.value)
    sys.process.Process("git rev-parse HEAD").lines_!.head
  else
    tagName.value
}

releaseTagName := tagName.value

scalacOptions in(Compile, doc) ++= {
  val tag = tagOrHash.value
  Seq(
    "-sourcepath", (baseDirectory in LocalRootProject).value.getAbsolutePath,
    "-doc-source-url", s"https://github.com/dapassoftware/sbt-cxf/tree/${tag}€{FILE_PATH}.scala"
  )
}

// Release
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  releaseStepCommandAndRemaining("^ test"),
  releaseStepCommandAndRemaining("^ scripted"),
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("^ publish"),
  releaseStepTask(bintrayRelease in `sbt-cxf`),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
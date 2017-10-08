import sbt.file

import scala.io.Source

lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.11.11",
    cxfWsdls := Seq(
      Wsdl(
        id = "simple",
        wsdlFile = (resourceDirectory in Compile).value / "wsdl" / "simple.wsdl",
        implementations = Seq(CxfImplementationType.Impl),
        pkg = "acme.simple"
      )
    ),
    cxfVersion := "3.2.0",
    TaskKey[Unit]("checkVersion") := {
      (cxfWsdls in Compile).value.foreach { wsdl =>
        val file: File = (((cxfTarget in Compile).value / wsdl.id) ** "Test.java").get.headOption
          .getOrElse(sys.error("No Java file found"))
        val buf = Source.fromFile(file)
        try {
          if (!buf.getLines().exists(_.contains("Apache CXF " + (cxfVersion in Compile).value)))
            sys.error(file.getAbsolutePath + " does not contain version " + (cxfVersion in Compile).value)
        } finally {
          buf.close()
        }
        ()
      }
    }
  )
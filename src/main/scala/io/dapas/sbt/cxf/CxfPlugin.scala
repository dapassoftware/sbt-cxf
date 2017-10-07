package io.dapas.sbt.cxf

import sbt.Keys.{ivyConfigurations, _}
import sbt.{Def, _}

import scala.sys.process.ProcessLogger

object CxfPlugin extends sbt.AutoPlugin {

  override def requires = sbt.plugins.JvmPlugin

  override def trigger = allRequirements

  object autoImport {
    lazy val CxfConfig = config("cxf").hide

    lazy val wsdl2java = TaskKey[Seq[File]](
      "wsdl2java",
      "Generate Java classes from a WSDL"
    )
    lazy val cxfVersion = SettingKey[String](
      "cxfVersion",
      "The CXF version to use"
    )
    lazy val cxfFlags = SettingKey[Seq[String]](
      "cxfFlags",
      "General CXF flags to use"
    )
    lazy val cxfWsdls = SettingKey[Seq[Wsdl]](
      "cxfWsdls",
      "The WSDLs to generate from"
    )
    lazy val cxfTarget = SettingKey[File](
      "cxfTarget",
      "The directory to generate the Java classes in"
    )

    object CxfImplementationType extends Enumeration {
      val Client, Impl, Server = Value
    }

    case class Wsdl(id: String, wsdlFile: File, pkg: String, implementations: Seq[CxfImplementationType.Value] = Seq(CxfImplementationType.Client, CxfImplementationType.Impl), extraFlags: Seq[String] = Nil, bindFile: Option[File] = None)

  }

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    Seq(ivyConfigurations += CxfConfig) ++ cxfDefaults ++ inConfig(Compile)(cxfConfig)

  val cxfDefaults: Seq[Def.Setting[_]] = Seq(
    cxfVersion := "2.1.2",
    libraryDependencies ++= Seq(
      "org.apache.cxf" % "cxf-tools-wsdlto-core" % cxfVersion.value % CxfConfig.name,
      "org.apache.cxf" % "cxf-tools-common" % cxfVersion.value % CxfConfig.name,
      "org.apache.cxf" % "cxf-tools-wsdlto-databinding-jaxb" % cxfVersion.value % CxfConfig.name,
      "org.apache.cxf" % "cxf-tools-wsdlto-frontend-jaxws" % cxfVersion.value % CxfConfig.name
    ),
    cxfFlags := Seq(
      "-validate",
      "-verbose",
      "-xjc -Xequals -XhashCode -XtoString"
    ),
    cxfWsdls := Nil,
    cxfTarget := sourceManaged.value / "cxf"
  )

  lazy val cxfConfig: Seq[Def.Setting[_]] = Seq(
    sourceManaged in CxfConfig := sourceManaged.value,
    managedSourceDirectories in Compile += (sourceManaged in CxfConfig).value,
    managedClasspath in wsdl2java := {
      val ct = (classpathTypes in wsdl2java).value
      val report = update.value
      Classpaths.managedJars(CxfConfig, ct, report)
    },
    wsdl2java := {
      val s: TaskStreams = streams.value
      val classpath: String = (managedClasspath in wsdl2java).value.files.map(_.getAbsolutePath).mkString(System.getProperty("path.separator"))

      val wsdls = (cxfWsdls in Compile).value
      val target = (cxfTarget in Compile).value

      def outputDir(wsdl: Wsdl): File = new File(cxfTarget.value, wsdl.id)

      def params(wsdl: Wsdl): Seq[String] =
        cxfFlags.value ++
          Seq("-p", wsdl.pkg) ++
          wsdl.implementations.map {
            case CxfImplementationType.Client => "-client"
            case CxfImplementationType.Impl => "-impl"
            case CxfImplementationType.Server => "-server"
          } ++
          wsdl.bindFile.map(_.absolutePath).map(Seq("-b", _)).getOrElse(Nil)

      s.log.debug(s"Generating Java classes for ${wsdls.map(_.id)} into $target using classpath $classpath")
      val (updated, notModified) = wsdls.partition { wsdl =>
        val out = outputDir(wsdl)
        !out.exists || wsdl.wsdlFile.lastModified() > out.lastModified() || out.listFiles().isEmpty
      }
      notModified.foreach { wsdl =>
        s.log.debug(s"Skipping unmodified WSDL ${wsdl.id} from ${wsdl.wsdlFile}")
      }
      updated.foreach { wsdl =>
        val output = outputDir(wsdl)
        val args: Seq[String] =
          Seq("-d", output.getAbsolutePath) ++ params(wsdl)
        s.log.debug(s"Cleaning output directory ${output.getAbsolutePath} for ${wsdl.id}...")
        IO.delete(output)
        IO.createDirectory(output)
        s.log.info(s"Generating class for ${wsdl.id} from ${wsdl.wsdlFile.getAbsolutePath} to ${output.getAbsolutePath}")
        val cmd: Seq[String] = Seq(
          "java", "-cp", classpath, "-Dfile.encoding=UTF-8",
          "org.apache.cxf.tools.wsdlto.WSDLToJava"
        ) ++ args :+ wsdl.wsdlFile.getAbsolutePath
        s.log.debug(cmd.toString())
        scala.sys.process.Process(cmd).!
        s.log.info(s"Finished generation for ${wsdl.id}")
        IO.copyDirectory(output, (sourceManaged in CxfConfig).value, overwrite = true)
      }
      ((sourceManaged in CxfConfig).value ** "*.java").get
    },
    (sourceGenerators in Compile) += {
      wsdl2java
    }
  )
}

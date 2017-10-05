import io.dapas.sbt.cxf.{CxfImplementationType, Wsdl}

lazy val root = (project in file("."))
    .plugins(CxfPlugin, JvmPlugin)
  .settings(
    version := "0.1",
    scalaVersion := "2.10.6"
    cxfWsdls := Seq(
      Wsdl(
        id = "simple",
        wsdlFile = (resourceDirectory in Compile).value / "wsdl" / "simple.wsdl",
        implementations = Seq(CxfImplementationType.Impl, CxfImplementationType.Client),
        pkg = "com.acme.generated.jaxws.simple"
      )
    )
  )
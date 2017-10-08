lazy val root = (project in file("."))
  .settings(
    version := "0.1",
    scalaVersion := "2.11.11",
    cxfWsdls := Seq(
      Wsdl(
        id = "simple",
        wsdlFile = (resourceDirectory in Compile).value / "wsdl" / "simple.wsdl",
        implementations = Seq(CxfImplementationType.Impl, CxfImplementationType.Client, CxfImplementationType.Server),
        pkg = "acme.simple"
      )
    )
  )
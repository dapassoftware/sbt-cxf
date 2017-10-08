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
    cxfTarget := (crossTarget in Compile).value / "cxf"
  )
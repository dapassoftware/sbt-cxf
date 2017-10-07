# sbt-cxf

SBT plugin for generating [CXF](http://cxf.apache.org/) Java classes from WSDLs using [wsdl2java](http://cxf.apache.org/docs/wsdl-to-java.html).

## Installation
Add the plugin to your `project/plugins.sbt`

```scala
addSbtPlugin("io.dapas.sbt" % "sbt-cxf" % "x.y.z")
```

The plugin is an auto-plugin and is automatically added to all projects and registers itself as a source generator in the `Compile` stage.


Any WSDLs added to the `cxfWsdls` setting are automatically picked up. For example:

```scala
  .settings(
    cxfWsdls in Compile := Seq(
      Wsdl(
        id = "mywsdl",
        wsdlFile = (resourceDirectory in Compile).value / "wsdl" / "my-wsdl.wsdl",
        implementations = Seq(CxfImplementationType.Impl, CxfImplementationType.Client),
        pkg = "com.acme.generated.jaxws.my",
        bindFile = Some((resourceDirectory in Compile).value / "wsdl" / "my-wsdl-bindings.xjb")
      ),
      Wsdl(
        id = "otherwsdl",
        wsdlFile = (resourceDirectory in Compile).value / "wsdl" / "other.wsdl",
        implementations = Seq(CxfImplementationType.Impl, CxfImplementationType.Client),
        pkg = "com.acme.generated.jaxws.other"
      )
    )
  )
```

You can configure the CXF version to use (default: 2.1.2) through the `cxfVersion := "<version>"` setting

## Usage
The plugin auto loads and when any WSDLs are defined under `cxfWsdls` the Java classes are generated during a compile, prior to compiling the rest of the souces.

### General CXF settings

- `cxfVersion := "2.1.2"` - The CXF version to use
- `cxfFlags := Seq("-validate", "-verbose", "-xjc -Xequals -XhashCode -XtoString")` - The CXF flags that are applied for all WSDLs in the project
- `cxfTarget := sourceManaged / "cxf"` - The directories to place the generated sources; using SBT defaults this would be `target/scala-2.xx/src_managed/cxf/<id>/`
- `cxfWsdls := Seq.empty[Wsdl]` - The WSDLs to generate the classes for, defaults to empty which skips generation
### WSDL settings

## History
- *0.1.0* - First (rough) implementation

## TODO
About everything, but also including

- Refactor output location and managedSource management
- Move logic out of the task definition to a separate object
- Add unit tests for the object
- Add more sbt scripted tests
- Add easier and more extensive configuration
 

## Acknowledgments
This plugin is based on/drew inspiration from
- [sbt-cxf-wsdl2java]("https://github.com/ebiznext/sbt-cxf-wsdl2java")

## License
Published source code and binary files have the following copyright:

```
Copyright Dapas Software B.V.
Apache License, Version 2.0
http://www.apache.org/licenses/LICENSE-2.0.html
```                                  
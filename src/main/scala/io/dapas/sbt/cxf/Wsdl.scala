package io.dapas.sbt.cxf

import sbt.File

case class Wsdl(
  id: String,
  wsdlFile: File,
  pkg: String,
  implementations: Seq[CxfImplementationType.Value] = Seq(CxfImplementationType.Client, CxfImplementationType.Impl),
  extraFlags: Seq[String] = Nil,
  bindFile: Option[File] = None
)

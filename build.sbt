import sbt._
import sbt.Keys._

lazy val root =
  Project(id = "cloudflow-poc", base = file("."))
    .enablePlugins(ScalafmtPlugin)
    .settings(
      name := "cloudflow-poc",
      skip in publish := true,
      scalafmtOnCompile := true,
    )
    .withId("cloudflow-poc")
    .settings(commonSettings)
    .aggregate(
      datamodel,
      pipelineBlueprint,
      flinkStreamlet,
    )

lazy val pipelineBlueprint= appModule("pipeline-blueprint")
  .enablePlugins(CloudflowApplicationPlugin)
  .settings(commonSettings)
  .settings(
    name := "pipeline-blueprint",
    runLocalConfigFile := Some("pipeline-blueprint/src/main/resources/local.conf"),
  )

lazy val datamodel = appModule("datamodel")
  .enablePlugins(CloudflowLibraryPlugin)
  .settings(
    schemaCodeGenerator := SchemaCodeGenerator.Java,
    schemaPaths := Map(
      SchemaFormat.Avro -> "src/main/avro",
    ),
    commonSettings,
  )


lazy val flinkStreamlet= appModule("flink-streamlet")
  .enablePlugins(CloudflowFlinkPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "ch.qos.logback"         %  "logback-classic"        % "1.2.3",
      // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
      "com.github.javafaker" % "javafaker" % "1.0.2"

    )
  )
  .settings(
    parallelExecution in Test := false
  )
  .dependsOn(datamodel)

def appModule(moduleID: String): Project = {
  Project(id = moduleID, base = file(moduleID))
    .settings(
      name := moduleID
    )
    .withId(moduleID)
    .settings(commonSettings)
}

lazy val commonSettings = Seq(
  organization := "com.lightbend.cloudflow",
  headerLicense := Some(HeaderLicense.ALv2("(C) 2016-2020", "Lightbend Inc. <https://www.lightbend.com>")),
  scalaVersion := "2.12.11",
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.8",
    "-Xlog-reflective-calls",
    "-Xlint",
    "-Ywarn-unused",
    "-Ywarn-unused-import",
    "-deprecation",
    "-feature",
    "-language:_",
    "-unchecked"
  ),

  scalacOptions in (Compile, console) --= Seq("-Ywarn-unused", "-Ywarn-unused-import"),
  scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value,
)

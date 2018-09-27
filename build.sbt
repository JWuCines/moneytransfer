name := "MoneyTransfer"

version := "0.1"

scalaVersion := "2.12.6"
crossPaths := false

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"


libraryDependencies ++= {
  val AkkaHttpVersion       = "10.1.5"
  val AkkaModuleVersion = "2.5.16"
  val ScalaLoggingVersion = "3.9.0"
  val ScalatestVersion  = "3.0.5"
  val Slf4jVersion = "1.7.25"
  val LogBackVersion = "1.2.3"

  Seq(
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion % "compile",
    "com.typesafe.akka" %% "akka-actor" % AkkaModuleVersion % "compile",
    "com.typesafe.akka" %% "akka-stream" % AkkaModuleVersion % "compile",
    "com.typesafe.akka" %% "akka-slf4j" %  AkkaModuleVersion % "compile",
    "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion % "compile",
    "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion % "compile",
    "org.slf4j" % "slf4j-api" % Slf4jVersion % "compile",
    "org.slf4j" % "log4j-over-slf4j" % Slf4jVersion % "compile",
    "ch.qos.logback" % "logback-classic" % LogBackVersion % "compile",
    "net.liftweb" %% "lift-json" % "3.3.0" % "compile",
    "joda-time" % "joda-time" % "2.10" % "compile",
    "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % "test",
    "org.scalatest"     %% "scalatest" % ScalatestVersion % "test",
  )
}
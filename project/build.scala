import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin
import akka.sbt.AkkaKernelPlugin.{ Dist, outputDirectory, distJvmOptions}
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{ MultiJvm }

object AkkaDoublerBuild extends Build {

  val Organization = "marco"
  val Version = "2.2.3"
  val ScalaVersion = "2.10.2"

  val IvyXML = <dependencies><exclude org="org.slf4j" module="slf4j-log4j12"/></dependencies>
  
  lazy val buildSettings = Defaults.defaultSettings ++ multiJvmSettings ++ Seq(
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    crossPaths := false,
    organizationName := "marco",
    ivyXML := IvyXML 
  )

  lazy val defaultSettings = buildSettings ++ Seq(
    // compile options
    scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")    
  )

  lazy val AkkaDoubler = Project(
    id = "hecate-akka",
    base = file("."),
    settings = defaultSettings ++ AkkaKernelPlugin.distSettings ++ Seq(
      libraryDependencies ++= Dependencies.hecateAkka,
      distJvmOptions in Dist := "-Xms256M -Xmx1024M",
      outputDirectory in Dist := file("target/AkkaDoubler-dist")
    )
  ) configs(MultiJvm)

  // multiJvmSettings for sbt 0.13
  lazy val multiJvmSettings = SbtMultiJvm.multiJvmSettings ++ Seq(
    // make sure that MultiJvm test are compiled by the default test compilation
    compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
    // disable parallel tests
    parallelExecution in Test := false,
    // make sure that MultiJvm tests are executed by the default test target
    executeTests in Test <<=
    ((executeTests in Test), (executeTests in MultiJvm)) map {
      case ((testResults), (multiJvmResults)) =>
	val overall =
	  if (testResults.overall.id < multiJvmResults.overall.id)
	    multiJvmResults.overall
	  else
	    testResults.overall
	Tests.Output(overall,
		     testResults.events ++ multiJvmResults.events,
		     testResults.summaries ++ multiJvmResults.summaries)
    }
  )

}

object Dependencies {
  // Versions
  object V {
    val Akka = "2.2.3"
    val ScalaLibrary = "2.10.2"
    val ScalaTest = "1.9.1"
    val JUnit = "4.11"
    val Logback = "1.0.7"
  }

  val hecateAkka = Seq(
    // ---- application dependencies ----
    "com.typesafe.akka" %% "akka-actor" % V.Akka,
    "com.typesafe.akka" % "akka-contrib_2.10" % V.Akka,
    "com.typesafe.akka" %% "akka-kernel" % V.Akka,
    "com.typesafe.akka" %% "akka-remote" % V.Akka,
    "com.typesafe.akka" %% "akka-slf4j" % V.Akka,
    "org.scala-lang" % "scala-library" % V.ScalaLibrary,
    "ch.qos.logback" % "logback-classic" % V.Logback,

    // ---- test dependencies ----
    "com.typesafe.akka" %% "akka-testkit" % V.Akka % "test",
    "com.typesafe.akka" %% "akka-multi-node-testkit" % V.Akka % "test",
    "org.scalatest" %% "scalatest" % V.ScalaTest % "test",
    "junit" % "junit" % V.JUnit % "test"
  )
}

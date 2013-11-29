import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "akka-doubler"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// specs2 - start; http://etorreborre.github.io/specs2/

// libraryDependencies += "org.specs2" %% "specs2" % "2.1.1" % "test"

// scalacOptions in Test ++= Seq("-Yrangepos")

// resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
// 		  "releases"  at "http://oss.sonatype.org/content/repositories/releases")

// specs2 - end

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

retrieveManaged := true

// see also project/build.scala
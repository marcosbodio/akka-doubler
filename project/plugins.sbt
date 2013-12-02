addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.2.0")

addSbtPlugin("com.typesafe.akka" % "akka-sbt-plugin" % "2.2.3")

// http://scalasbt.artifactoryonline.com/scalasbt/simple/sbt-plugin-releases/com.typesafe.akka/akka-sbt-plugin/scala_2.10/sbt_0.13/2.2.3/

// addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.1") <-- this version conflicts with multi-jvm: see https://github.com/typesafehub/sbt-multi-jvm/issues/8

// addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.5")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.9.1")

// resolvers += "sbt-assembly-resolver-0" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"

addSbtPlugin("com.typesafe.sbt" % "sbt-multi-jvm" % "0.3.8")

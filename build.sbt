import sbt._
import Keys._

import com.twitter.sbt._
import com.twitter.sbt.PackageDist._

organization := "rs.tibiras"

name := "tb_backend"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.3"

seq(PackageDist.newSettings: _*)

seq(GitProject.gitSettings: _*)

libraryDependencies ++= {
	Seq(		
		"com.typesafe.akka" %% "akka-actor" % "2.2.3" % "compile",
        "com.typesafe.akka" %% "akka-agent" % "2.2.3" % "compile",
        "com.typesafe.akka" %% "akka-remote" % "2.2.3" % "compile",
        "com.typesafe.akka" %% "akka-kernel" % "2.2.3" % "compile",
        "com.typesafe.akka" %% "akka-slf4j" % "2.2.3" % "compile",
        "io.spray" % "spray-http" % "1.2.0" % "compile",
        "io.spray" % "spray-routing" % "1.2.0" % "compile",
        "io.spray" % "spray-can" % "1.2.0" % "compile",
        "io.spray" % "spray-util" % "1.2.0" % "compile",
        "io.spray" % "spray-client" % "1.2.0" % "compile",
        "io.spray" %  "spray-json_2.10" % "1.2.5" % "compile",
         "com.typesafe.slick" %% "slick" % "2.0.2"
	)
}

libraryDependencies ++= {
     Seq(
        "org.scalatest" %% "scalatest" % "2.0" % "test",
        "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test",
        "io.spray" % "spray-testkit" % "1.2.0" % "test"
    )
}

crossPaths := false

publishMavenStyle := true 

resolvers ++= {
	Seq(
		"Local Maven" at "file://" + Path.userHome.absolutePath + "/.m2/repo",
        "Local Ivy" at "file://" + Path.userHome.absolutePath + "/.ivy2/local",
		"Akka Releases" at "http://akka.io/repository",
        "Spray Releases" at "http://repo.spray.io"
	)
}

logLevel := Level.Info

maxErrors := 50

pollInterval := 1000

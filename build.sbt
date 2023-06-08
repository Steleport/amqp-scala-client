name := "amqp-scala-client"

organization := "space.spacelift"

version := "2.1.1-SNAPSHOT"

scalaVersion := "2.13.10"

crossScalaVersions := Seq("2.12.18", "2.13.10")

scalacOptions  ++= Seq("-feature", "-language:postfixOps")

scalacOptions  ++= Seq("-unchecked", "-deprecation")

//useGpg := true

//credentials += Credentials(Path.userHome / ".ivy2" / ".spacelift-credentials")

//usePgpKeyHex("561D5885877866DF")

publishMavenStyle := true

publishTo := None
//{
  //val nexus = "https://oss.sonatype.org/"
  //if (isSnapshot.value)
    //Some("snapshots" at nexus + "content/repositories/snapshots")
  //else
    //Some("releases"  at nexus + "service/local/staging/deploy/maven2")
//}

//publishArtifact in Test := false

//pomIncludeRepository := { _ => false }


val akkaVersion   = "2.6.20"
libraryDependencies ++=
    Seq(
        "com.rabbitmq"         % "amqp-client"          % "4.0.2",
        "com.typesafe.akka"    %% "akka-actor"          % akkaVersion % "provided",
        "com.typesafe.akka"    %% "akka-slf4j"          % akkaVersion % "test",
        "com.typesafe.akka"    %% "akka-testkit"        % akkaVersion  % "test",
        "org.scalatest"        %% "scalatest"           % "3.2.16" % "test",
        "ch.qos.logback"       %  "logback-classic"     % "1.4.7" % "test",
        "junit"           	   % "junit"                % "4.12" % "test"
    )

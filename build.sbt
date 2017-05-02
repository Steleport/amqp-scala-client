name := "amqp-scala-client"

organization := "space.spacelift"
 
version := "2.0.1-SNAPSHOT"
 
scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.11.8", "2.12.1")

scalacOptions  ++= Seq("-feature", "-language:postfixOps")
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions  ++= Seq("-unchecked", "-deprecation")

useGpg := true

credentials += Credentials(Path.userHome / ".ivy2" / ".spacelift-credentials")

usePgpKeyHex("561D5885877866DF")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://spacelift.space</url>
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://github.com/spacelift/amqp-scala-client</url>
    <connection>scm:git:git@github.com:spacelift/amqp-scala-client.git</connection>
  </scm>
  <developers>
    <developer>
      <id>drheart</id>
      <name>Dustin R. Heart</name>
      <url>http://spacelift.space</url>
    </developer>
  </developers>)


libraryDependencies <<= scalaVersion { scala_version => 
    val akkaVersion   = "2.4.16"
    Seq(
        "com.rabbitmq"         % "amqp-client"          % "4.0.2",
        "com.typesafe.akka"    %% "akka-actor"          % akkaVersion % "provided",
        "com.typesafe.akka"    %% "akka-slf4j"          % akkaVersion % "test",
        "com.typesafe.akka"    %% "akka-testkit"        % akkaVersion  % "test",
        "org.scalatest"        %% "scalatest"           % "3.0.1" % "test",
        "ch.qos.logback"       %  "logback-classic"     % "1.1.9" % "test",
        "junit"           	   % "junit"                % "4.12" % "test"
    )
}

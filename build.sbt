import sbt.Keys.scalaVersion

name := "amqp-scala-client"

organization := "space.spacelift"
 
version := "2.1.1-SNAPSHOT"
 
scalaVersion := "2.12.15"



crossScalaVersions := Seq("2.11.12", "2.12.15")

scalacOptions  ++= Seq("-feature", "-language:postfixOps")
 
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

scalacOptions  ++= Seq("-unchecked", "-deprecation")

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

Test / publishArtifact := false

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

val akkaVersion = "2.5.32"

libraryDependencies ++= Seq(
        "com.rabbitmq"         % "amqp-client"          % "4.12.0",
        "com.typesafe.akka"    %% "akka-actor"          % akkaVersion % "provided",
        "com.typesafe.akka"    %% "akka-slf4j"          % akkaVersion % "test",
        "com.typesafe.akka"    %% "akka-testkit"        % akkaVersion % "test",
        "org.scalatest"        %% "scalatest"           % "3.0.3" % "test",
        "ch.qos.logback"       %  "logback-classic"     % "1.1.11" % "test",
        "junit"           	   % "junit"                % "4.12" % "test"
)


name := "amqp-client"

organization := "com.github.sstone"
 
version := "2.0-SNAPSHOT"
 
scalaVersion := "2.11.8"

scalacOptions  ++= Seq("-feature", "-language:postfixOps")

scalacOptions  ++= Seq("-unchecked", "-deprecation")
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies <<= scalaVersion { scala_version => 
    val akkaVersion   = "2.4.3"
    Seq(
        "com.rabbitmq"         % "amqp-client"          % "3.6.1",
        "com.typesafe.akka"    %% "akka-actor"          % akkaVersion % "provided",
        "com.typesafe.akka"    %% "akka-slf4j"          % akkaVersion % "test",
        "com.typesafe.akka"    %% "akka-testkit"        % akkaVersion  % "test",
        "org.scalatest"        %% "scalatest"           % "2.2.5" % "test",
        "ch.qos.logback"       %  "logback-classic"     % "1.1.2" % "test",
        "junit"           	   % "junit"                % "4.12" % "test"
    )
}

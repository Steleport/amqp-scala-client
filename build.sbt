name := "amqp-client"

organization := "com.github.sstone"
 
version := "2.0-SNAPSHOT"
 
scalaVersion := "2.12.1"

scalacOptions  ++= Seq("-feature", "-language:postfixOps")
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

scalacOptions  ++= Seq("-unchecked", "-deprecation")

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

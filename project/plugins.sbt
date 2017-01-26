resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")

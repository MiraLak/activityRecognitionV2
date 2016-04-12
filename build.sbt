name := "Activity Recognition V2"

version := "1.0"

scalaVersion := "2.1O.5-local"
scalaHome := Some(file("/home/lakhal/Logiciels/scala-2.10.5"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "1.4.0",
  "org.apache.spark" % "spark-mllib_2.10" % "1.4.0",
  "org.apache.spark" % "spark-streaming_2.10" % "1.4.0",
  "com.datastax.spark" % "spark-cassandra-connector_2.10" % "1.4.0"
)

resolvers ++= Seq(
  "Akka Repository" at "http://repo.akka.io/releases/",
  "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Second Typesafe repo" at "http://repo.typesafe.com/typesafe/maven-releases/"
)

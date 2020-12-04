name := "MoviesAnalytics"

version := "1.0"

scalaVersion := "2.12.2"

val circeVersion = "0.13.0"
val sparkVersion = "2.4.5"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
//  "org.apache.spark" %% "spark-core" % sparkVersion ,
//  "org.apache.spark" %% "spark-sql" % sparkVersion ,
  "org.apache.spark" %% "spark-core" % "2.4.5",
  "org.apache.spark" %% "spark-sql" % "2.4.5" ,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-jawn" % circeVersion
)


name := "logrep"

version := "0.1.2"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.github.nscala-time" %% "nscala-time" % "1.4.0"
)

scalacOptions ++= Seq("-optimise", "-feature", "-deprecation")
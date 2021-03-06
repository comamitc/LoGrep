name := "logrep"

version := "0.1.3"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.github.nscala-time" %% "nscala-time" % "1.4.0",
	"io.spray" %%  "spray-json" % "1.3.1"
)

scalacOptions ++= Seq("-optimise", "-feature", "-deprecation")
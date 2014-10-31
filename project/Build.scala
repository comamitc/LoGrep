import sbt._

object LoGrepBuild extends Build {
  println("building...")
  lazy val LoGrep = Project("LoGrep", file(".")) dependsOn(uniform % "compile")

  lazy val uniform = RootProject(uri("git://github.com/comamitc/uniform.git"))
}
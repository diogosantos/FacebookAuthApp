import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "AuthApp"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.scribe" % "scribe" % "1.3.3"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}

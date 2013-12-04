package com.github.timothyklim

import sbt._
import Keys._

object LiquibasePlugin extends Plugin {

  lazy val conf = config("liquibase") describedAs ("liquibase sbt settings")

  val newTask = taskKey[Unit]("A new task.") in conf
  val update = taskKey[Unit]("Wow!.") in conf
  val newSetting = settingKey[String]("A new setting.")

  // a group of settings ready to be added to a Project
  // to automatically add them, do
  val newSettings = Seq(
    newSetting := "test",
    newTask := println(newSetting.value),
    update := println(newSetting.value)
  )

  lazy val myCommand =
    Command.command("hello") {
      (state: State) =>
        println("Hi!")
        state
    }


  lazy val mySecondCommand =
    Command.command("wow-wow") {
      (state: State) =>
        println("wow!")
        state
    }

  val myPluginSettings = inConfig(conf)(
    newSettings ++
      Seq(commands ++= Seq(myCommand, mySecondCommand))
  )

}

package com.github.timothyklim


import sbt._
import sbt.classpath.ClasspathUtilities
import Keys._

import liquibase.integration.commandline.CommandLineUtils
import liquibase.resource.FileSystemResourceAccessor
import liquibase.database.{Database => DatabaseTrait}
import liquibase.{Liquibase => LiquibaseClass}


object LiquibasePlugin extends Plugin {

  val Liquibase = config("liquibase").hide

  object LiquibaseKeys {

    val update = TaskKey[Unit]("liquibase-update")
    val status = TaskKey[Unit]("liquibase-status")

    val database = TaskKey[DatabaseTrait]("liquibase-database")
    val instance = TaskKey[LiquibaseClass]("liquibase-instance")

    val options = SettingKey[Map[String, String]]("liquibase-options")
    val changeLog = SettingKey[String]("liquibase-changelog")
    val schemaName = SettingKey[String]("liquibase-schema-name")
    val catalog = SettingKey[String]("liquibase-catalog")
    val context = SettingKey[String]("liquibase-context")
    val version = SettingKey[String]("liquibase-version")

  }

  lazy val liquibaseSettings: Seq[Setting[_]] = inConfig(Liquibase)(LiquibaseSettings.default) ++ LiquibaseSettings.dependencies

  object LiquibaseSettings {

    import LiquibaseKeys._

    def default: Seq[Setting[_]] = Seq(
      options := Map(),
      changeLog := "src/main/resources/migrations/changelog.sql",
      schemaName := "",
      catalog := "",
      context := "",
      version := "3.0.7",
      database <<= databaseTask,
      instance <<= instanceTask,
      status <<= statusTask,
      update <<= updateTask
    )

    def dependencies: Seq[Setting[_]] = Seq(
      ivyConfigurations += Liquibase,
      libraryDependencies <+= (version in Liquibase) {
        version =>
          "org.liquibase" % "liquibase-core" % version
      }
    )

    def databaseTask = (options, catalog, schemaName, fullClasspath in Runtime) map {
      (options, catalog, schemaName, classPath) =>
        val classLoader = ClasspathUtilities.toLoader(classPath.map(_.data))
        CommandLineUtils.createDatabaseObject(classLoader,
          options.getOrElse("url", ""),
          options.getOrElse("username", ""),
          options.getOrElse("password", ""),
          options.getOrElse("driver", ""),
          catalog,
          schemaName,
          catalog.isEmpty,
          schemaName.isEmpty,
          null,
          null,
          catalog,
          schemaName
        )
    }

    def instanceTask = (database, changeLog) map {
      (database, changeLog) =>
        new LiquibaseClass(changeLog, new FileSystemResourceAccessor, database)
    }

    def statusTask = (instance, context) map {
      (instance, context) =>
        instance.reportStatus(true, context, new LoggerWriter(ConsoleLogger()))
    }

    def updateTask = (instance, context) map {
      (instance, context) =>
        instance.update(context)
    }

  }

}

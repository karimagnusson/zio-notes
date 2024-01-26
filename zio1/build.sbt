
inThisBuild(List(
  organization := "io.github.karimagnusson",
  homepage := Some(url("https://kuzminki.info/")),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  developers := List(
    Developer(
      "karimagnusson",
      "Kari Magnusson",
      "kotturinn@gmail.com",
      url("https://github.com/karimagnusson")
    )
  )
))

ThisBuild / version := "1.0.1"
ThisBuild / versionScheme := Some("early-semver")

scalaVersion := "3.3.1"

lazy val scala3 = "3.3.1"
lazy val scala213 = "2.13.12"
lazy val scala212 = "2.12.18"
lazy val supportedScalaVersions = List(scala212, scala213, scala3)

lazy val root = (project in file("."))
  .aggregate(zioNotes)
  .settings(
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val zioNotes = (project in file("zio-notes"))
  .settings(
    name := "zio-notes",
    crossScalaVersions := supportedScalaVersions,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "1.0.18"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature"
    )
  )
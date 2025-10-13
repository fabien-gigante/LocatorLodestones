pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.kikugie.dev/snapshots")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
	centralScript = "build.gradle.kts"

	create(rootProject) {
		versions("1.21.6", "1.21.9")
		vcsVersion = "1.21.9"
	}
}
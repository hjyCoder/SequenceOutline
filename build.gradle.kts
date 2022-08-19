fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.7.10"
  // Gradle IntelliJ Plugin
  id("org.jetbrains.intellij") version "1.8.0"
  // Gradle Changelog Plugin
  id("org.jetbrains.changelog") version "1.3.1"
  // Gradle Qodana Plugin
  id("org.jetbrains.qodana") version "0.1.13"

  id("org.jetbrains.grammarkit") version "2021.2.2"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

repositories {
  mavenLocal()
  mavenCentral()
}

dependencies {
  implementation("org.apache.commons:commons-lang3:3.5")
  implementation("commons-io:commons-io:2.5")
  implementation("org.projectlombok:lombok:1.18.12")
  annotationProcessor("org.projectlombok:lombok:1.18.12")
  implementation("com.alibaba:fastjson:1.2.73")
  implementation("com.alibaba:transmittable-thread-local:2.12.1")
  implementation("org.apache.commons:commons-collections4:4.4")
  implementation("com.google.guava:guava:31.1-jre")
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
  options.compilerArgs = listOf("-Xlint:deprecation")
}

intellij {
  pluginName.set(properties("pluginName"))
  version.set(properties("platformVersion"))
  type.set(properties("platformType"))

  // Plugin Dependencies. Uses `platformPlugins` property from the gradle.properties file.
  plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

changelog {
  version.set(properties("pluginVersion"))
  groups.set(emptyList())
}

tasks {
  properties("javaVersion").let {
    withType<JavaCompile> {
      sourceCompatibility = it
      targetCompatibility = it
    }
  }

  wrapper {
    gradleVersion = properties("gradleVersion")
  }

  patchPluginXml {
    version.set(properties("pluginVersion"))
    sinceBuild.set(properties("pluginSinceBuild"))
    untilBuild.set(properties("pluginUntilBuild"))
  }
}

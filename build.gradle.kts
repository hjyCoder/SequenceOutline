fun properties(key: String) = project.findProperty(key).toString()

plugins {
  // Java support
  id("java")
  // Kotlin support
  id("org.jetbrains.kotlin.jvm") version "1.6.10"
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
//  maven { url 'https://plugins.gradle.org/m2/' }
//  maven { url 'http://maven.aliyun.com/nexus/content/repositories/google' }
//  maven { url 'http://maven.aliyun.com/nexus/content/groups/public/' }
//  maven { url 'http://maven.aliyun.com/nexus/content/repositories/jcenter'}
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
//  implementation("com.moorror.plugin.common.test:moorror-plugin-common-test:1.0-SNAPSHOT"){
//    exclude(group="org.slf4j", module = "slf4j-api")
//  }
//  implementation("com.moorror.common.money.share:moorror-common-money-share:1.0-SNAPSHOT")
//  implementation("com.moorror.common.utils:moorror-common-utils:1.0-SNAPSHOT")
//  implementation("org.slf4j:slf4j-api:1.7.32")
//  implementation("redis.clients:jedis:4.2.3"){
//    exclude(group="org.slf4j", module = "slf4j-api")
//  }

//  implementation(files("/Users/jiyanghuang/Documents/programDev/bdef/bdef-constant/target/bdef-constant-1.0-SNAPSHOT.jar",
//    "/Users/jiyanghuang/Documents/programDev/bdef/bdef-model/target/bdef-model-1.0-SNAPSHOT.jar",
//  "/Users/jiyanghuang/Documents/programDev/bdef/bdef-runtime/target/bdef-runtime-1.0-SNAPSHOT.jar"))
//
//  compile group: 'com.moorror.plugin.common.test', name: 'moorror-plugin-common-test', version: '1.0-SNAPSHOT'
//  compile group: 'com.moorror.common.money.share', name: 'moorror-common-money-share', version: '1.0-SNAPSHOT'
//  compile group: 'com.moorror.common.utils', name: 'moorror-common-utils', version: '1.0-SNAPSHOT'
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

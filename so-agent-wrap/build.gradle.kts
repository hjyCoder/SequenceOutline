plugins {
    java
}

group = "com.muy"
version = ""

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    compileOnly("com.alibaba.jvm.sandbox:sandbox-api:1.3.3")
    implementation("org.kohsuke.metainf-services:metainf-services:1.9")
    annotationProcessor("org.kohsuke.metainf-services:metainf-services:1.9")
    implementation("org.apache.commons:commons-lang3:3.5")
    implementation("commons-io:commons-io:2.4")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.slf4j:slf4j-api:1.7.24")
    implementation("ch.qos.logback:logback-classic:1.2.1")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.4")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.4")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.alibaba:transmittable-thread-local:2.12.1")
    implementation("commons-codec:commons-codec:1.15")
    annotationProcessor("org.projectlombok:lombok:1.18.12")
    implementation("org.projectlombok:lombok:1.18.12")

}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

//tasks.jar {
//    archiveBaseName.set("sr-agent-wrap")
//}

tasks.jar {
    archiveBaseName.set("so-agent-wrap")
//    archiveClassifier.set("uber")
    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

//tasks.register<Jar>("uberJar") {
//    archiveClassifier.set("uber")
//    from(sourceSets.main.get().output)
//    dependsOn(configurations.runtimeClasspath)
//    from({
//        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
//    })
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//}

//tasks["jar"].dependsOn("uberJar")

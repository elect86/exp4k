plugins {
    java
    kotlin("jvm") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"
val kotestVersion = "4.1.0.293-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))

    listOf("runner-junit5", "assertions-core", "runner-console"/*, "property"*/).forEach {
        testImplementation("io.kotest:kotest-$it-jvm:$kotestVersion")
    }
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> { useJUnitPlatform() }
}
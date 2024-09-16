plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.strikt:strikt-core:0.35.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.1")
    testImplementation("org.assertj:assertj-core:3.26.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

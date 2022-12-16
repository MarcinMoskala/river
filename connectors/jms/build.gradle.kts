import Dependencies.Pool
import Dependencies.RiverCore

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
}

repositories {
    mavenCentral()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(RiverCore)
    implementation(Pool)

    Dependencies.Coroutines.forEach { implementation(it) }

    implementation(Dependencies.Jms.Api)
    implementation(Dependencies.Jms.ArtemisClient)
    implementation(Dependencies.Jms.ArtemisServer)

    compileOnly("org.slf4j:slf4j-api:${Version.Slf4j}")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.kotest:kotest-runner-junit5:${Version.Kotest}")
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xcontext-receivers")
        jvmTarget = "17"
    }
}

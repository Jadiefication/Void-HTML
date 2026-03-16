plugins {
    kotlin("jvm") version "2.3.20"
    `maven-publish`
    // API docs generation
    id("org.jetbrains.dokka") version "2.1.0"
    id("jacoco")
}

group = "io.void"
version = "1.1.4"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.github.Void-Framework:Void:v2.2.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

description = "void-html"

publishing {
    publications {
        create<MavenPublication>("mavenKotlin") {
            artifact(tasks["jar"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            groupId = "com.github.Jadiefication"
            artifactId = "Void-HTML"
            version = rootProject.version.toString()
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // Run coverage report after tests
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.withType<JacocoReport>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    // Only run if tests ran
    dependsOn(tasks.withType<Test>())
}

// Optional: aggregate coverage across modules
tasks.register<JacocoReport>("jacocoRootReport") {
    group = "verification"
    description = "Generates an aggregate JaCoCo coverage report for all subprojects"

    // Ensure tests in this project run first
    dependsOn("test")

    // Explicitly ensure this project's jacocoTestReport runs before the aggregator.
    // Gradle requires an explicit dependsOn for tasks that produce executionData used by another task.
    tasks.findByName("jacocoTestReport")?.let { dependsOn(it) }

    // Collect execution data from subprojects
    executionData(
        fileTree(project.rootDir) {
            include("**/build/jacoco/test.exec")
            include("**/build/jacoco/test.exec.gz")
            include("**/build/jacoco/test.exec.zip")
            include("**/build/jacoco/test.exec/**")
            include("**/build/jacoco/test/*.exec")
            include("**/build/jacoco/test.exec*")
            include("**/build/jacoco/*.exec")
            include("**/build/jacoco/*.ec")
            include("**/build/jacoco/test/*.ec")
        },
    )

    val sourceSets = project.extensions.findByName("sourceSets") as? SourceSetContainer
    sourceSets?.findByName("main")?.let { main ->
        sourceDirectories.from(main.allSource.srcDirs)
        classDirectories.from(main.output)
    }

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    // Ensure subproject reports are generated first
    dependsOn(subprojects.mapNotNull { it.tasks.findByName("jacocoTestReport") })
}

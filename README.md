# Void-HTML

A module add‑on for the Void framework that provides an HTML/CSS/JS DSL for building pages and responses on the JVM.

- Wiki: [DeepWiki](https://deepwiki.com/Void-Framework/Void-HTML)

---

### Overview

Void-HTML is a Kotlin DSL and utilities layer that makes it easy to:

- Construct HTML via type‑safe generated element classes (for example `Div`, `H1`, `Img`, etc.).
- Compose fragments with a small `fractal { ... }` container.
- Render full HTML responses compatible with the Void framework's routing/page model.
- Manage page metadata (title, description, external CSS/JS) and integrate CSS resources.

This repository is a library (not a standalone application). You add it as a dependency to a Void-based service.

### Tech Stack

- Language: Kotlin (JVM)
- Build tool: Gradle (Kotlin DSL), Wrapper: Gradle 9.0.0
- Kotlin: 2.2.20
- Java toolchain: 21
- Tests: Kotlin test on JUnit Platform
- Depends on: `com.github.Jadiefication:Void` (via JitPack)

### Requirements

- JDK 21 (OpenJDK 21)
- Gradle Wrapper (included): use `./gradlew` so you don’t need a local Gradle install
- Internet access to fetch dependencies from Maven Central and JitPack

### Installation

This project is intended to be consumed from JitPack. Coordinates and publication may vary; verify the artifact ID
before use.

Gradle (Kotlin DSL):

```kotlin
repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.Jadiefication:Void-HTML:<version>")
}
```

Maven:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    <repository>
        <id>central</id>
        <url>https://repo1.maven.org/maven2/</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.Jadiefication</groupId>
    <artifactId>Void-HTML</artifactId>
    <version>VERSION</version>
</dependency>
</dependencies>
```
### Quick Start

Build a small HTML fragment and render an HTTP response using the Void framework types:

```kotlin
// Define a route in your Void service and return an HTML response
val page = route("/") {
    GET {
        val body = fractal {
            Div("id" to "root") {
                // add children elements here
            }
        }

        val meta = Metadata(this)
        meta.title = "Home"

        createResponse(body, meta)
    }
}
```

You can also create a response with defaults:

```kotlin
val response = createResponse(fractal { /* ...elements... */ })
```

The DSL exposes generated elements under `io.voidx.html.generated.*` (e.g., `Div`, `H1`, `Img`, etc.) and containers
like `Fractal` for text/fragment composition.

Page helpers are available to integrate with Void pages/handlers:

```kotlin
// inside a PageHandler context
handler.html({
    title = "Example"
}) {
    // DSL to build the Element tree for the body
}
```

Including CSS resources packaged in your classpath `css/` folder is supported via page configuration (resources are
exposed through routes and linked into metadata):

```kotlin
val page: Page = /* ... */
    page("site.css", "theme.css") // select resources by file name under resources/css
// Routing integration occurs when the page is added to the router.
```

### Commands

- Build: `./gradlew build`
- Run tests: `./gradlew test`
- Publish to local Maven: `./gradlew publishToMavenLocal` (used by JitPack in `jitpack.yml`)
- Generate JAR/Sources/Javadoc: `./gradlew jar sourcesJar javadocJar`

### Environment Variables

No required environment variables are currently defined.

### Tests

Tests are written with Kotlin test on JUnit Platform. Run:

```
./gradlew test
```

Sample covered behaviors include:

- Rendering HTML responses with correct headers and body content
- Metadata rendering for title, external CSS/JS, and default tags
- Element search utilities (by id/class) within composed trees
- Initialization hooks that integrate HTML/CSS handling with the Void runtime

### Project Structure

Key paths and packages:

- `src/main/kotlin/io/voidx/html/generated/` — Generated element types (`Div`, `H1`, `Img`, ...)
- `src/main/kotlin/io/voidx/html/` — Core DSL primitives (`Element`, `Fractal`, etc.)
- `src/main/kotlin/io/voidx/html/page/` — Page helpers and utilities for integrating with Void routing/pages
- `src/main/kotlin/io/voidx/html/metadata/` — Metadata model and rendering helpers
- `src/main/kotlin/io/voidx/css/` — CSS helpers/pages (e.g., serving CSS content, Tailwind generator utilities)
- `src/test/kotlin/` — Unit tests and integration tests for the DSL and utilities

Build and tooling:

- `build.gradle.kts` — Kotlin/JVM library configuration, Java toolchain 21, Kotlin 2.2.20, JUnit Platform, publishing
  setup
- `settings.gradle.kts` — Root project name and toolchain resolver
- `jitpack.yml` — JitPack build instructions (uses OpenJDK 21, publishes to local Maven)
- `.github/workflows/ktlint.yml` — Ktlint checks (formatting/lint) [if configured in CI]
- `.github/workflows/codeql.yml` — CodeQL analysis

### License

This project is licensed under the MIT License — see `LICENSE` for details.

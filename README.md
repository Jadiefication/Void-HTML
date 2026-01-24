<div align="center">

  <img alt="Void-HTML logo" src=".github/image.png" width="160" height="160" />
  <h1>Void-HTML</h1>
  <p>A module add‑on for the Void framework that provides an HTML/CSS/JS DSL for building pages and responses on the JVM.</p>

  <p>
    <a href="https://jitpack.io/#Jadiefication/Void-HTML"><img alt="JitPack" src="https://jitpack.io/v/Jadiefication/Void-HTML.svg"></a>
    <a href="https://kotlinlang.org"><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin"></a>
    <a href="LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-blue.svg"></a>
    <a href="https://gitpod.io/#https://github.com/Jadiefication/Void-HTML"><img alt="Contribute with Gitpod" src="https://img.shields.io/badge/Contribute%20with-Gitpod-908a85?logo=gitpod"></a>
      <a href="https://codecov.io/github/Void-Framework/Void-HTML" > 
 <img src="https://codecov.io/github/Void-Framework/Void-HTML/graph/badge.svg?token=7XF0IECLH2"/> 
 </a>
  </p>
</div>

Void-HTML is a Kotlin DSL and utilities layer that makes it easy to:

- Construct HTML via type‑safe generated element classes (for example `Div`, `H1`, `Img`, etc.).
- Compose fragments with a small `fractal { ... }` container.
- Render full HTML responses compatible with the Void framework's routing/page model.
- Manage page metadata (title, description, external CSS/JS) and integrate CSS resources.

This repository is a library (not a standalone application). You add it as a dependency to a Void-based service.

Quick links

- Security policy: [SECURITY.md](SECURITY.md)
- Roadmap/TODO: [TODO.md](TODO.md)
- Contributing guide: [CONTRIBUTING.md](CONTRIBUTING.md)
- Code of Conduct: [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)
- Support: [SUPPORT.md](SUPPORT.md)
- License: MIT ([LICENSE](LICENSE))
- Wiki: [DeepWiki](https://deepwiki.com/Void-Framework/Void-HTML)

## Tech Stack

- **Language:** [Kotlin 2.2.20](https://kotlinlang.org/)
- **Build System:** [Gradle 9.0.0](https://gradle.org/) (Kotlin DSL)
- **Minimum Java:** 21 (for building the project)
- **Frameworks:** kotlinx-serialization, SLF4J
- **Distribution:** [JitPack](https://jitpack.io/)
- **Depends on:** `com.github.Jadiefication:Void` (via JitPack)

## Project Structure

- `src/main/kotlin/io/voidx/html/generated/` — Generated element types (`Div`, `H1`, `Img`, ...)
- `src/main/kotlin/io/voidx/html/` — Core DSL primitives (`Element`, `Fractal`, etc.)
- `src/main/kotlin/io/voidx/html/page/` — Page helpers and utilities for integrating with Void routing/pages
- `src/main/kotlin/io/voidx/html/metadata/` — Metadata model and rendering helpers
- `src/main/kotlin/io/voidx/css/` — CSS helpers/pages (e.g., serving CSS content, Tailwind generator utilities)
- `src/test/kotlin/` — Unit tests and integration tests for the DSL and utilities

Build and tooling:

- `build.gradle.kts` — Kotlin/JVM library configuration, Java toolchain 21, Kotlin 2.2.20, JUnit Platform, publishing setup
- `settings.gradle.kts` — Root project name and toolchain resolver
- `jitpack.yml` — JitPack build instructions (uses OpenJDK 21, publishes to local Maven)
- `.github/workflows/ktlint.yml` — Ktlint checks (formatting/lint)
- `.github/workflows/codeql.yml` — CodeQL analysis

## Get started

### Requirements

- JDK 21 (OpenJDK 21)
- Gradle Wrapper (included): use `./gradlew` so you don't need a local Gradle install
- Internet access to fetch dependencies from Maven Central and JitPack

### Installation (JitPack)

This project is intended to be consumed from JitPack. Coordinates and publication may vary; verify the artifact ID before use.

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

### Hello, Void-HTML

Build a small HTML fragment and render an HTTP response using the Void framework types:

```kotlin
// Define a route in your Void service and return an HTML response
val page = route("/") {
    html({
        title = "Home"
    }) {
        fractal {
            Div("id" to "root") {
                H1 { +"Welcome to Void-HTML!" }
                P { +"Type-safe HTML generation" }
            }
        }
    }
}
```

You can also create a response with defaults:

```kotlin
val response = createResponse(fractal { /* ...elements... */ })
```

The DSL exposes generated elements under `io.voidx.html.generated.*` (e.g., `Div`, `H1`, `Img`, etc.) and containers like `Fractal` for text/fragment composition.

Page helpers are available to integrate with Void pages/handlers:

```kotlin
// inside a PageHandler context
handler.html({
    title = "Example"
}) {
    // DSL to build the Element tree for the body
}
```

Including CSS resources packaged in your classpath `css/` folder is supported via page configuration (resources are exposed through routes and linked into metadata):

```kotlin
val page: Page = /* ... */
    route("/") {    }("site.css", "theme.css") // select resources by file name under resources/css
// Routing integration occurs when the page is added to the router.
```

Then open [http://localhost:8080](http://localhost:8080)

## Commands & Scripts

The project uses Gradle for all common tasks:

- `./gradlew build`: Build all modules.
- `./gradlew test`: Run all tests.
- `./gradlew publishToMavenLocal`: Publish to local Maven (used by JitPack in `jitpack.yml`).
- `./gradlew jar sourcesJar javadocJar`: Generate JAR/Sources/Javadoc.
- `./gradlew ktlintCheck`: Run linting checks.

## Configuration & Env Vars

No required environment variables are currently defined.

## Principles

#### Type-Safe DSL
Void-HTML provides type-safe generated element classes that correspond to HTML tags, ensuring compile-time safety and IDE support.

#### Composable
Use the `fractal { ... }` container to compose fragments and build complex HTML structures with a clean, declarative syntax.

#### Framework Integration
Seamlessly integrates with the Void framework's routing/page model while maintaining compatibility with standard HTTP responses.

## Documentation

Core entry points:

- `io.voidx.html.fractal { }` — Create element containers and fragments.
- `io.voidx.html.generated.*` — Type-safe HTML element classes (`Div`, `H1`, `Img`, etc.).
- `io.voidx.html.metadata.Metadata` — Page metadata management (title, CSS, JS).
- `io.voidx.html.util.createResponse()` — Package elements and metadata into HTTP responses.
- `io.voidx.page.route("/path") { GET { ... } }` — Define routes in Void framework.

### Element Construction

The HTML DSL uses generated element classes with a type-safe builder pattern:

```kotlin
fractal {
    Div("id" to "container", "class" to "main") {
        H1 { +"Welcome" }
        P("class" to "intro") { 
            +"This is a paragraph."
        }
    }
}
```

- **Attributes:** Passed as `Pair<String, String>` arguments to the element constructor
- **Child Content:** Added inside the element's lambda block
- **Text Nodes:** Created using the unary plus operator (`+`) on strings

### Metadata Management

Control the HTML `<head>` section with comprehensive metadata support:

```kotlin
val meta = metadata(page_ {
    title = "My Page"
    description = "A description for search engines"
    favicon = "/favicon.ico" to "image/x-icon"
    keywords = listOf("kotlin", "void", "html")
    externalCss = mutableListOf("/app.css")
    mexternalJS = mutableMapOf("/app.js" to true)
}
```

## Testing

The test suite is authoritative and aims for high coverage. Tests are written with Kotlin test on JUnit Platform.

- Run tests: `./gradlew test`
- Coverage: `./gradlew jacocoRootReport`

Sample covered behaviors include:

- Rendering HTML responses with correct headers and body content
- Metadata rendering for title, external CSS/JS, and default tags
- Element search utilities (by id/class) within composed trees
- Initialization hooks that integrate HTML/CSS handling with the Void runtime

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

## License

[MIT](LICENSE) — © 2025 Jadiefication

# TODO - Void Framework (Milestone 1)

## Milestone 1 — Foundation & Core Infrastructure (1–2 weeks)

### 1. Core HTML DSL completion
- [ ] Document runtime and build-time environment variables (area/docs, kind/feature, 0.5d) [1](#3-0) 
- [ ] Enhance error messages for invalid child element nesting (area/dsl, kind/feature, 1d)
- [ ] HTML auto-escape; unsafeHtml() explicit opt-out (area/security, kind/refactor, 1d)

### 2. Page & Routing System Polish
- [ ] Add comprehensive integration tests for kts-* attributes (area/router, kind/test, 1d)
- [ ] Optimize RouterUtil header processing performance (area/router, kind/refactor, 0.5d) [2](#3-1) 
- [ ] Add more routing examples and edge case handling (area/docs, kind/docs, 0.5d)
- [ ] Route groups and scoped middleware: group("/api") { before(Auth) … } (area/router, kind/feature, 1–2d)

### 3. Asset Management & CSS Pipeline
- [ ] Add support for additional CSS frameworks (area/css, kind/feature, 1d)
- [ ] Improve asset bundling and caching strategies (area/css, kind/feature, 1d)

### 4. Server-Driven UI & HTML Features
- [ ] Swap strategy utilities and examples (area/dsl, kind/docs, 1d)
- [ ] Hydration bridge: Embed initial snapshots into SSR HTML window.__VOID_STATE__ (area/state, area/dsl, kind/feature, 1–2d)

### 5. Performance & Caching for HTML
- [ ] SSR fragment/page cache with TTL and vary by user/locale (area/perf, kind/feature, 2–3d)
- [ ] Streaming HTML (chunked) support (area/perf, kind/feature, 2–3d)
- [ ] Static assets: hashed URLs, ETag/If‑None‑Match support (area/router, kind/feature, 1–2d)

### 6. Build & Quality Infrastructure
- [ ] Enhance test coverage for all DSL components (area/test, kind/test, 1d)
- [ ] Add performance benchmarks for Fractal containers (area/test, kind/test, 0.5d) [4](#3-3) 
- [ ] Set up comprehensive CI/CD pipeline (area/build, kind/feature, 1d)
- [ ] Update dependency versions and review build performance (area/build, kind/chore, 0.5d)

### 7. Documentation & Examples -> Will be in [Examples](https://github.com/Void-Framework/Void-Examples)
- [ ] Create comprehensive getting started guide (area/docs, kind/docs, 1d)
- [ ] Add example projects demonstrating core features (area/docs, kind/feature, 1d)

### 8. Client-Side Runtime Enhancement
- [ ] Improve kts_script.js error handling and debugging (area/client, kind/feature, 1d)
- [ ] Add more swap strategies and DOM manipulation utilities (area/client, kind/feature, 1d)
- [ ] Optimize client-side performance for large DOM trees (area/client, kind/refactor, 0.5d)

## Suggested GitHub Issues (Milestone 1)

- [M1] Complete HTML element validation — feature/html-validation
- [M1] Enhance kts-* attribute testing — test/kts-attributes
- [M1] HTML auto-escape and unsafeHtml() — feature/html-escape
- [M1] HTMX-style attributes support — feature/htmx-attributes
- [M1] Tailwind pipeline rewrite — feature/tailwind-pipeline-rewrite
- [M1] Route groups and content negotiation — feature/router-polish
- [M1] SSR cache and streaming HTML — feature/ssr-cache-streaming
- [M1] Comprehensive getting started guide — docs/getting-started
- [M1] Client runtime performance optimization — perf/client-runtime
- [M1] CI/CD pipeline setup — chore/ci-pipeline

## Acceptance Checklist per Feature

- API surface documented with KDoc
- Unit/integration tests with >80% coverage
- Examples working and CI green
- Backward compatible or guarded by feature flags
- Performance benchmarks established

## Code Examples: Target Developer Experience

### Route groups with middleware
```kotlin
group("/api") {
    before(Auth::class)
    get("/me") { ok(json(me())) }
}
```

### SSR with caching
```kotlin
get("/home") {
    cache(key = "home:${'$'}{user.id}", ttl = 10.seconds) {
        ok(html(HomePage(user)))
    }
}
```

### Streaming HTML
```kotlin
ok(streamingHtml {
    chunk { +"<h1>Title</h1>" }
    chunk { +render(Header()) }
    chunk { +render(SlowSection()) }
})
```

### HTML escaping
```kotlin
div { +userInput }          // escaped by default
div { unsafeHtml(rawHtml) } // opt-out
```

### Hydration bridge
```kotlin
body {
    // ... HTML ...
    scriptRaw("""
        window.__VOID_STATE__ = ${'$'}{serialize(mapOf(
            "cart" to Snapshot(v = cartAtom.state.valueVersion, data = cartAtom.state.value)
        ))};
    """.trimIndent())
}
```

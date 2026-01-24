package test

import io.voidx.dto.ok
import io.voidx.html.metadata.Metadata
import io.voidx.html.metadata.metadata
import io.voidx.page.route
import java.util.*
import kotlin.test.*

class MetadataExtrasTests {
    @Test
    fun favicon_and_keywords_and_canonical_render() {
        val page =
            route("/") {
                GET {
                    ok("", mutableMapOf("Content-Type" to "text/plain"))
                }
            }
        val meta = Metadata(page)
        meta.favicon = "/favicon.ico" to "image/x-icon"
        meta.keywords = listOf("kotlin", "void")
        meta.canonical = "https://example.com/"

        val head = meta.render()
        assertTrue(head.contains("<link rel=\"icon\" href=\"/favicon.ico\" type=\"image/x-icon\">"))
        assertTrue(head.contains("name=\"keywords\""))
        assertTrue(head.contains("<link rel=\"canonical\" href=\"https://example.com/\">"))
    }

    @Test
    fun metadata_render_includes_all_fields() {
        val page = route("/test") { }
        val meta =
            metadata(page) {
                title = "Custom Title"
                description = "Custom Desc"
                favicon = "/fav.ico" to "image/x-icon"
                keywords = listOf("void", "kotlin")
                themeColor = "#000000"
                externalCss = mutableListOf("/app.css")
                externalJS = mutableMapOf("/app.js" to true, "/analytics.js" to false)
                rawTags.add("<meta name=\"custom\" content=\"val\">")
                pwa("", "#000000", "")
            }

        val rendered = meta.render()
        assertTrue(rendered.contains("<title>Custom Title</title>"))
        assertTrue(rendered.contains("content=\"Custom Desc\""))
        assertTrue(rendered.contains("href=\"/fav.ico\""))
        assertTrue(rendered.contains("content=\"void, kotlin\""))
        assertTrue(rendered.contains("content=\"#000000\""))
        assertTrue(rendered.contains("href=\"/app.css\""))
        assertTrue(rendered.contains("src=\"/app.js\" defer"))
        assertTrue(rendered.contains("src=\"/analytics.js\""))
        assertFalse(rendered.contains("/analytics.js\" defer"))
        assertTrue(rendered.contains("<meta name=\"custom\" content=\"val\">"))
    }

    @Test
    fun handleStyles_logic() {
        val page = route("/test") { }
        val styleUuid = UUID.randomUUID()

        // Case 1: externalCss is null
        val meta1 =
            metadata(page) {
                style = styleUuid
            }
        val rendered1 = meta1.render()
        assertTrue(rendered1.contains("/css/$styleUuid/styles.css"))

        // Case 2: externalCss is not null
        val meta2 =
            metadata(page) {
                externalCss = mutableListOf("/other.css")
                style = styleUuid
            }
        val rendered2 = meta2.render()
        assertTrue(rendered2.contains("/other.css"))
        assertTrue(rendered2.contains("/css/$styleUuid/styles.css"))
    }

    @Test
    fun external_js_defer_false_is_rendered_without_defer_and_order_preserved() {
        val page =
            route("/") {
                GET {
                    ok("", mutableMapOf("Content-Type" to "text/plain"))
                }
            }
        val meta = Metadata(page)
        meta.externalJS =
            linkedMapOf(
                "/js/first.js" to true, // deferred
                "/js/second.js" to false, // not deferred
            )

        val head = meta.render()

        // first.js should have defer
        assertTrue(head.contains("<script src=\"/js/first.js\" defer></script>"))

        // second.js should NOT have defer
        assertTrue(
            head.contains("<script src=\"/js/second.js\"></script>") ||
                head.contains("<script src=\"/js/second.js\" ></script>"),
        )

        // order preserved
        assertTrue(head.indexOf("first.js") < head.indexOf("second.js"))
    }

    @Test
    fun meta_name_property_and_http_equiv_all_render() {
        val page = route("/meta") { }
        val meta = Metadata(page)

        meta.meta("description", "desc")
        meta.metaProperty("og:type", "website")
        meta.metaHttpEquiv("refresh", "10")

        val head = meta.render()

        assertTrue(head.contains("name=\"description\""))
        assertTrue(head.contains("property=\"og:type\""))
        assertTrue(head.contains("http-equiv=\"refresh\""))
    }

    @Test
    fun viewport_helper_renders_expected_content() {
        val page = route("/viewport") { }
        val meta = Metadata(page)

        meta.viewport()

        val head = meta.render()
        assertTrue(head.contains("name=\"viewport\""))
        assertTrue(head.contains("width=device-width"))
        assertTrue(head.contains("initial-scale=1.0"))
    }

    @Test
    fun viewport_custom_values_render_correctly() {
        val page = route("/viewport-custom") { }
        val meta = Metadata(page)

        meta.viewport(width = "1024", initialScale = 2.0)

        val head = meta.render()
        assertTrue(head.contains("width=1024"))
        assertTrue(head.contains("initial-scale=2.0"))
    }

    @Test
    fun link_tag_with_extra_attributes_is_rendered() {
        val page = route("/link") { }
        val meta = Metadata(page)

        meta.link(
            rel = "preconnect",
            href = "https://cdn.example.com",
            attrs = mapOf("crossorigin" to "anonymous"),
        )

        val head = meta.render()
        assertTrue(head.contains("rel=\"preconnect\""))
        assertTrue(head.contains("crossorigin=\"anonymous\""))
    }

    @Test
    fun script_inline_only_renders_without_src() {
        val page = route("/script-inline") { }
        val meta = Metadata(page)

        meta.script(inline = "console.log('hi')")

        val head = meta.render()
        assertTrue(head.contains("<script>console.log('hi')</script>"))
    }

    @Test
    fun script_with_src_and_attributes_renders_correctly() {
        val page = route("/script-src") { }
        val meta = Metadata(page)

        meta.script(
            src = "/test.js",
            attrs = mapOf("defer" to "defer", "type" to "module"),
        )

        val head = meta.render()
        assertTrue(head.contains("src=\"/test.js\""))
        assertTrue(head.contains("defer=\"defer\""))
        assertTrue(head.contains("type=\"module\""))
    }

    @Test
    fun twitter_card_helper_adds_all_expected_meta_tags() {
        val page = route("/twitter") { }
        val meta = Metadata(page)

        meta.twitterCard(
            title = "Title",
            description = "Desc",
            image = "https://example.com/image.png",
        )

        val head = meta.render()
        assertTrue(head.contains("name=\"twitter:card\""))
        assertTrue(head.contains("name=\"twitter:title\""))
        assertTrue(head.contains("name=\"twitter:description\""))
        assertTrue(head.contains("name=\"twitter:image\""))
    }

    @Test
    fun pwa_helper_registers_meta_and_manifest_link() {
        val page = route("/pwa") { }
        val meta = Metadata(page)

        meta.pwa(
            name = "My App",
            themeColor = "#abcdef",
            manifest = "/manifest.json",
        )

        val head = meta.render()
        assertTrue(head.contains("name=\"application-name\""))
        assertTrue(head.contains("name=\"theme-color\""))
        assertTrue(head.contains("apple-mobile-web-app-capable"))
        assertTrue(head.contains("href=\"/manifest.json\""))
    }

    @Test
    fun content_security_policy_helper_renders_http_equiv_meta() {
        val page = route("/csp") { }
        val meta = Metadata(page)

        meta.contentSecurityPolicy("default-src 'self'")

        val head = meta.render()
        assertTrue(head.contains("http-equiv=\"Content-Security-Policy\""))
        assertTrue(head.contains("default-src"))
    }

    @Test
    fun inline_style_blocks_are_rendered_in_head() {
        val page = route("/style") { }
        val meta = Metadata(page)

        meta.style("body { color: red; }")

        val head = meta.render()
        assertTrue(head.contains("<style>body { color: red; }</style>"))
    }

    @Test
    fun html_is_escaped_in_meta_content_and_attributes() {
        val page = route("/escape") { }
        val meta = Metadata(page)

        meta.meta("description", "<b>bad</b>")
        meta.link("icon", "\"onerror\"", mapOf("data" to "<xss>"))

        val head = meta.render()

        assertFalse(head.contains("<b>bad</b>"))
        assertTrue(head.contains("&lt;b&gt;bad&lt;/b&gt;"))
        assertTrue(head.contains("&quot;onerror&quot;"))
        assertTrue(head.contains("&lt;xss&gt;"))
    }

}

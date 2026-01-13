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
        val meta = metadata(page) {
            title = "Custom Title"
            description = "Custom Desc"
            favicon = "/fav.ico" to "image/x-icon"
            keywords = listOf("void", "kotlin")
            themeColor = "#000000"
            siteVerification = "google123"
            externalCss = mutableListOf("/app.css")
            externalJS = mutableMapOf("/app.js" to true, "/analytics.js" to false)
            rawTags.add("<meta name=\"custom\" content=\"val\">")
        }

        val rendered = meta.render()
        assertTrue(rendered.contains("<title>Custom Title</title>"))
        assertTrue(rendered.contains("content=\"Custom Desc\""))
        assertTrue(rendered.contains("href=\"/fav.ico\""))
        assertTrue(rendered.contains("content=\"void, kotlin\""))
        assertTrue(rendered.contains("content=\"#000000\""))
        assertTrue(rendered.contains("content=\"google123\""))
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
        val meta1 = metadata(page) {
            style = styleUuid
        }
        val rendered1 = meta1.render()
        assertTrue(rendered1.contains("/css/$styleUuid/styles.css"))

        // Case 2: externalCss is not null
        val meta2 = metadata(page) {
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
}

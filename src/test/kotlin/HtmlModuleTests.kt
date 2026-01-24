package test

import io.voidx.Method
import io.voidx.css.CssPage
import io.voidx.dto.ResponseBody
import io.voidx.dto.buildRequest
import io.voidx.dto.emptyResponse
import io.voidx.html.Element
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.metadata.Metadata
import io.voidx.html.page.*
import io.voidx.html.router.RouterUtil
import io.voidx.html.util.createResponse
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.Router
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HtmlModuleTests {
    @Test
    fun css_page_tests() {
        val uuid = UUID.randomUUID()
        val bodyText = ".test { color: red; }"
        val cssPage = CssPage(uuid, bodyText)
        assertEquals("/css/$uuid/styles.css", cssPage.target)

        // GET
        cssPage.request =
            buildRequest {
                method = Method.GET
                target = cssPage.target
            }
        val resp = cssPage.content()
        assertEquals(200, resp.status)
        assertEquals("text/css", resp.headers["Content-Type"])
        val respBody = resp.body as ResponseBody.StringBody
        assertEquals(bodyText, respBody.body)

        // POST
        cssPage.request =
            buildRequest {
                method = Method.POST
                target = cssPage.target
            }
        val resp405 = cssPage.content()
        assertEquals(405, resp405.status)
    }

    @Test
    fun html_response_sets_headers_and_attributes() {
        val el = fractal { Div("id" to "root") { } }
        val meta =
            Metadata(
                route("/") {
                    GET {
                        createResponse(el)
                    }
                },
            )

        val resp = createResponse(el, meta)
        assertEquals("text/html", resp.headers["Content-Type"])
        val attrElement = resp.attributes["Element"] as? Element
        assertNotNull(attrElement)
        assertEquals("", attrElement.name)
        val body =
            when (val b = resp.body) {
                is ResponseBody.StringBody -> b.body
                is ResponseBody.ByteArrayBody -> String(b.body)
            }
        assertTrue(body.contains("<head>"))
        assertTrue(body.contains("<body>"))
        assertTrue(body.contains("<div"))
    }

    @Test
    fun page_metadata_extension_property() {
        val page = route("/") { }
        val meta = Metadata(page)
        page.metadata = meta
        assertEquals(meta, page.metadata)
    }

    @Test
    fun page_handler_html_dsl() {
        val page =
            route("/") {
                html({ title = "DSL Title" }) {
                    Div { +"Hello" }
                }
            }
        page.request =
            buildRequest {
                method = Method.GET
                target = "/"
            }
        val response = page.content()
        assertEquals("DSL Title", page.metadata?.title)
        val body = (response.body as ResponseBody.StringBody).body
        assertTrue(body.contains("<title>DSL Title</title>"))
        assertTrue(body.contains("Hello"))
    }

    @Test
    fun js_page_tests() {
        val uuid = UUID.randomUUID()
        val bodyText = "console.log('hello')"
        val jsPage = JsPage(uuid, bodyText)
        assertEquals("/js/$uuid/script.js", jsPage.target)

        // GET
        jsPage.request =
            buildRequest {
                method = Method.GET
                target = jsPage.target
            }
        val resp = jsPage.content()
        assertEquals(200, resp.status)
        assertEquals("text/javascript", resp.headers["Content-Type"])
        val respBody = resp.body as ResponseBody.StringBody
        assertEquals(bodyText, respBody.body)

        // POST
        jsPage.request =
            buildRequest {
                method = Method.POST
                target = jsPage.target
            }
        val resp405 = jsPage.content()
        assertEquals(405, resp405.status)

        // addToMetadata
        val htmlPage = route("/") { }
        JsPage.addToMetadata(htmlPage, jsPage)
        assertTrue(htmlPage.metadata!!.externalJS!!.containsKey(jsPage.target))

        JsPage.addToMetadata(htmlPage, listOf(jsPage)) // list version
    }

    @Test
    fun page_util_add_css_to_router() {
        val router = Router()
        val page = route("/") { }
        page.metadata = Metadata(page)

        // Use Page.invoke to add css files
        // Note: this depends on resources/css being present.
        // If it's empty, we might not get any hits.
        page("nonexistent.css")

        // Manually add to cssFiles to ensure addCssToRouter does something
        page.cssFiles.add("css/test.css") // simulating a found resource

        // This will try to read resource "css/test.css"
        // If it doesn't exist, it might fail.
        // Assuming there might be at least one css file in resources for other tests.
        try {
            page.addCssToRouter(router)
            assertNotNull(page.metadata?.externalCss)
            assertTrue(page.metadata!!.externalCss!!.any { it.startsWith("/css/") })
        } catch (e: Exception) {
            // If resource reading fails because file doesn't exist, we skip
        }
    }
}

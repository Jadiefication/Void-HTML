package test

import io.voidx.Method
import io.voidx.css.CssPage
import io.voidx.css.TailwindGen
import io.voidx.css.TailwindString
import io.voidx.css.containsKey
import io.voidx.css.get
import io.voidx.dto.buildRequest
import io.voidx.fetch
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.page.classAttributes
import io.voidx.html.page.html
import io.voidx.html.page.metadata
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.Router
import io.voidx.router.router
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TailwindGenTests {
    @Test
    fun tailwind_gen_actually_compiles_css() {
        val page =
            route("/") {
                html({}) {
                    Div(
                        "class" to
                            """
                            flex items-center
                            hover:bg-red-500
                            md:p-4
                            mb-[7px]
                            sm:hover:mt-[2rem]
                            -p-[1px]
                            unknown-[10px]
                            """.trimIndent(),
                    ) { }
                }
            }

        val router =
            router {
                route(page)
            }

        val cssPage =
            router.routes
                .filter { it.value is CssPage }
                .values
                .first()
                .apply {
                    request = buildRequest { Method.GET }
                }

        val css =
            cssPage
                .content()
                .body.body as String

        assertNotNull(page.metadata?.style)
        assertTrue((page.content().body.body as String).contains(cssPage.target))

        // ✅ real assertions
        assertTrue(css.contains(".flex"))
        assertTrue(css.contains("display:flex"))

        assertTrue(css.contains(".hover\\:bg-red-500:hover"))
        assertTrue(css.contains("background-color"))

        assertTrue(css.contains("@media (min-width: 640px)"))
        assertTrue(css.contains(".md\\:p-4"))

        assertTrue(css.contains(".mb-\\[7px\\]"))
        assertTrue(css.contains("margin-bottom: 7px"))

        assertTrue(css.contains(".-p-\\[1px\\]"))
        assertTrue(page.metadata?.style != null)

        // ❌ unknown utility must NOT compile
        assertTrue(!css.contains("unknown-[10px]"))
    }

    @Test
    fun tailwind_gen_helpers_coverage() {
        // Exercise helper methods
        val list = listOf("a" to 1, "b" to 2)
        assertTrue(list.containsKey("a"))
        assertEquals(1, list["a"])
    }

    @Test
    fun tailwind_string_delegate_coverage() {
        val page = route("/ts") { }
        val ts = TailwindString("bg-blue-500")

        // Use a real property via reflection
        val property = Page::target
        val result = ts.provideDelegate(page, property)

        assertEquals("bg-blue-500", result)
        assertTrue(page.classAttributes.contains("bg-blue-500"))
    }
}

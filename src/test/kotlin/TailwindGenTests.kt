package test

import io.voidx.Method
import io.voidx.css.TailwindGen
import io.voidx.css.TailwindString
import io.voidx.css.containsKey
import io.voidx.css.get
import io.voidx.dto.buildRequest
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.page.html
import io.voidx.html.page.metadata
import io.voidx.page.Page
import io.voidx.page.route
import io.voidx.router.Router
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TailwindGenTests {

    @Test
    fun tailwind_gen_full_coverage() {
        val router = Router()
        val page = route("/") {
            html({}) {
                Div("class" to "flex items-center hover:bg-red-500 md:p-4 mb-[7px] sm:hover:mt-[2rem] -p-[1px] my-[5%] unknown-[10px] m-[1px] p-[1px] mt-[1px] pt-[1px] mr-[1px] pr-[1px] ml-[1px] pl-[1px] mx-[1px] px-[1px] py-[1px] container") {
                    Div("class" to "p-2 m-1") { }
                }
            }
        }
        
        // Populate resourceFile with some mock CSS to test extraction
        val field = TailwindGen::class.java.getDeclaredField("resourceFile")
        field.isAccessible = true
        field.set(TailwindGen, """
            html { height: 100% }
            .flex { display: flex }
            @media (min-width: 768px) {
              .md\:p-4 { padding: 1rem }
            }
        """.trimIndent())

        // Ensure request is set so page.content() works (many implementations depend on it)
        page.request = buildRequest { 
            method = Method.GET
            target = "/"
        }

        TailwindGen.processTailwind(page, router)
        
        assertNotNull(page.metadata?.style)
        
        // Trigger handleMetadataAdding with null metadata
        val pageNoMeta = route("/nometa") {
            html({}) { Div("class" to "flex") { } }
        }
        pageNoMeta.request = page.request
        TailwindGen.processTailwind(pageNoMeta, router)
        assertNotNull(pageNoMeta.metadata?.style)

        // Case with no classes
        val pageNoClasses = route("/noclasses") {
            html({}) { }
        }
        pageNoClasses.request = page.request
        TailwindGen.processTailwind(pageNoClasses, router)
        
        // Assertions
        assertNotNull(page.metadata?.style)

        // Exercise handleElements with multiple children
        val nestedPage = route("/nested") {
            html({}) {
                Div("class" to "c1") {
                    Div("class" to "c2") {
                        Div("class" to "c3") { }
                    }
                }
            }
        }
        nestedPage.request = page.request
        TailwindGen.processTailwind(nestedPage, router)
        assertTrue(nestedPage.classAttributes.contains("c3"))
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

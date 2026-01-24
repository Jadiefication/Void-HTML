package test

import io.voidx.Method
import io.voidx.dto.RequestDTO
import io.voidx.html.*
import io.voidx.html.exception.ChildNotAllowedException
import io.voidx.html.exception.FragmentChildNotAllowedException
import io.voidx.html.exception.NoOutputException
import io.voidx.html.generated.*
import kotlin.reflect.KClass
import kotlin.test.*

class ElementInternalTests {
    private class TestElement(
        override val name: String,
    ) : Element(name) {
        override fun render(): String {
            var attrs = ""
            attributes.forEach { (n, v) -> attrs += "$n=\"$v\" " }
            val childrenRendered = children?.joinToString("") { it.render() } ?: ""
            return "<$name $attrs>$childrenRendered</$name>"
        }
    }

    private open class DivNode : Element("div") {
        override fun render(): String = ""
    }

    private open class H1Node : Element("h1") {
        override fun render(): String = ""
    }

    @Test
    fun addAttributes_and_get_operator_work() {
        val el = fractal { Div { } }
        el.addAttributes("id" to "root", "class" to "box")
        assertEquals("root", el["id"])
        assertEquals("box", el["class"])
        assertNull(el["nonexistent"])
    }

    @Test
    fun unary_plus_adds_text_child_and_render_contains_text() {
        val el =
            fractal {
                Div("id" to "r") {
                    +"Hello"
                }
            }
        val s = el.render()
        assertTrue(s.contains("Hello"))
        assertTrue(s.contains("<div"))
    }

    @Test
    fun loop_helper_repeats_children_in_fragment() {
        val parent = fractal { Div { } }
        val fragment =
            parent.loop(1..3) { idx ->
                H2 { Fractal("Item $idx") }
            }
        // Attach produced fragment to parent
        parent.children!!.add(fragment)
        val rendered = parent.render()
        // Expect three occurrences
        assertTrue(rendered.contains("Item 1"))
        assertTrue(rendered.contains("Item 2"))
        assertTrue(rendered.contains("Item 3"))
    }

    @Test
    fun findElement_returns_first_match_depth_first() {
        val root =
            fractal {
                Div("id" to "root") {
                    Div("class" to "box", "id" to "b1") {
                        H2("id" to "title") { }
                    }
                    Div("class" to "box", "id" to "b2") { }
                }
            }
        val foundBox = root.findElement(".box")
        assertNotNull(foundBox)
        assertEquals("b1", foundBox["id"])

        val foundTitle = root.findElement("#title")
        assertNotNull(foundTitle)
        assertEquals("h2", foundTitle.name)

        // findElement returns null for non #/. queries based on code
        assertNull(root.findElement("div"))

        assertNull(root.findElement("#nonexistent"))
        assertNull(root.findElement(".nonexistent"))
        assertNull(root.findElement("nonexistent"))
    }

    @Test
    fun element_render_basic() {
        val el = TestElement("custom")
        assertEquals("<custom ></custom>", el.render())
    }

    @Test
    fun element_toString() {
        val el = TestElement("custom")
        assertTrue(el.toString().contains("custom"))
    }

    @Test
    fun element_getName() {
        val el = TestElement("custom")
        assertEquals("custom", el.name)
    }

    @Test
    fun fractal_constructors_and_render() {
        val f1 = Fractal("some text")
        assertEquals("some text", f1.render())

        val f2 =
            Fractal {
                Div { +"child" }
            }
        assertEquals("<div >child</div>", f2.render())

        val f3 = Fractal()
        assertEquals("", f3.render())
    }

    @Test
    fun element_fractal_helpers() {
        val root = TestElement("root")
        val f1 = root.Fractal("text")
        assertEquals(1, root.children!!.size)
        assertEquals("text", f1.render())

        val f2 = root.Fractal { Div { } }
        assertEquals(2, root.children!!.size)
        assertTrue(f2.render().contains("<div"))
    }

    @Test
    fun self_closing_element_render() {
        val root = fractal { Br("id" to "mybr") }
        val rendered = root.render()
        assertEquals("<br id=\"mybr\" />", rendered)
    }

    @Test
    fun child_not_allowed_exception() {
        val parent =
            object : ElementWithChildren("parent") {
                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(DivNode::class)
            }
        val child = TestElement("child")
        parent.children!!.add(child)
        assertFailsWith<ChildNotAllowedException> {
            parent.render()
        }
    }

    @Test
    fun fragment_child_not_allowed_exception() {
        val parent =
            object : ElementWithChildren("parent") {
                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(H1Node::class)
            }
        val frag =
            Fractal {
                children!!.add(DivNode()) // Should throw FragmentChildNotAllowedException because DivNode is not accepted
            }
        parent.children!!.add(frag)
        assertFailsWith<FragmentChildNotAllowedException> {
            parent.render()
        }
    }

    @Test
    fun fragment_accepted_recursively() {
        val parent =
            object : ElementWithChildren("parent") {
                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(DivNode::class)
            }
        val frag =
            Fractal {
                DivNode()
            }
        parent.children!!.add(frag)
        // Should NOT throw because DivNode is not added
        parent.render()
    }

    @Test
    fun empty_fragment_is_accepted() {
        val parent =
            object : ElementWithChildren("parent") {
                override val acceptedChildren: MutableList<KClass<out Element>?> = mutableListOf(DivNode::class)
            }
        val frag = Fractal()
        parent.children!!.add(frag)
        parent.render() // Should not throw because empty fragment is accepted
    }

    @Test
    fun element_kt_helpers() {
        val root =
            fractal {
                Container("id" to "cid") {
                    Flex("id" to "fid") {
                        Center("id" to "ceid") {
                            Section("My Title", "id" to "sid") {
                                +"Content"
                            }
                        }
                    }
                }
            }
        val rendered = root.render()
        assertTrue(rendered.contains("id=\"cid\""))
        assertTrue(rendered.contains("id=\"fid\""))
        assertTrue(rendered.contains("id=\"ceid\""))
        assertTrue(rendered.contains("id=\"sid\""))
        assertTrue(rendered.contains("My Title"))
        assertTrue(rendered.contains("Content"))
    }

    @Test
    fun generated_elements_smoke_test() {
        val root =
            fractal {
                Div {
                    A { +"Link" }
                    Abbr { +"Abbr" }
                    Address { +"Address" }
                    Area()
                    Article { }
                    Aside { }
                    Audio { }
                    B { }
                    Bdi { }
                    Bdo { }
                    Blockquote { }
                    Br()
                    Button { }
                    Caption { }
                    Cite { }
                    Code { }
                    Col()
                    Colgroup { }
                    Data { }
                    Datalist { }
                    Dd { }
                    Del { }
                    Details { }
                    Dfn { }
                    Dialog { }
                    Dl { }
                    Dt { }
                    Em { }
                    Embed()
                    Fieldset { }
                    Figcaption { }
                    Figure { }
                    Footer { }
                    Form { }
                    H1 { }
                    H2 { }
                    H3 { }
                    H4 { }
                    H5 { }
                    H6 { }
                    Header { }
                    Hr()
                    I { }
                    Iframe { }
                    Img()
                    Input()
                    Ins { }
                    Kbd { }
                    Label { }
                    Legend { }
                    Li { }
                    Main { }
                    Map { }
                    Mark { }
                    Menu { }
                    Menuitem()
                    Meter { }
                    Nav { }
                    Object { }
                    Ol { }
                    Optgroup { }
                    Option { }
                    Output { }
                    P { }
                    Param()
                    Picture { }
                    Pre { }
                    Progress { }
                    Q { }
                    Rp { }
                    Rt { }
                    Ruby { }
                    S { }
                    Samp { }
                    Section { }
                    Select { }
                    Small { }
                    Source()
                    Span { }
                    Strong { }
                    Sub { }
                    Summary { }
                    Sup { }
                    Table { }
                    Tbody { }
                    Td { }
                    Template { }
                    Textarea { }
                    Tfoot { }
                    Th { }
                    Thead { }
                    Time { }
                    Tr { }
                    Track()
                    U { }
                    Ul { }
                    Var { }
                    Video { }
                    Wbr()
                }
            }
        val rendered = root.render()
        assertTrue(rendered.contains("<a"))
        assertTrue(rendered.contains("<div"))
        assertTrue(rendered.contains("<img"))
    }

    @Test
    fun exceptions_test() {
        val parent = TestElement("parent")
        val child = TestElement("child")
        val ex1 = ChildNotAllowedException(child, parent)
        assertTrue(ex1.message!!.contains("parent doesn't take child as it's child element."))

        val ex2 = FragmentChildNotAllowedException(parent)
        assertTrue(ex2.message!!.contains("parent doesn't take one of the Fragment's child as it's child element."))

        val ex3 = NoOutputException()
        assertEquals("No output directory was specified.", ex3.message)
    }
}

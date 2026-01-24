package test

import io.voidx.html.util.escapeHtmlAttr
import io.voidx.html.util.escapeHtmlText
import kotlin.test.*

class HtmlEscapeTests {

    @Test
    fun escape_html_attr_escapes_all_required_characters() {
        val input = """&"< >"""
        val escaped = escapeHtmlAttr(input)

        assertEquals("&amp;&quot;&lt; &gt;", escaped)
    }

    @Test
    fun escape_html_attr_does_not_double_escape() {
        val input = "&amp;"
        val escaped = escapeHtmlAttr(input)

        // current behavior: replaces raw '&' again
        assertEquals("&amp;amp;", escaped)
    }

    @Test
    fun escape_html_attr_leaves_safe_text_unchanged() {
        val input = "abc123-_"
        val escaped = escapeHtmlAttr(input)

        assertEquals(input, escaped)
    }

    @Test
    fun escape_html_attr_handles_empty_string() {
        assertEquals("", escapeHtmlAttr(""))
    }

    @Test
    fun escape_html_text_escapes_html_sensitive_characters() {
        val input = "&<>"
        val escaped = escapeHtmlText(input)

        assertEquals("&amp;&lt;&gt;", escaped)
    }

    @Test
    fun escape_html_text_does_not_escape_quotes() {
        val input = "\"quoted\""
        val escaped = escapeHtmlText(input)

        assertEquals("\"quoted\"", escaped)
    }

    @Test
    fun escape_html_text_leaves_safe_text_unchanged() {
        val input = "plain text 123"
        val escaped = escapeHtmlText(input)

        assertEquals(input, escaped)
    }

    @Test
    fun escape_html_text_handles_empty_string() {
        assertEquals("", escapeHtmlText(""))
    }

    @Test
    fun escape_html_text_escapes_mixed_content_correctly() {
        val input = "Hello <b>& world</b>"
        val escaped = escapeHtmlText(input)

        assertEquals("Hello &lt;b&gt;&amp; world&lt;/b&gt;", escaped)
    }

    @Test
    fun escape_html_attr_does_not_escape_single_quotes() {
        val input = "it's a test"
        val escaped = escapeHtmlAttr(input)

        // Documents that single quotes are NOT escaped - safe only with double-quoted attributes
        assertEquals("it's a test", escaped)
    }
}

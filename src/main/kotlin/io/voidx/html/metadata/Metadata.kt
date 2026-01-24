package io.voidx.html.metadata

import io.voidx.html.util.escapeHtmlAttr
import io.voidx.html.util.escapeHtmlText
import io.voidx.page.Page
import java.net.InetAddress
import java.nio.charset.Charset
import java.util.*

data class MetaTag(
    val name: String? = null,
    val property: String? = null,
    val httpEquiv: String? = null,
    val content: String,
) {
    /**
     * Render this MetaTag as an HTML `<meta>` element.
     *
     * Only includes the `name`, `property`, and `http-equiv` attributes when their corresponding values are present; `content` is always included.
     *
     * `@return` A `String` containing the rendered `<meta>` element.
     */
    fun render(): String =
        buildString {
            append("<meta ")
            name?.let { append("name=\"${escapeHtmlAttr(it)}\" ") }
            property?.let { append("property=\"${escapeHtmlAttr(it)}\" ") }
            httpEquiv?.let { append("http-equiv=\"${escapeHtmlAttr(it)}\" ") }
            append("content=\"${escapeHtmlAttr(content)}\">")
        }
}

data class LinkTag(
    val rel: String,
    val href: String,
    val attrs: Map<String, String> = emptyMap(),
) {
    /**
     * Builds an HTML `<link>` element string using the tag's `rel`, `href`, and any additional attributes.
     *
     * `@return` A string containing a complete `<link>` tag with `rel`, `href`, and the provided attributes.
     */
    fun render(): String =
        buildString {
            append("<link rel=\"${escapeHtmlAttr(rel)}\" href=\"${escapeHtmlAttr(href)}\"")
            if (attrs.isNotEmpty()) {
                append(" ")
                append(attrs.entries.joinToString(" ") { "${escapeHtmlAttr(it.key)}=\"${escapeHtmlAttr(it.value)}\"" })
            }
            append(">")
        }
}

data class ScriptTag(
    val src: String? = null,
    val inline: String? = null,
    val attrs: Map<String, String> = emptyMap(),
) {
    /**
     * Produces an HTML <script> element incorporating the optional `src`, any provided attributes, and optional inline script content.
     *
     * `@return` The complete script tag as an HTML string.
     */
    fun render(): String =
        buildString {
            append("<script ")
            src?.let { append("src=\"${escapeHtmlAttr(it)}\" ") }
            attrs.forEach { (k, v) -> append("${escapeHtmlAttr(k)}=\"${escapeHtmlAttr(v)}\" ") }
            append(">")
            inline?.let { append(escapeHtmlText(it)) }
            append("</script>")
        }
}

/**
 * Describes HTML document metadata for a [Page]: title, description, icons, social tags,
 * canonical URL, theme color, robots, external CSS/JS, and arbitrary raw tags.
 *
 * Instances are normally created via [metadata] DSL and rendered into the <head> by the router.
 */
class Metadata internal constructor(
    page: Page,
) {
    var title: String = "Void Page"
    var description: String = "This is the default description of a Void page"
    var favicon: Pair<String, String>? = null
    var keywords: List<String> = emptyList()
    var charset: Charset = Charsets.UTF_8
    var copyright: Pair<String, String> = "Void" to "© 2025 Void Page"

    private val hostAddress: String =
        runCatching { InetAddress.getLocalHost().hostAddress }
            .getOrElse { "localhost" }

    var og: Pair<Triple<String, String, String>, String> =
        Triple(
            title,
            description,
            "https://picsum.photos/seed/example/300/200",
        ) to "http://$hostAddress${page.target}"

    var canonical: String = "http://$hostAddress/"

    var themeColor: String = "#ffffff"
    var robotRules: String = "noindex nofollow"

    var externalCss: MutableList<String>? = null
    var externalJS: MutableMap<String, Boolean>? = null
    internal var style: UUID? = null

    val metaTags = mutableListOf<MetaTag>()
    val linkTags = mutableListOf<LinkTag>()
    val scriptTags = mutableListOf<ScriptTag>()
    val styleBlocks = mutableListOf<String>()
    val rawTags = mutableListOf<String>()

    /**
     * Adds a meta tag with a `name` attribute and `content` to the metadata collection.
     *
     * @param name The value for the meta tag's `name` attribute (e.g., "description", "viewport").
     * @param content The value for the meta tag's `content` attribute.
     */

    fun meta(
        name: String,
        content: String,
    ) {
        metaTags += MetaTag(name = name, content = content)
    }

    /**
     * Adds a meta tag with the given `property` attribute and `content` to the metadata collection.
     *
     * @param property The value for the meta tag's `property` attribute (e.g., "og:title").
     * @param content The value for the meta tag's `content` attribute.
     */
    fun metaProperty(
        property: String,
        content: String,
    ) {
        metaTags += MetaTag(property = property, content = content)
    }

    /**
     * Adds an HTTP-equiv meta tag to the metadata collection.
     *
     * @param httpEquiv The HTTP-EQUIV attribute name (e.g., "Content-Security-Policy", "refresh").
     * @param content The value for the HTTP-EQUIV attribute.
     */
    fun metaHttpEquiv(
        httpEquiv: String,
        content: String,
    ) {
        metaTags += MetaTag(httpEquiv = httpEquiv, content = content)
    }

    /**
     * Adds a link tag to the metadata collection.
     *
     * @param rel The link relationship (e.g., "stylesheet", "icon").
     * @param href The URL for the link.
     * @param attrs Additional attributes to include on the link tag (attribute name to value).
     */
    fun link(
        rel: String,
        href: String,
        attrs: Map<String, String> = emptyMap(),
    ) {
        linkTags += LinkTag(rel, href, attrs)
    }

    /**
     * Adds a script tag to the metadata with either an external `src` or inline JavaScript.
     *
     * @param src URL of an external script to include; omit to use `inline` instead.
     * @param inline Inline JavaScript content to embed inside the script tag; omit to use `src` instead.
     * @param attrs Additional attributes to include on the script tag (for example `"defer"` or `"async"`).
     */
    fun script(
        src: String? = null,
        inline: String? = null,
        attrs: Map<String, String> = emptyMap(),
    ) {
        scriptTags += ScriptTag(src, inline, attrs)
    }

    /**
     * Adds a CSS block to the metadata's collection of inline style blocks.
     *
     * @param css The CSS text to add as an inline style block.
     */
    fun style(css: String) {
        styleBlocks += css
    }

    /**
     * Adds a viewport meta tag configured for responsive layouts.
     *
     * @param width The viewport width value (e.g., "device-width"). Defaults to "device-width".
     * @param initialScale The initial zoom scale for the viewport. Defaults to 1.0.
     */

    fun viewport(
        width: String = "device-width",
        initialScale: Double = 1.0,
    ) {
        meta("viewport", "width=$width, initial-scale=$initialScale")
    }

    /**
     * Adds Twitter Card meta tags to the metadata for social preview optimization.
     *
     * @param card The Twitter card type (e.g., "summary_large_image", "summary", "app", "player").
     * @param title The title to display in the Twitter card.
     * @param description The description to display in the Twitter card.
     * @param image The absolute URL of the image to display in the Twitter card.
     */
    fun twitterCard(
        card: String = "summary_large_image",
        title: String,
        description: String,
        image: String,
    ) {
        meta("twitter:card", card)
        meta("twitter:title", title)
        meta("twitter:description", description)
        meta("twitter:image", image)
    }

    /**
     * Registers Progressive Web App metadata and a manifest link in the metadata DSL.
     *
     * @param name The application name exposed to the browser and device UI.
     * @param themeColor The theme color (e.g., "#ffffff") used by browsers and system UI.
     * @param manifest The URL or path to the web app manifest file.
     */
    fun pwa(
        name: String,
        themeColor: String,
        manifest: String,
    ) {
        meta("application-name", name)
        meta("theme-color", themeColor)
        meta("apple-mobile-web-app-capable", "yes")
        link("manifest", manifest)
    }

    /**
     * Adds a Content-Security-Policy `http-equiv` meta tag using the provided policy value.
     *
     * @param policy The Content Security Policy string to place in the meta tag (for example: "default-src 'self'; img-src https:;").
     */
    fun contentSecurityPolicy(policy: String) {
        metaHttpEquiv("Content-Security-Policy", policy)
    }

    /**
     * Renders the accumulated metadata into an HTML head fragment.
     *
     * Ensures any configured style resource is wired into external CSS before generating tags and then concatenates charset, title, standard meta tags, favicon/canonical/link/script tags, inline styles, external asset links, and any raw tags into a single string.
     *
     * @return A string containing the HTML elements for the document head metadata.
     */
    internal fun render(): String {
        handleStyles()

        return buildString {
            append("<meta charset=\"${escapeHtmlAttr(charset.toString())}\">")
            append("<title>${escapeHtmlAttr(title)}</title>")

            append("<meta name=\"description\" content=\"${escapeHtmlAttr(description)}\">")
            append("<meta name=\"keywords\" content=\"${escapeHtmlAttr(keywords.joinToString())}\">")
            append("<meta name=\"author\" content=\"${escapeHtmlAttr(copyright.first)}\">")
            append("<meta name=\"copyright\" content=\"${escapeHtmlAttr(copyright.second)}\">")
            append("<meta name=\"robots\" content=\"${escapeHtmlAttr(robotRules)}\">")
            append("<meta name=\"theme-color\" content=\"${escapeHtmlAttr(themeColor)}\">")

            favicon?.let {
                append("<link rel=\"icon\" href=\"${escapeHtmlAttr(it.first)}\" type=\"${escapeHtmlAttr(it.second)}\">")
            }

            append("<link rel=\"canonical\" href=\"${escapeHtmlAttr(canonical)}\">")

            val ogTags =
                listOf(
                    MetaTag(property = "og:title", content = og.first.first),
                    MetaTag(property = "og:description", content = og.first.second),
                    MetaTag(property = "og:image", content = og.first.third),
                    MetaTag(property = "og:url", content = og.second),
                )
            ogTags.forEach { append(it.render()) }

            metaTags.forEach { append(it.render()) }
            linkTags.forEach { append(it.render()) }

            externalCss?.forEach {
                append("<link rel=\"stylesheet\" href=\"${escapeHtmlAttr(it)}\">")
            }

            styleBlocks.forEach {
                append("<style>${escapeHtmlAttr(it)}</style>")
            }

            scriptTags.forEach { append(it.render()) }

            externalJS?.forEach { (src, defer) ->
                append("<script src=\"${escapeHtmlAttr(src)}\" ${if (defer) "defer" else ""}></script>")
            }

            rawTags.forEach { append(it) }
        }
    }

    /**
     * Ensures the configured style UUID is wired into the externalCss list.
     *
     * If `style` is null this is a no-op. Otherwise it adds `/css/{styleId}/styles.css`
     * to `externalCss`, initializing the list if necessary.
     */
    private fun handleStyles() {
        val styleId = style ?: return
        if (externalCss == null) {
            externalCss = mutableListOf("/css/$styleId/styles.css")
        } else {
            externalCss!!.add("/css/$styleId/styles.css")
        }
    }
}

/**
 * DSL entry point to create [Metadata] for the given [page] using [builder].
 */
fun metadata(
    page: Page,
    builder: Metadata.() -> Unit,
): Metadata {
    val metadata = Metadata(page)
    metadata.apply(builder)
    return metadata
}

package io.voidx.html.metadata

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
    fun render(): String =
        buildString {
            append("<meta ")
            name?.let { append("name=\"$it\" ") }
            property?.let { append("property=\"$it\" ") }
            httpEquiv?.let { append("http-equiv=\"$it\" ") }
            append("content=\"$content\">")
        }
}

data class LinkTag(
    val rel: String,
    val href: String,
    val attrs: Map<String, String> = emptyMap(),
) {
    fun render(): String =
        "<link rel=\"$rel\" href=\"$href\" " +
            attrs.entries.joinToString(" ") { "${it.key}=\"${it.value}\"" } +
            ">"
}

data class ScriptTag(
    val src: String? = null,
    val inline: String? = null,
    val attrs: Map<String, String> = emptyMap(),
) {
    fun render(): String =
        buildString {
            append("<script ")
            src?.let { append("src=\"$it\" ") }
            attrs.forEach { (k, v) -> append("$k=\"$v\" ") }
            append(">")
            inline?.let { append(it) }
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

    // ---------- DSL helpers ----------

    fun meta(
        name: String,
        content: String,
    ) {
        metaTags += MetaTag(name = name, content = content)
    }

    fun metaProperty(
        property: String,
        content: String,
    ) {
        metaTags += MetaTag(property = property, content = content)
    }

    fun metaHttpEquiv(
        httpEquiv: String,
        content: String,
    ) {
        metaTags += MetaTag(httpEquiv = httpEquiv, content = content)
    }

    fun link(
        rel: String,
        href: String,
        attrs: Map<String, String> = emptyMap(),
    ) {
        linkTags += LinkTag(rel, href, attrs)
    }

    fun script(
        src: String? = null,
        inline: String? = null,
        attrs: Map<String, String> = emptyMap(),
    ) {
        scriptTags += ScriptTag(src, inline, attrs)
    }

    fun style(css: String) {
        styleBlocks += css
    }

    // ---------- Opinionated helpers ----------

    fun viewport(
        width: String = "device-width",
        initialScale: Double = 1.0,
    ) {
        meta("viewport", "width=$width, initial-scale=$initialScale")
    }

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

    fun contentSecurityPolicy(policy: String) {
        metaHttpEquiv("Content-Security-Policy", policy)
    }

    internal fun render(): String {
        handleStyles()

        return buildString {
            append("<meta charset=\"$charset\">")
            append("<title>$title</title>")

            append("<meta name=\"description\" content=\"$description\">")
            append("<meta name=\"keywords\" content=\"${keywords.joinToString()}\">")
            append("<meta name=\"author\" content=\"${copyright.first}\">")
            append("<meta name=\"copyright\" content=\"${copyright.second}\">")
            append("<meta name=\"robots\" content=\"$robotRules\">")
            append("<meta name=\"theme-color\" content=\"$themeColor\">")

            favicon?.let {
                append("<link rel=\"icon\" href=\"${it.first}\" type=\"${it.second}\">")
            }

            append("<link rel=\"canonical\" href=\"$canonical\">")

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
                append("<link rel=\"stylesheet\" href=\"$it\">")
            }

            styleBlocks.forEach {
                append("<style>$it</style>")
            }

            scriptTags.forEach { append(it.render()) }

            externalJS?.forEach { (src, defer) ->
                append("<script src=\"$src\" ${if (defer) "defer" else ""}></script>")
            }

            rawTags.forEach { append(it) }
        }
    }

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

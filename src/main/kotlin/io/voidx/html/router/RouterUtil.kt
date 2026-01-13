package io.voidx.html.router

import io.voidx.Method
import io.voidx.bootstrap.Bootstrap
import io.voidx.css.CssPage
import io.voidx.css.TailwindGen
import io.voidx.dto.buildRequest
import io.voidx.dto.emptyResponse
import io.voidx.html.Element
import io.voidx.html.page.JsPage
import io.voidx.html.page.KtsPage
import io.voidx.html.page.addCssToRouter
import io.voidx.html.page.metadata
import io.voidx.router.Router
import io.voidx.router.listResourcePaths
import io.voidx.util.readResourceText
import io.voidx.util.toResult
import java.util.*

/**
 * Wires the HTML module into the core runtime by registering integration hooks.
 *
 * Responsibilities:
 * - Expose a handler for KTS requests via [HtmlIntegration.getKtsPage].
 * - Discover and register embedded JS resources to [HtmlIntegration.jsPages].
 * - Provide a per-page hook [HtmlIntegration.handleJsAndCss] to attach CSS (Tailwind and external)
 *   and JS to newly added routes.
 */
object RouterUtil : Bootstrap.Module {

    override fun onRouterCreated(ctx: Bootstrap.Context) {
        val router = ctx.router
        Bootstrap.registerSpecialRoute { requestDTO, query, clientHandler ->
            val target = requestDTO.target
            val routes = router.routes
            val page = routes[target]
            if (page != null && page is KtsPage) {
                page.queries = query
                val route = requestDTO.headers["KTS-Route"]!!
                val content = routes[route]!!.content()
                val rootElement = content.attributes["Element"] as Element
                val triggerId = requestDTO["KTS-Trigger"]
                val targetId = requestDTO["KTS-Target"]
                val trigger = triggerId?.let { rootElement.findElement(it) }
                val targetEl = targetId?.let { rootElement.findElement(it) }
                page._target = targetEl
                page._trigger = trigger
                page.request = requestDTO

                page.middlewareProcessBefore(requestDTO.toResult())
                    ?: router.handleResponse(page, clientHandler, target)
            } else {
                null
            }
        }
    }
}

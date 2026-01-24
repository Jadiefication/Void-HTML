package test

import io.voidx.bootstrap.Bootstrap
import io.voidx.dto.ResponseDTO
import io.voidx.dto.buildRequest
import io.voidx.html.fractal
import io.voidx.html.generated.Div
import io.voidx.html.page.ktsRoute
import io.voidx.html.router.RouterUtil
import io.voidx.page.route
import io.voidx.router.Router
import io.voidx.router.router
import org.junit.jupiter.api.Test
import java.util.ServiceLoader
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RouterUtilTests {
    @Test
    fun `test special route handler in RouterUtil`() {
        val router = Router()

        // 1. Setup a regular page that will be referenced by KTS-Route
        val mainPage =
            route("/main") {
                GET {
                    val el =
                        fractal {
                            Div("id" to "root") {
                                Div("id" to "my-trigger") { +"Trigger" }
                                Div("id" to "my-target") { +"Target" }
                            }
                        }
                    io.voidx.html.util
                        .createResponse(el)
                }
            }
        router.addRoute(mainPage)

        // 2. Setup a KtsPage
        val ktsPage =
            ktsRoute("/kts-endpoint") { req, trigger, target ->
                fractal { Div { +"KTS Response" } }
            }
        router.addRoute(ktsPage)

        // 3. Initialize RouterUtil using reflection to bypass internal constructor
        val contextClass = Bootstrap.Context::class.java
        val contextConstructor = contextClass.getDeclaredConstructors()[0]
        contextConstructor.isAccessible = true
        val context = contextConstructor.newInstance(router) as Bootstrap.Context

        ServiceLoader.load(Bootstrap.Module::class.java, Thread.currentThread().contextClassLoader).first { it is RouterUtil }.onRouterCreated(context)

        // 4. Access registered special route handlers via reflection
        val bootstrapClass = Bootstrap::class.java
        val handlersField = bootstrapClass.getDeclaredField("specialRoutes")
        handlersField.isAccessible = true
        val handlers = handlersField.get(null) as Set<*>

        val specialRoute = handlers.last()!!
        val getHandlerMethod = specialRoute::class.java.getDeclaredMethod("getHandler")
        getHandlerMethod.isAccessible = true
        val handlerLambda = getHandlerMethod.invoke(specialRoute) as Function3<*, *, *, *>

        // 5. Invoke the handler with full data
        val request =
            buildRequest {
                target = "/kts-endpoint"
                headers["KTS-Route"] = "/main"
                headers["KTS-Trigger"] = "my-trigger"
                headers["KTS-Target"] = "my-target"
            }
        mainPage.request = request

        // 6. Test with missing trigger/target ids
        val request2 =
            buildRequest {
                target = "/kts-endpoint"
                headers["KTS-Route"] = "/main"
            }

        // 7. Test with non-KtsPage target
        val request3 =
            buildRequest {
                target = "/main"
            }

        val invokeMethod =
            handlerLambda::class.java.methods.first {
                it.name == "invoke" && it.parameterCount == 3
            }
        invokeMethod.isAccessible = true

        val clientHandlerClass = Class.forName("io.voidx.ClientHandler")
        val rf = sun.reflect.ReflectionFactory.getReflectionFactory()
        val objConstructor = Any::class.java.getDeclaredConstructor()
        val specConstructor = rf.newConstructorForSerialization(clientHandlerClass, objConstructor)
        val clientHandler = specConstructor.newInstance()

        val response = invokeMethod.invoke(handlerLambda, request, emptyMap<String, List<String>>(), clientHandler) as ResponseDTO?
        assertNotNull(response)

        val response2 = invokeMethod.invoke(handlerLambda, request2, emptyMap<String, List<String>>(), clientHandler) as ResponseDTO?
        assertNotNull(response2)

        val response3 = invokeMethod.invoke(handlerLambda, request3, emptyMap<String, List<String>>(), clientHandler) as ResponseDTO?
        // Should return null because target is not a KtsPage
        kotlin.test.assertNull(response3)
        val bodyField = response.body::class.java.getDeclaredField("body")
        bodyField.isAccessible = true
        val bodyContent = bodyField.get(response.body).toString()
        println("[DEBUG_LOG] Response body actual content: $bodyContent")

        assertTrue(bodyContent.contains("KTS Response"))
    }

    @Test
    fun `RouterUtil being loaded`() {
        val modules = ServiceLoader.load(Bootstrap.Module::class.java, Thread.currentThread().contextClassLoader)
        assertTrue(modules.any { it::class == RouterUtil::class })
    }
}

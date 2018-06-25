import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import com.google.gson.Gson
import javafx.scene.web.WebHistory
import kotlinx.coroutines.experimental.newCoroutineContext
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.request.httpMethod
import org.jetbrains.ktor.response.header
import org.jetbrains.ktor.routing.post

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/api") {
                var headers = call.request.httpMethod == HttpMethod.Get
                var gson = Gson()
                var str = gson.toJson(headers)
                call.response.header("Access-Control-Allow-Origin", "*")
                call.respondText(str, ContentType.Application.Json)
            }
        }
    }
    embeddedServer(Netty, 8080) {
        routing {
            post("/api/create") {
                var body = call.request
            }
        }
    }
}

data class Entry(val message: String)
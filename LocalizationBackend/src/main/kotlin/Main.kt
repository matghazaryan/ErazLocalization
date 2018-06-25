import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import com.google.gson.Gson
import javafx.scene.web.WebHistory
import kotlinx.coroutines.experimental.newCoroutineContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.application.receive
import org.jetbrains.ktor.client.RequestBuilder
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.request.httpMethod
import org.jetbrains.ktor.response.header
import org.jetbrains.ktor.routing.post

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        routing {
            get("/api/database") {
                val queries = call.request.queryParameters
                val dbName = queries["name"]
                val client = OkHttpClient()
                val request = Request.Builder().url("https://localization-1be56.firebaseio.com/" + dbName + ".json")
                        .build()
                val response = client.newCall(request).execute()
                val str = response.body()?.string()
                call.response.header("Access-Control-Allow-Origin", "*")
                call.respondText(str.toString(), ContentType.Application.Json)
            }
            post("/api/{path}/") {
                val body = call.request.receive<String>()
                val path = call.parameters["path"]
                if (path.equals(Constants.CREATE.toString(), true)) {
                    val json = Gson().fromJson(body, HashMap<String, String>().javaClass)
                    var appAlias = json["app_alias"]

                    call.respondText(appAlias.toString())
                } else {
                    call.respond("Unknown path")
                }
            }
        }
    }.start(true)
}

data class Entry(val message: String)
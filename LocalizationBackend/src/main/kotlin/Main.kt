import com.google.api.client.json.Json
import com.google.common.net.MediaType
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
import okhttp3.RequestBody
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
                val str = FirebaseManager().getProjectData(dbName.toString())
                call.response.header("Access-Control-Allow-Origin", "*")
                call.respondText(str.toString(), ContentType.Application.Json)
            }
            post("/api/eraz-local/{path}/") {
                val body = call.request.receive<String>()
                val path = call.parameters["path"]
                //var JSON = MediaType.parse("application/json; charset=utf-8")
                if (path.equals(Constants.PATH.CREATE_PROJECT, true)) {
                    val reqBody = Gson().fromJson(body, HashMap<String, String>().javaClass)
                    val appAlias = reqBody["alias"]
                    val appName = reqBody["name"]
                    val url = "https://" + Constants.FIREBASE.PROJECT_ID + ".firebaseio.com/" + appAlias + ".json"
                    val json = Gson().toJson(hashMapOf("name" to "jack",
                            "surname" to "sparrow"))
                    val client = OkHttpClient()
                    val body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)
                    val request =  Request.Builder().url(url)
                            .post(body)
                            .build()
                    val response = client.newCall(request).execute()
                    call.respondText(response.body()?.string().toString())
                } else {
                    call.respond("Unknown path")
                }
            }
        }
    }.start(true)
}

data class Entry(val message: String)
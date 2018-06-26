import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

class FirebaseManager {
    val JSON = okhttp3.MediaType.parse("application/json; charset=utf-8")
    val client = OkHttpClient()
    fun createProject(name: String, alias: String, y: Any): String {
        val url = "https://" + Constants.FIREBASE.PROJECT_ID + ".firebaseio.com/projects/" + alias + ".json"
        val json = Gson().toJson(hashMapOf("name" to name,
                                                  "alias" to alias))
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder().url(url)
                .post(body)
                .build()
        val response = client.newCall(request).execute()
        return response.body()?.string().toString()
    }
    fun getProjectData(alias: String): String {
        val url = "https://" + Constants.FIREBASE.PROJECT_ID + ".firebaseio.com/" + alias + ".json"
        val request = Request.Builder().url(url)
                .build()
        val response = client.newCall(request).execute()
        val str = response.body()?.string()
        return str.toString()
    }
}
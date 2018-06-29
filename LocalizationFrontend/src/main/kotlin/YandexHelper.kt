import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Json
import kotlin.js.Promise

interface YandexHelper {
    companion object {
        fun translate(to: String, text: String, detection: Boolean = true, from: String = ""): Promise<String> {
            var fromLang = from
            if (detection) {
                fromLang = detectLanguage(text)
            }
            val url = URL(Constants.YANDEX.TRANSLATE_URL)
            val searchParams = URLSearchParams()
            searchParams.append("key", Constants.YANDEX.KEY)
            searchParams.append("text", text)
            searchParams.append("lang", "$fromLang-$to")
            searchParams.append("format", "plain")
            url.search = searchParams.toString()
            return Promise { res, rej ->
                val req = XMLHttpRequest()
                req.open("POST", url.toString(), true)
                req.onloadend = {
                    val response = JSON.parse<dynamic>(req.responseText)
                    if (response["code"] == "200") {
                        res((response["text"] as Array<String>).first())
                    } else {
                        rej(Throwable(response["message"].toString()))
                    }
                }
                req.onerror = {
                    rej(Throwable("error"))
                }
                req.send()
            }
        }

        fun detectLanguage(text: String): String {
            val url = URL(Constants.YANDEX.DETECTION_URL)
            val searchParams = URLSearchParams()
            searchParams.append("key", Constants.YANDEX.KEY)
            searchParams.append("text", text)
            url.search = searchParams.toString()
            val req = XMLHttpRequest()
            req.open("POST", url.toString(), false)
            var detectedLang = ""
            req.onloadend = {
                val response = JSON.parse<dynamic>(req.responseText)
                if (response["code"] == "200") {
                    detectedLang = response["lang"].toString()
                }
            }
            req.send()
            console.log(detectedLang)
            return detectedLang
        }

        private val supportedLanguages = hashMapOf<String, String>()

        fun supportedLanguages(ui: String = "en"): Promise<HashMap<String, String>> {
            return Promise { success, reject ->
                if (supportedLanguages.isNotEmpty()) {
                    success(supportedLanguages)
                }
                val url = URL(Constants.YANDEX.LANGUAGES_URL)
                val searchParams = URLSearchParams()
                searchParams.append("key", Constants.YANDEX.KEY)
                searchParams.append("ui", ui)
                url.search = searchParams.toString()
                val req = XMLHttpRequest()
                req.open("POST", url.toString(), true)
                req.onloadend = {
                    val response = JSON.parse<dynamic>(req.responseText)
                    if (response["code"] != null) {
                        reject(Throwable(response["message"].toString()))
                    }
                    val langs = response["langs"]
                    Object().keys(langs).forEach(fun(element: dynamic) {
                        supportedLanguages[element.toString()] = langs[element].toString()
                    })
                    success(supportedLanguages)
                }
                req.send()
            }
        }
    }
}

inline fun Object(): dynamic { return js("Object")}
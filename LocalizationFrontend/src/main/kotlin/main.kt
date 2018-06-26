import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json

fun main(args: Array<String>) {
    window.onload = {
        //fetch("1")
        console.log("WTF!!!?????")
//        //Example of how to add stylesheets dynamically
//        //add stylesheet if we have any
//        val head = document.getElementsByTagName("head")
//        head.get(0).appendChild(createStylesheetLink("style.css"))
        //bind elements
        /*val input = document.getElementById("count_id") as HTMLInputElement
         */
        var x = 0
        val button = document.getElementById("button_id")
        //bind click listener on button
        button?.addEventListener("click", fun(event: Event) {
            fetch()
        })

    }
}

fun fetch() {
    val url = "https://" + Constants.FIREBASE.PROJECT_ID + ".firebaseio.com/" + "arca" + ".json"
    val req = XMLHttpRequest()
    req.onload = fun(event: Event) {
        val text = req.responseText
        val json = JSON.parse<Json>(text)
        val textArea = document.getElementById("textarea_id") as HTMLTextAreaElement
        textArea.value = ""
        var response = mutableMapOf<String, Any>()
        var screens = mutableListOf<String>()
        for (key in js("Object").keys(json)) {
            // screen name is first layer
            screens.add(key)
        }
        response.put("screens", screens)
//        objectArray.forEach {
//            val message = it.key + ":" + it.value
//            textArea.value = message
//        }

        console.log(response)
     }
     req.open("GET", url, true)
     req.send()
}

fun createStylesheetLink(filePath: String): Element {
    val style = document.createElement("link")
    style.setAttribute("rel", "stylesheet")
    style.setAttribute("href", filePath)
    return style
}

external fun alert(message: Any?): Unit
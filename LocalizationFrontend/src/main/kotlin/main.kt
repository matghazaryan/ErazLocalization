import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.get
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json

external fun require(module:String):dynamic

// Բայց սոռտ է, մարդավարի ES5/ES6 ով կգրես, require('firebase/app') կամ import firebase from 'firebase'
// չի աշխատի, տողի վրա կգրես, կաշխատի 😂

var firebase: dynamic = js("firebase")
var dbRef = firebase.database().ref().child("projects")

fun main(args: Array<String>) {
    window.onload = {
        console.log("WTF!!!?????")
        // notify changes
        val onValueChange = fun (value: Any) {
            count = js("value.numChildren()") as Int
            console.log("count = $count")
            js("console.log(value.val())")
        }
//        dbRef.on(Constants.FIREBASE.contentType.VALUE, onValueChange)
        getProjects {
            it.forEach {
                val element = document.getElementById(it["alias"] as String)
                if (element != null) {
                    element?.let { it1 -> document.body?.removeChild(it1) }
                }
                var label = document.createElement("h1")
                label.id = it["alias"] as String
                label.innerHTML = it["name"] as String
                document.body?.appendChild(label)
            }
        }
        val button = document.getElementById("push_id")
        val name = document.getElementById("name_id") as HTMLInputElement
        val alias = document.getElementById("alias_id") as HTMLInputElement
        //bind click listener on button
        button?.addEventListener("click", fun(event: Event) {
            if (name != null && alias != null) {
                createProject(name.value, alias.value)
            }
        })

    }
}

var count = 0

fun createStylesheetLink(filePath: String): Element {
    val style = document.createElement("link")
    style.setAttribute("rel", "stylesheet")
    style.setAttribute("href", filePath)
    return style
}

external fun alert(message: Any?): Unit
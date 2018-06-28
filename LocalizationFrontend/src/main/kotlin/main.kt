import org.w3c.dom.*
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.StyleSheet
import org.w3c.dom.events.Event
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
            }
        }
        loadJSON {
            it.forEach {
                val div = document.createElement("div") as HTMLDivElement
                val checkbox = document.createElement("input") as HTMLInputElement
                checkbox.type = "checkbox"
                checkbox.id = it.key
                checkbox.onclick = {
                    console.log("pll")
                }
                val label = document.createElement("label") as HTMLLabelElement
                label.htmlFor = checkbox.id
                label.innerText = it.value
                div.append(label, checkbox)
                document.body?.appendChild(div)
            }
        }
        val button = document.getElementById("push_id")
        val name = document.getElementById("name_id") as HTMLInputElement
        val alias = document.getElementById("alias_id") as HTMLInputElement
        //bind click listener on button
        button?.addEventListener("click", fun(event: Event) {
            /*val json = js("langs")
            val array = arrayListOf<Pair<String, String>>()
            js("Object").keys(json).forEach(fun (key: String) {
                val checkBox = document.getElementById(key) as HTMLInputElement
                if (checkBox.checked) {
                    array.add(key to json[key])
                }
            })
            addLanguages("arca", array.toTypedArray())
            */
            getProject("arca", {
                console.log(it)
            })
        })
    }
}

var count = 0

/// Helpers

fun loadJSON(callBack: (HashMap<String, String>) -> Unit) {
    /*val req = XMLHttpRequest()
    req.overrideMimeType("application/json")
    req.open("GET", "./langs.json", true)
    req.onreadystatechange = {
        if (req.readyState == 4.toShort() && req.status == 200.toShort()) {
            // Required use of an anonymous callback as .open will NOT return a value but simply returns undefined in asynchronous mode
            val map = JSON.parse<HashMap<String, String>>(req.responseText)
            callBack(map)
        }
    }
    req.send()*/
    val json = js("langs")
    val map = hashMapOf<String, String>()
    js("Object").keys(json).forEach(fun (key: String) {
        map.put(key, json[key])
    })
    callBack(map)
}


fun createStylesheetLink(filePath: String): Element {
    val style = document.createElement("link")
    style.setAttribute("rel", "stylesheet")
    style.setAttribute("href", filePath)
    return style
}

external fun alert(message: Any?): Unit
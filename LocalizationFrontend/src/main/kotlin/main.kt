import org.w3c.dom.*
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.StyleSheet
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.Promise

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
        getProject("arca") {
            val languages = it["languages"] as Json
            js("Object").values(languages).forEach(fun (item: dynamic) {
                console.log(item["langName"] as? String)
            }) as Unit
        }
        val button = document.getElementById("push_id")
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
            YandexHelper.supportedLanguages().forEach {
                console.log("${it.key} = ${it.value}")
            }
        })
    }
}

var count = 0

/// Helpers

@Deprecated("As we use Yandex translate, so we need to support same languages like Yandex, use YandexHelper.supportedLanguages() instead", ReplaceWith("YandexHelper.supportedLanguages()"), DeprecationLevel.WARNING)
fun loadJSON(callBack: (HashMap<String, String>) -> Unit) {
    val json = js("langs")
    val map = hashMapOf<String, String>()
    js("Object").keys(json).forEach(fun (key: String) {
        map[key] = json[key] as String
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
external fun encodeURIComponent(uri: String): String
external fun encodeURI(uri: String): String
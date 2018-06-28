import org.w3c.dom.*
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

    if (window.location.href.contains("index.html", false)) {

        window.onload = {
            getProjects {
                val divProjects = document.getElementById("projects-collection") as HTMLDivElement

                // Add project card
                var innerHtml: String = "<div class=\"col s12 m3\">\n" +
                        "                    <div class=\"card\" data-alias=\"new-project\">\n" +
                        "                        <div class=\"card-container\">\n" +
                        "                            <img src=\"images/icon-plus.png\" alt=\"Add Project\" height=\"36\" width=\"36\">\n" +
                        "                            <p>Add Project</p>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                </div>"

                // Projects
                it.forEach {
                    innerHtml += "<div class=\"col s12 m3\">\n" +
                            "         <div class=\"card\" data-alias=\"${it["alias"] as String}\">\n" +
                            "             <div class=\"card-content black-text\">\n" +
                            "                 <span class=\"card-title\">${it["name"] as String}</span>\n" +
                            "                 <p>${it["alias"] as String}</p>\n" +
                            "                 <div class=\"platform-container\">\n" +
                            "                 <img src=\"images/icon-ios.png\" alt=\"iOS\" height=\"24\" width=\"24\">\n" +
                            "                 <img src=\"images/icon-android.png\" alt=\"Android\" height=\"24\" width=\"24\">\n" +
                            "                 <img src=\"images/icon-website.png\" alt=\"Web\" height=\"24\" width=\"24\">\n" +
                            "             </div>\n" +
                            "         </div>\n" +
                            "      </div>\n" +
                            "    </div>"
                }

                // Show projects
                divProjects.innerHTML = innerHtml

                // Adding click event listener
                for (node: Node in divProjects.childNodes.asList()) {
                    node.addEventListener("click", fun(event: Event) {
                        val element = node as HTMLDivElement
                        val cardElement = element.firstElementChild
                        if (cardElement != null) {
                            val projectAlias = cardElement.getAttribute("data-alias")
                            window.location.href = "file:///Users/grigorhakobyan/IdeaProjects/ErazLocalization/LocalizationFrontend/src/main/resources/project.html?alias=${projectAlias}"
                        }
                    })
                }
            }
        }
    } else if (window.location.href.contains("project.html")) {

    }
}

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



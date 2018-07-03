import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.files.Blob
import org.w3c.files.File
import org.w3c.files.FilePropertyBag
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json
import kotlin.js.Promise
import kotlin.js.json

// Բայց սոռտ է, մարդավարի ES5/ES6 ով կգրես, require('firebase/app') կամ import firebase from 'firebase'
// չի աշխատի, տողի վրա կգրես, կաշխատի 😂

var firebase: dynamic = js("firebase")
var dbRef = firebase.database().ref().child("projects")

fun main(args: Array<String>) {
    if (window.location.href.contains("index.html", false)) {

        window.onload = {
            getProjects {
                val divProjects = document.getElementById("row") as HTMLDivElement

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
                            window.location.href = "./project.html?alias=${projectAlias}"
                        }
                    })
                }
            }
        }
    } else if (window.location.href.contains("project.html")) {
        val targetProjectAlias = document.location?.href?.substringAfterLast("=", "=")

//        document.addEventListener("DOMContentLoaded", fun(event: Event) {
//            js("M.Modal.init(document.querySelectorAll(\".modal\"), {});")
//        })

        if (targetProjectAlias != null) {
            val collectionElement = document.getElementById("collection-header")

            getProject(targetProjectAlias) {
                val projectName = it["name"] as String
                val projectAlias = it["alias"] as String

                var languages = arrayListOf<String>()
                val languagesJson = it["languages"] as Json
                js("Object").values(languagesJson).forEach(fun (language: dynamic) {
                    languages.add(language["langName"] as String)
                })

                if (collectionElement != null) {

                    var innerHtml =
                            "<div class=\"header-container\">" +
                                    "<div class=\"header-container-base\">" +
                                    "<div>" +
                                    "<h5>${projectName}</h5>" +
                                    "<h6>${projectAlias}</h6>" +
                                    "</div>" +
                                    "<!-- Modal Trigger -->\n" +
                                    "<a class=\"btn-floating waves-effect waves-light btn modal-trigger\" href=\"#modal1\"><i class=\"material-icons\">add</i></a>\n" +
                                    "<!-- Modal Structure -->\n" +
                                    "<div id=\"modal1\" class=\"modal modal-fixed-footer\">\n" +
                                    "<div class=\"modal-content\">\n" +
                                    "<h4>Modal Header</h4>\n" +
                                    "<p>A bunch of text</p>\n" +
                                    "</div>\n" +
                                    "<div class=\"modal-footer\">\n" +
                                    "<a href=\"#!\" class=\"modal-close waves-effect waves-green btn-flat\">Agree</a>\n" +
                                    "</div>\n" +
                                    "</div>" +
                                    "<div>" +
                                    "</div>"

                    val tableData =
                            "<table class=\"highlight centered responsive-table\">" +
                                    "<thead>" +
                                    "<tr>" +
                                    getColumNames(languages) +
                                    "</tr>" +
                                    "</thead>" +
                                    "<tbody>" +
                                    getRows(it) +
                                    "</tbody>" +
                                    "</table>"

                    innerHtml += tableData
                    collectionElement.innerHTML = innerHtml
                }






//                val floatButtonElement = document.getElementById("float-button")
//                floatButtonElement?.addEventListener("click", fun(event: Event) {
//                    console.log("floatButtonElement")
//                })
            }
        }
    }
}

fun getColumNames(languages: ArrayList<String>): String {
    var str =  "<th>N</th>" + "<th>Screen</th>" +  "<th>Key</th>"
    for (language in languages) {
        str += "<th>${language}</th>"
    }
    return  str
}

fun getRows(json: Json): String {
    var str = ""
    var index = 0

    var screens = arrayListOf<String>()
    val screensJson = json["screens"] as Json
    js("Object").values(screensJson).forEach(fun(screen: String) {
        screens.add(screen)
    })

    val localization = json["localization"] as Json
    for (screen in screens) {

        val screenLocalization = localization[screen] as? Json
        if (screenLocalization != null) {


            js("Object").values(screenLocalization).forEach(fun(localization: dynamic) {
                index++
                val key = localization["key"] as String

                str += "<tr>" +
                        "<td>${index}</td>" +
                        "<td>${screen}</td>" +
                        "<td>${key}</td>"

                var values = arrayListOf<String>()
                val valuesJson = localization["values"] as Json
                js("Object").values(valuesJson).forEach(fun(value: dynamic) {
                    val languageKey = value["lang_key"] as String
                    val languageValue = value["lang_value"] as String
                    str += "<td>${languageValue}</td>"
                })

                str += "</tr>"
            })

        } else {
            continue
        }
    }

    return str
}

//M.Dropdown.init(elems, options);



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

external fun alert(message: Any?): Unit
external fun encodeURIComponent(uri: String): String
external fun encodeURI(uri: String): String
// function below is for save iOS format files zip
external fun saveiOS(project: Json): Unit


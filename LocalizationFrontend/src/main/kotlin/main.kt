import org.w3c.dom.*
import org.w3c.dom.events.Event
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Json



external fun require(module:String): dynamic

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
                            "<div class=\"collection-header\">" +
                            "<h5>${projectName}</h5>" +
                            "<h6>${projectAlias}</h6>" +
                                    "<dic>" +
                                    "  <!-- Dropdown Trigger -->\n" +
                                    "  <a class='dropdown-trigger btn' href='#' data-target='dropdown1'>Drop Me!</a>\n" +
                                    "\n" +
                                    "  <!-- Dropdown Structure -->\n" +
                                    "  <ul id='dropdown1' class='dropdown-content'>\n" +
                                    "    <li><a href=\"#!\">one</a></li>\n" +
                                    "    <li><a href=\"#!\">two</a></li>\n" +
                                    "    <li class=\"divider\" tabindex=\"-1\"></li>\n" +
                                    "    <li><a href=\"#!\">three</a></li>\n" +
                                    "    <li><a href=\"#!\"><i class=\"material-icons\">view_module</i>four</a></li>\n" +
                                    "    <li><a href=\"#!\"><i class=\"material-icons\">cloud</i>five</a></li>\n" +
                                    "  </ul>" +
                                    "</div>" +
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
    js("Object").values(screensJson).forEach(fun (screen: String) {
        screens.add(screen)
    })

    val localization = json["localization"] as Json
    for (screen in screens) {

        val screenLocalization = localization[screen] as? Json
        if (screenLocalization != null) {


            js("Object").values(screenLocalization).forEach(fun (localization: dynamic) {
                index++
                val key = localization["key"] as String

                str +=  "<tr>" +
                        "<td>${index}</td>" +
                        "<td>${screen}</td>" +
                        "<td>${key}</td>"

                var values = arrayListOf<String>()
                val valuesJson = localization["values"] as Json
                js("Object").values(valuesJson).forEach(fun (value: dynamic) {
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


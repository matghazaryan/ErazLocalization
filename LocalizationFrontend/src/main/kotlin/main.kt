import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.File
import org.w3c.files.FilePropertyBag
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.clear
import kotlin.dom.createElement
import kotlin.js.Json
import kotlin.js.Promise
import kotlin.js.json
import kotlin.math.cos

// Բայց սոռտ է, մարդավարի ES5/ES6 ով կգրես, require('firebase/app') կամ import firebase from 'firebase'
// չի աշխատի, տողի վրա կգրես, կաշխատի 😂

var firebase: dynamic = js("firebase")
var dbRef = firebase.database().ref().child("projects")
var projectName = String()

fun main(args: Array<String>) {
    if (window.location.href.contains("index.html", false)) {

        window.onload = {
            val elems = document.querySelectorAll(".modal")
            val params = json("onCloseEnd" to fun () {
                (document.getElementById("project_name") as HTMLInputElement).value = ""
                (document.getElementById("project_alias") as HTMLInputElement).value = ""
            })
            js("M").Modal.init(elems, params)

            YandexHelper.supportedLanguages().then {
                val select = document.createElement("select") as HTMLSelectElement
                select.multiple = true
                var index = it.keys.indexOf("en")
                it.forEach {
                    val option = document.createElement("option") as HTMLOptionElement
                    option.value = it.key
                    option.text = it.value
                    select.appendChild(option)
                }
                select.options.selectedIndex = index
                val div = document.getElementById("languages-combobox") as HTMLDivElement
                div.insertBefore(select, div.firstChild)
                val elems = document.querySelectorAll("select")
                val instance = js("M").FormSelect.init(elems, {})
                val addButton = document.getElementById("add_project")
                addButton?.addEventListener("click", {
                    val projectName = (document.getElementById("project_name") as HTMLInputElement).value
                    val projectAlias = (document.getElementById("project_alias") as HTMLInputElement).value
                    val languages = arrayListOf<Pair<String, String>>()
                    for (i in 0..(select.selectedOptions.length - 1)) {
                        val option = select.selectedOptions[i] as HTMLOptionElement
                        languages.add(option.value to option.text)
                    }
                    if (projectAlias.isNotEmpty() && projectName.isNotEmpty() && languages.isNotEmpty()) {
                        createProject(projectName, projectAlias, languages.toTypedArray())
                    }
                })
            }
            getProjects {
                val divProjects = document.getElementById("row") as HTMLDivElement
                while (divProjects.childElementCount > 1) {
                    val lastChild = divProjects.lastChild
                    if (lastChild != null) {
                        divProjects.removeChild(lastChild)
                    }
                }
                // Projects
                it.forEach {
                    var projectCard = document.getElementById(it["alias"].toString())
                    if (projectCard == null) {
                        projectCard = document.createElement("div") as HTMLDivElement
                        projectCard.className = "col s12 m3"
                        projectCard.id = it["alias"].toString()
                        val card = document.createElement("div") as HTMLDivElement
                        card.className = "card"
                        card.setAttribute("data-alias", it["alias"].toString())
                        val cardContext = document.createElement("div") as HTMLDivElement
                        cardContext.className = "card-content black-text"
                        val cardTitle = document.createElement("span") as HTMLSpanElement
                        cardTitle.className = "card-title"
                        cardTitle.innerText = it["name"].toString()
                        val alias = document.createElement("p") as HTMLParagraphElement
                        alias.innerText = it["alias"].toString()
                        val platformContainer = document.createElement("div") as HTMLDivElement
                        platformContainer.className = "platform-container"
                        val iosImage = document.createElement("img") as HTMLImageElement
                        iosImage.src = "images/icon-ios.png"
                        iosImage.alt = "iOS"
                        iosImage.width = 24
                        iosImage.height = 24
                        val androidImage = document.createElement("img") as HTMLImageElement
                        androidImage.src = "images/icon-android.png"
                        androidImage.alt = "Android"
                        androidImage.width = 24
                        androidImage.height = 24
                        val webImage = document.createElement("img") as HTMLImageElement
                        webImage.src = "images/icon-website.png"
                        webImage.alt = "Web"
                        webImage.width = 24
                        webImage.height = 24
                        platformContainer.append(iosImage, androidImage, webImage)
                        cardContext.append(cardTitle, alias, platformContainer)
                        card.appendChild(cardContext)
                        projectCard.appendChild(card)
                        divProjects.appendChild(projectCard)
                    }

                    // remove loading
                    val loading = document.getElementById("indicator")
                    if (loading != null) {
                        divProjects.removeChild(loading as HTMLElement)
                    }
                }


                // Adding click event listener
                for (node: Node in divProjects.childNodes.asList()) {
                    node.addEventListener("click", fun(event: Event) {
                        val element = node as HTMLDivElement
                        val cardElement = element.firstElementChild
                        if (cardElement != null) {
                            val projectAlias = cardElement.getAttribute("data-alias")
                            if (projectAlias != "new-project") {
                                window.location.href = "./project.html?alias=${projectAlias}"
                            }
                        }
                    })
                }
            }
        }
    } else if (window.location.href.contains("project.html")) {

        window.onload  = {
            setupModal()
        }

        val url = URL(document.location!!.href)
        val targetProjectAlias = url.searchParams.get("alias")


        if (targetProjectAlias != null) {

            val collectionElement = document.getElementById("collection-header")

            getProject(targetProjectAlias) {
                addLanguageInputsToPopup(it)

                projectName = it["name"] as String
                val projectAlias = it["alias"] as String

                var languages = arrayListOf<String>()
                val languagesJson = it["languages"] as Json
                js("Object").values(languagesJson).forEach(fun (language: dynamic) {
                    languages.add(language["langName"] as String)
                })


                var screens = arrayOf<String>()
                val screensJson = it["screens"] as Json
                js("Object").values(screensJson).forEach(fun(screen: String) {
                    screens[screens.count()] = screen
                })

                initScreenAutocompleteList(screens)

                var types = arrayOf<String>()
                val typesJson = it["types"] as Json
                js("Object").values(typesJson).forEach(fun(type: String) {
                    types[types.count()] = type
                })

                initTypeAutocompleteList(types)
                setupDropDown(it)

                if (collectionElement != null) {

                    // Header
                    val headerContainer = document.createElement("div") as HTMLDivElement
                    headerContainer.addClass("header-container")
                    val headerContainerBase = document.createElement("div") as HTMLDivElement
                    headerContainerBase.addClass("header-container-base")
                    val projectNameAndAlias = document.createElement("div") as HTMLDivElement
                    val HProjectName = document.createElement("h5") as HTMLHeadingElement
                    HProjectName.innerText = projectName
                    val HProjectAlias = document.createElement("h6") as HTMLHeadingElement
                    HProjectAlias.innerText = projectAlias
                    projectNameAndAlias.append(HProjectName, HProjectAlias)
                    val exportButton = document.createElement("div") as HTMLDivElement
                    exportButton.addClass("export_button")
                    exportButton.innerHTML = " <!-- Dropdown Trigger -->\n"
                    val dropdownTrigger = document.createElement("a")
                    dropdownTrigger.addClass("dropdown-trigger btn")
                    dropdownTrigger.setAttribute("href", "#")
                    dropdownTrigger.setAttribute("data-target", "dropdown1")
                    dropdownTrigger.innerHTML = "Export"
                    val dropdownContent = document.createElement("ul") as HTMLUListElement
                    dropdownContent.addClass("dropdown-content")
                    dropdownContent.id = "dropdown1"
                    val exportiOS = document.createElement("li") as HTMLLIElement
                    exportiOS.innerHTML = "<a href=\"#!\" id=\"export_ios\">iOS</a>"
                    val divider = document.createElement("li") as HTMLLIElement
                    divider.addClass("divider")
                    divider.tabIndex = -1
                    val exportAndroid = document.createElement("li") as HTMLLIElement
                    exportAndroid.innerHTML = "<a href=\"#!\" id=\"export_android\">Andriod</a>"
                    val exportWeb = document.createElement("li") as HTMLLIElement
                    exportWeb.innerHTML = "<a href=\"#!\" id=\"export_web\">Web</a>"
                    dropdownContent.append(exportiOS, divider, exportAndroid, divider.cloneNode(true), exportWeb)
                    exportButton.append(dropdownTrigger, dropdownContent)
                    headerContainerBase.append(projectNameAndAlias, exportButton)
                    headerContainer.appendChild(headerContainerBase)

                    //Table Data
                    val table = document.createElement("table") as HTMLTableElement
                    table.addClass("highlight centered responsive-table")
                    val tableHead = document.createElement("thead")
                    val row = document.createElement("tr") as HTMLTableRowElement
                    val tableIndex = document.createElement("th") as HTMLTableCellElement
                    tableIndex.addClass("table_index")
                    tableIndex.innerText = "N"
                    val tableScreen = document.createElement("th") as HTMLTableCellElement
                    tableScreen.addClass("table_screen")
                    tableScreen.innerText = "Screen"
                    val tableKey = document.createElement("th") as HTMLTableCellElement
                    tableKey.innerText = "Key"
                    row.append(tableIndex, tableScreen, tableKey)
                    for (language in languages) {
                        val th = document.createElement("th") as HTMLTableCellElement
                        th.innerText = language
                        row.appendChild(th)
                    }
                    tableHead.appendChild(row)
                    val tableBody = document.createElement("tbody")
                    var index = 0
                    val localization = it["localization"] as Json
                    Object().values(it["screens"]).forEach(fun(screen: String) {
                        val screenLocalization = localization[screen] as? Json
                        if (screenLocalization != null) {
                            Object().values(screenLocalization).forEach(fun(localization: dynamic) {
                                index++
                                val key = localization["key"] as String
                                val tr = document.createElement("tr") as HTMLTableRowElement
                                val tableIndex = document.createElement("td")
                                tableIndex.addClass("table_index")
                                tableIndex.innerHTML = "$index"
                                val tableScreen = document.createElement("td")
                                tableScreen.addClass("table_screen")
                                tableScreen.innerHTML = "$screen"
                                val tableKey = document.createElement("td")
                                tableKey.innerHTML = "$key"
                                tr.append(tableIndex, tableScreen, tableKey)
                                Object().values(localization["values"]).forEach(fun(value: dynamic) {
                                    val languageValue = value["lang_value"] as String
                                    val td = document.createElement("td")
                                    td.innerHTML = "$languageValue"
                                    tr.appendChild(td)
                                })
                                tableBody.appendChild(tr)
                            })
                        }
                    })

                    table.append(tableHead, tableBody)

                    val floatButton = document.createElement("div") as HTMLDivElement
                    floatButton.addClass("float_button")
                    floatButton.innerHTML = "<a class=\"btn-floating waves-effect waves-light btn modal-trigger\" href=\"#modal1\"><i class=\"material-icons\">add</i></a>\n"
                    collectionElement.innerHTML = ""
                    collectionElement.append(headerContainer, table, floatButton)

                    setupDropDown(it)

                }
            }
        }
    }
}


private fun setupModal(): Unit {

    val screenNameInput = document.getElementById("screen_autocomplete_input") as HTMLInputElement
    val typeInput = document.getElementById("type_autocomplete_input") as HTMLInputElement
    val keyInput = document.getElementById("localization_value") as HTMLInputElement
    val form = document.getElementById("localization_form") as HTMLFormElement

    val elems = document.querySelectorAll(".modal")

    val params = json("onCloseEnd" to fun () {
        screenNameInput.value = ""
        typeInput.value =  ""
        keyInput.value = ""

        form.reset()
    })

    js("M").Modal.init(elems, params)

    val addProjectElem = document.getElementById("add_project")
    addProjectElem?.addEventListener("click", fun(event:Event) {

        val screenName = screenNameInput.value
        val type = typeInput.value
        val key = keyInput.value

        form.reportValidity()
        val isValid = form.checkValidity()

        console.log(isValid)

        if (isValid == false) {
            return
        }

        val normalizedKey = screenName + "_" + type + "_" + key
        console.log(normalizedKey)

        var values = json()

        val elements = document.querySelectorAll("input.validate.language_input")
        for (element in elements.asList()) {
            val inputElem = element as HTMLInputElement
            val key = inputElem.getAttribute("data-key") as String
            val value = inputElem.value
            values.set(key, value)
        }

        console.log(values)
        addLocalization(projectName, screenName, type, normalizedKey, values)

        val elem = document.getElementById("modal1")
        val modal = js("M").Modal.getInstance(elem)
        modal.close()
    })
}



private fun setupDropDown(json: Json): Unit {

    js("var elems = document.querySelectorAll('.dropdown-trigger');" +
             "var instances = M.Dropdown.init(elems, {});"
    )

    val exportiOSElement = document.getElementById("export_ios")
    val exportAndroidElement = document.getElementById("export_android")
    val exportWebElement = document.getElementById("export_web")

    exportiOSElement?.addEventListener("click", fun(event: Event) {
        saveiOS(json)
    })

    exportAndroidElement?.addEventListener("click", fun(event: Event) {
        saveAndroid(json)
    })

    exportWebElement?.addEventListener("click", fun(event: Event) {
        saveWeb(json)
    })
}

fun addLanguageInputsToPopup(json: Json): Unit {
    val element = document.getElementById("localization_input")
    if (element != null) {
        var innerHtml = ""

        val languagesJson = json["languages"] as Json
        js("Object").values(languagesJson).forEach(fun (language: dynamic) {
            var languageName = language["langName"] as String
            var languageCode = language["langCode"] as String

            innerHtml += "" +
                    "<div class=\"row\">" +
                    "   <div class=\"input-field col s12\">\n" +
                    "       <i class=\"material-icons prefix\">g_translate</i>\n" +
                    "       <input id=\"language_input_${languageCode}\" data-key=\"${languageCode}\" type=\"text\" autocomplete=\"off\" class=\"validate language_input\" pattern=\".{1,}\" required title=\"\">" +
                    "       <label for=\"language_input_${languageCode}\">${languageName}</label>" +
                    "   </div>" +
                    "</div>"
        })

        element.innerHTML = innerHtml


        val elems = document.querySelectorAll("i.material-icons.prefix")
        console.log(elems)

        elems.asList().forEach {
            it.addEventListener("click", fun(event: Event) {
                val parentElement = it.parentElement as HTMLDivElement
                val inputElement = parentElement.children[1] as HTMLInputElement
                val from = inputElement.value

                if (from.isEmpty()) {
                    alert("Please type value")
                    return
                }

                val langKey = inputElement.getAttribute("data-key") as String
                val inputElems = document.querySelectorAll("input.validate.language_input")
                inputElems.asList().forEach {
                    val languageInputElement = it as HTMLInputElement
                    val _langKey = languageInputElement.getAttribute("data-key") as String

                    if (langKey != _langKey) {
                        console.log(langKey, "-", _langKey)
                        YandexHelper.translate(_langKey, from).then {
                            languageInputElement.focus()
                            languageInputElement.value = it
                        }
                    }
                }
            })
        }
    }
}



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
external fun initScreenAutocompleteList(screenNames: Array<String>): Unit
external fun initTypeAutocompleteList(types: Array<String>): Unit
external fun saveiOS(project: Json): Unit
external fun saveAndroid(project: Json): Unit
external fun saveWeb(project: Json): Unit
external fun addLocalization(projectName: String, screanName: String, type: String, newKey: String, valuesMap: Json): Unit
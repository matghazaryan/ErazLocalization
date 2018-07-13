import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.dom.url.URL
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.addClass
import kotlin.dom.removeClass
import kotlin.js.Json
import kotlin.js.json

// Բայց սոռտ է, մարդավարի ES5/ES6 ով կգրես, require('firebase/app') կամ import firebase from 'firebase'
// չի աշխատի, տողի վրա կգրես, կաշխատի 😂

var firebase: dynamic = js("firebase")
var dbRef = firebase.database().ref().child("projects")
var projectName = String()

fun main(args: Array<String>) {

    if (window.location.href.contains("index.html", false)) {

        window.onload = {

            var elems = document.querySelectorAll(".modal")
            val params = json("onCloseEnd" to fun() {
                val createProjectForm = document.getElementById("create_project_form") as HTMLFormElement
                createProjectForm.reset()
                val select = document.getElementById("language_select") as HTMLSelectElement

                val modalElem = document.getElementById("modal1")
                modalElem?.removeAttribute("mode")

                var index = 0
                for (i in 0 until select.options.asList().count()) {
                    val _optionElement = select.options[i] as HTMLOptionElement
                    if (_optionElement.value == "en") {
                        _optionElement.selected = true
                        index = i
                        break
                    }
                }
            })

            js("M").Modal.init(elems, params)

            setupLanguageOptions()

            getProjects {
                val divProjects = document.getElementById("row") as HTMLDivElement

                divProjects.innerHTML = "<div class=\"col s12 m3\">\n" +
                        "                    <div class=\"card\" data-alias=\"new-project\" data-target=\"modal1\" id=\"new-project\">\n" +
                        "                        <div class=\"card-container\">\n" +
                        "                            <img src=\"images/icon-plus.png\" alt=\"Add Project\" height=\"36\" width=\"36\">\n" +
                        "                            <p>Add Project</p>\n" +
                        "                        </div>\n" +
                        "                    </div>\n" +
                        "                </div>"

                // Projects
                it.forEach {
                    var projectCard = document.getElementById(it["alias"].toString())
                    if (projectCard == null) {

                        projectCard = document.createElement("div") as HTMLDivElement
                        projectCard.className = "col s12 m3"
                        projectCard.id = it["alias"].toString()

                        val card = document.createElement("div") as HTMLDivElement
                        card.className = "card"
                        card.setAttribute("data-alias", it["name"].toString())

                        val cardContext = document.createElement("div") as HTMLDivElement
                        cardContext.className = "card-content black-text"

                        val cardTitle = document.createElement("span") as HTMLSpanElement
                        cardTitle.className = "card-title"
                        cardTitle.innerText = it["name"].toString()

                        val alias = document.createElement("p") as HTMLParagraphElement
                        val projectAlias = it["alias"].toString()
                        alias.innerText = projectAlias

                        var actionsEditElem = document.createElement("div") as HTMLDivElement
                        actionsEditElem.className = "actions"

                        val deleteElem = document.createElement("a") as HTMLElement
                        deleteElem.className = "btn-floating btn-small waves-effect waves-light red"
                        val projectName = it["name"].toString()
                        deleteElem.setAttribute("data-project_name", projectName)
                        val deleteChildElem = document.createElement("i") as HTMLElement
                        deleteChildElem.className = "material-icons"
                        deleteChildElem.innerText = "clear"

                        deleteElem.appendChild(deleteChildElem)

                        val editElem = document.createElement("a") as HTMLElement
                        editElem.className = "btn-floating btn-small waves-effect waves-light light-blue edit_card"
                        editElem.setAttribute("data-project_name", projectName)
                        val editChildElem = document.createElement("i") as HTMLElement
                        editChildElem.className = "material-icons"
                        editChildElem.innerText = "edit"

                        editElem.addEventListener("click", fun(event: Event){
                            event.stopPropagation()

                            val headerElem = document.getElementById("add_project_header") as HTMLElement
                            headerElem.innerText = "Edit Project"

                            val projectNameInput = document.getElementById("project_name") as HTMLInputElement
                            projectNameInput.value = projectName
                            projectNameInput.className = ""

                            val projectAliasInput = document.getElementById("project_alias") as HTMLInputElement
                            projectAliasInput.value = projectAlias
                            projectAliasInput.className = ""

                            val select = document.getElementById("language_select") as HTMLSelectElement

                            val languages = it["languages"] as Array<String>
                            console.log(languages)
                            for (language in languages) {
                                for (option in select.options.asList()) {
                                    val optionElem = option as HTMLOptionElement

                                    // Prevent selected option
                                    if (optionElem.selected) {
                                        continue
                                    }

                                    optionElem.selected = (optionElem.value == language) || (optionElem.value == "en")
                                    console.log(option)
                                }
                            }

                            val elems = document.querySelectorAll("select")
                            js("M").FormSelect.init(elems, {})

                            val modalElem = document.getElementById("modal1")
                            modalElem?.setAttribute("mode", "editing")
                            val modal = js("M").Modal.getInstance(modalElem)
                            modal.open()

                            projectNameInput.select()
                            projectAliasInput.select()

                            projectNameInput.disabled = true
                            projectAliasInput.disabled = true

                        })

                        editElem.appendChild(editChildElem)

                        actionsEditElem.appendChild(editElem)
                        actionsEditElem.appendChild(deleteElem)

                        deleteElem.addEventListener("click", fun(event: Event) {
                            event.stopPropagation()
                            val projectName = deleteElem.getAttribute("data-project_name") as String

                            deleteProject(projectName) {
                                if (it != null) {
                                    alert("error")
                                } else {
                                    val elem = document.getElementById(projectCard.id) as HTMLDivElement
                                    val parent = elem.parentElement as HTMLDivElement
                                    val removedChild = parent.removeChild(elem)
                                }
                            }
                        })

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
                        cardContext.append(cardTitle, alias, actionsEditElem, platformContainer)
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
                            } else {
                                // show modal

                                val headerElem = document.getElementById("add_project_header") as HTMLElement
                                headerElem.innerText = "New Project"

                                val projectNameInput = document.getElementById("project_name") as HTMLInputElement
                                projectNameInput.className = "validate"
                                projectNameInput.disabled = false

                                val projectAliasInput = document.getElementById("project_alias") as HTMLInputElement
                                projectAliasInput.className = "validate"
                                projectAliasInput.disabled = false

                                val modalElem = document.getElementById("modal1")
                                val modal = js("M").Modal.getInstance(modalElem)
                                modal.open()
                            }
                        }
                    })
                }
            }
        }
    } else if (window.location.href.contains("project.html")) {

        window.onload = {
            setupModal()
            setupCopyElement()
        }

        val url = URL(document.location!!.href)
        val targetProjectAlias = url.searchParams.get("alias")

        if (targetProjectAlias != null) {

            val collectionElement = document.getElementById("collection-header")

            getProject(targetProjectAlias) {
                addLanguageInputsToPopup(it)

                projectName = it["name"] as String
                val projectAlias = it["alias"] as String

                val languages = arrayListOf<String>()
                val languagesJson = it["languages"] as Json
                js("Object").values(languagesJson).forEach(fun(language: dynamic) {
                    languages.add(language["langName"] as String)
                })

                val screens = arrayOf<String>()
                val screensJson = it["screens"] as? Json

                if (screensJson != null) {
                    js("Object").values(screensJson).forEach(fun(screen: String) {
                        screens[screens.count()] = screen
                    })
                    initScreenAutocompleteList(screens)
                }

                var types = arrayOf<String>()
                val typesJson = it["types"] as? Json
                if (typesJson != null) {
                    js("Object").values(typesJson).forEach(fun(type: String) {
                        types[types.count()] = type
                    })
                    initTypeAutocompleteList(types)
                }

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
                    // Export button
                    val exportButton = document.createElement("div") as HTMLDivElement
                    exportButton.addClass("export_button")
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
                    // end of export button
                    headerContainerBase.append(projectNameAndAlias)
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

                    val tableComment = document.createElement("th") as HTMLTableCellElement
                    tableComment.innerText = "Comment"
                    tableComment.hidden = true
                    row.append(tableIndex, tableScreen, tableKey)
                    row.append(tableIndex, tableScreen, tableKey, tableComment)

                    for (language in languages) {
                        val th = document.createElement("th") as HTMLTableCellElement
                        th.innerText = language
                        row.appendChild(th)
                    }

                    val options = document.createElement("th") as HTMLTableCellElement
                    options.addClass("head_options")
                    options.innerText = "   "
                    row.appendChild(options)


                    tableHead.appendChild(row)
                    val tableBody = document.createElement("tbody")
                    var index = 0
                    val localization = it["localization"] as? Json
                    if (localization != null) {
                        Object().values(it["screens"]).forEach(fun(screen: String) {
                            val screenLocalization = localization[screen] as? Json
                            tableRowDataFromScreen(screen, screenLocalization).forEach {
                                index++
                                val tr = tableRowElementFromTableRowData(it, index)
                                tableBody.appendChild(tr)
                            }
                        })
                    } else {
                        val emptyContentElem = document.createElement("p")
                        emptyContentElem.innerHTML = "Localization is empty"
                        emptyContentElem.className = "empty_data_content"
                        tableBody.appendChild(emptyContentElem)
                    }

                    table.append(tableHead, tableBody)
                    if (screens.count() > 0) {
                        val comboBox = document.createElement("div") as HTMLDivElement
                        comboBox.addClass("input-field")
                        comboBox.addClass("col")
                        comboBox.addClass("s12")
                        comboBox.id = "screens_combobox"
                        val select = document.createElement("select") as HTMLSelectElement
                        val allOption = document.createElement("option") as HTMLOptionElement
                        allOption.value = "all"
                        allOption.text = "All"
                        allOption.selected = true
                        select.appendChild(allOption)
                        screens.forEach {
                            val option = document.createElement("option") as HTMLOptionElement
                            option.value = it
                            option.text = it
                            select.appendChild(option)
                        }
                        val label = document.createElement("label") as HTMLLabelElement
                        label.innerText = "Filter by screen name"
                        comboBox.append(select, label)

                        val searchField = document.createElement("div")
                        searchField.addClass("input-field")
                        searchField.addClass("col")
                        searchField.addClass("s6")
                        val icon = document.createElement("i")
                        icon.addClass("material-icons")
                        icon.addClass("prefix")
                        icon.innerHTML = "search"
                        val input = document.createElement("input") as HTMLInputElement
                        input.addClass("validate")
                        input.id = "icon_search"
                        val placeholder = document.createElement("label") as HTMLLabelElement
                        placeholder.htmlFor = "icon_search"
                        placeholder.innerText = "Search"
                        searchField.append(icon, input, placeholder)

                        headerContainerBase.appendChild(searchField)
                        headerContainerBase.appendChild(comboBox)
                        headerContainerBase.appendChild(exportButton)

                        input.addEventListener("input", fun(e: Event) {
                            select.selectedIndex = 2
                            val searchText = input.value
                            tableBody.innerHTML = ""
                            index = 0
                            if (searchText.isEmpty()) {
                                if (localization != null) {
                                    Object().values(it["screens"]).forEach(fun(screen: String) {
                                        val screenLocalization = localization[screen] as? Json
                                        tableRowDataFromScreen(screen, screenLocalization).forEach {
                                            index++
                                            val tr = tableRowElementFromTableRowData(it, index)
                                            tableBody.appendChild(tr)
                                        }
                                    })
                                }
                            } else {
                                // first Object().values(localization) is an array of screens localizations
                                // val screen is the values of screens localizations
                                val localizationsOfscreens = arrayOf<Json>()
                                Object().keys(localization).forEach(fun(key: String) {
                                    val elem = localization?.get(key) as Json
                                    Object().values(elem).forEach(fun(el: Json) {
                                        localizationsOfscreens.set(localizationsOfscreens.count(), json(key to el))
                                    })
                                })
                                val predicate: ((Json)) -> Boolean = {
                                    var contains = false
                                    val values = Object().values(it)[0]["values"] as Json
                                    Object().values(values).forEach(fun(el: Json) {
                                        val x = el["lang_value"].toString().contains(searchText, true)
                                        if (x) {
                                            contains = x
                                        }
                                    })
                                    contains
                                }
                                val filteredScreens = localizationsOfscreens.filter(predicate)
                                filteredScreens.forEach {
                                    val screenName = Object().keys(it)[0].toString()
                                    tableRowDataFromScreen(screenName, it).forEach {
                                        index++
                                        val tr = tableRowElementFromTableRowData(it, index)
                                        tableBody.appendChild(tr)
                                    }
                                }
                            }
                        })
                        select.onchange = { _ ->
                            input.value = ""
                            tableBody.innerHTML = ""
                            index = 0
                            if ((select.selectedOptions[0] as HTMLOptionElement).value != "all") {
                                filterScreens(projectName, (select.selectedOptions[0] as HTMLOptionElement).value, fun(screen: Json) {
                                    tableRowDataFromScreen((select.selectedOptions[0] as HTMLOptionElement).value, screen).forEach {
                                        index++
                                        val tr = tableRowElementFromTableRowData(it, index)
                                        tableBody.appendChild(tr)
                                    }
                                })
                            } else {
                                if (localization != null) {
                                    Object().values(it["screens"]).forEach(fun(screen: String) {
                                        val screenLocalization = localization[screen] as? Json
                                        tableRowDataFromScreen(screen, screenLocalization).forEach {
                                            index++
                                            val tr = tableRowElementFromTableRowData(it, index)
                                            tableBody.appendChild(tr)
                                        }
                                    })
                                }
                            }
                        }
                    }

                    val floatButton = document.createElement("div") as HTMLDivElement
                    floatButton.addClass("float_button")
                    floatButton.innerHTML = "<a class=\"btn-floating waves-effect waves-light btn modal-trigger\" href=\"#modal1\"><i class=\"material-icons\">add</i></a>\n"
                    collectionElement.innerHTML = ""
                    collectionElement.append(headerContainer, table, floatButton)
                    var elems = document.querySelectorAll("select")
                    js("M").FormSelect.init(elems, {})
                    js("M").updateTextFields()
                    setupDropDown(it)

                }
            }
        }
    }
}


private fun setupModal() {

    val confirmationModal = document.getElementById("confirm_modal")
    val screenNameInput = document.getElementById("screen_autocomplete_input") as HTMLInputElement
    val typeInput = document.getElementById("type_autocomplete_input") as HTMLInputElement
    val keyInput = document.getElementById("localization_value") as HTMLInputElement
    val form = document.getElementById("localization_form") as HTMLFormElement
    val commentInput = document.getElementById("localization_comment") as HTMLInputElement
    val modal = document.getElementById("modal1")
    val mobileSwitchElem = document.getElementById("is_mobile") as HTMLInputElement
    val generateValuesElem = document.getElementById("generate_values") as HTMLElement

    val params = json("onCloseEnd" to fun() {

        if (screenNameInput.value.isNotEmpty()) {
            setEditing(false, projectName, screenNameInput.value, keyInput.value)
        }
        form.reset()
        screenNameInput.disabled = false
        typeInput.disabled = false
        keyInput.disabled = false
        commentInput.disabled = false
        mobileSwitchElem.disabled = false
        generateValuesElem.removeClass("disabled")

        val addLocalizationHeading = document.getElementById("add_localization_heading") as HTMLElement
        addLocalizationHeading.innerText = "Add Localization"

        modal?.removeAttribute("data-mode")
    })

    js("M").Modal.init(modal, params)
    js("M").Modal.init(confirmationModal, {})

    // Event Listeners

    screenNameInput.addEventListener("change", fun(event: Event) {
        generateKey()
    })

    typeInput.addEventListener("change", fun(event: Event) {
        generateKey()
    })

    keyInput.addEventListener("change", fun(event: Event) {
        generateKey()
    })

    mobileSwitchElem.addEventListener("change", fun(event: Event) {
        generateKey()
    })

    generateValuesElem.addEventListener("click", fun(event: Event) {
        generateKey()

        val generatedBaseValue = keyInput.value

        if (generatedBaseValue.isEmpty()) return

        val normalizedValue = generatedBaseValue.replace("_", " ").capitalize()
        val elements = document.querySelectorAll("input.validate.language_input")

        for (element in elements.asList()) {
            val inputElem = element as HTMLInputElement
            val langKey = inputElem.getAttribute("data-key") as String

            if (langKey == "en") {
                inputElem.focus()
                inputElem.value = normalizedValue
            } else {
                YandexHelper.translate(langKey, normalizedValue).then {
                    inputElem.focus()
                    inputElem.value = it
                }
            }
        }
    })


    val addProjectElem = document.getElementById("add_project")
    addProjectElem?.addEventListener("click", fun(event: Event) {

        val screenName = screenNameInput.value.trim('_')
        val type = typeInput.value.trim('_')
        val key = keyInput.value.trim('_')
        val comment = commentInput.value

        if (!form.checkValidity()) {
            form.reportValidity()
            return
        }

        val normalizedKey = screenName + "_" + type + "_" + key
        console.log(normalizedKey)

        val mode = modal?.getAttribute("data-mode")
        if (mode == "editing") {

            val elements = document.querySelectorAll("input.validate.language_input")
            for (element in elements.asList()) {
                val inputElem = element as HTMLInputElement
                val langKey = inputElem.getAttribute("data-key") as String
                val langValue = inputElem.value

                editLocalization(projectName, screenName, normalizedKey, langKey, langValue)
            }

            val elem = document.getElementById("modal1")
            val modal = js("M").Modal.getInstance(elem)
            modal.close()

            return
        }

        var values = json()

        val elements = document.querySelectorAll("input.validate.language_input")
        for (element in elements.asList()) {
            val inputElem = element as HTMLInputElement
            val key = inputElem.getAttribute("data-key") as String
            val value = inputElem.value
            values.set(key, value)
        }

        val isMobile = (document.getElementById("is_mobile") as HTMLInputElement).checked
        if (addLocalization(projectName, screenName, type, normalizedKey, values, isMobile, comment)) {

            val elem = document.getElementById("modal1")
            val modal = js("M").Modal.getInstance(elem)
            modal.close()
        }
    })
}

private fun generateKey(): Unit {

    val screenNameInput = document.getElementById("screen_autocomplete_input") as HTMLInputElement
    val typeInput = document.getElementById("type_autocomplete_input") as HTMLInputElement
    val keyInput = document.getElementById("localization_value") as HTMLInputElement
    val generatedKeyInput = document.getElementById("disabled") as HTMLInputElement
    val mobileSwitch = document.getElementById("is_mobile") as HTMLInputElement

    val screenName = screenNameInput.value.trim('_')
    val type = typeInput.value.trim('_')
    val key = keyInput.value.trim('_')


    var generatedKey = String()

    if (mobileSwitch.checked) {
        generatedKey += "m_"
    }

    generatedKey += screenName

    if (!type.isEmpty()) {
        generatedKey += "_" + type
    }

    if (!key.isEmpty()) {
        generatedKey += "_" + key
    }

    generatedKeyInput.value = generatedKey
}

private fun setupCopyElement(): Unit {
    val copyElem = document.getElementById("content_copy")
    copyElem?.addEventListener("click", fun(event: Event) {
        val generatedKeyInput = document.getElementById("disabled") as HTMLInputElement
        val clipboardInput = document.getElementById("clipboard_input") as HTMLInputElement
        clipboardInput.value = generatedKeyInput.value
        clipboardInput.select()
        document.execCommand("copy")
        js("M.toast({html: 'Copied', classes: 'rounded'});")
    })
}

private fun setupDropDown(json: Json) {

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

fun tableRowDataFromScreen(name: String, screen: Json?): Array<TableRowData> {
    val array = arrayListOf<TableRowData>()
    if (screen != null) {
        Object().values(screen).forEach(fun(localization: dynamic) {
            val key = localization["key"] as String
            val comment = localization["comment"] as? String
            val isMobile = localization["isMobile"] as Boolean
            val values = mutableMapOf<String, String>()
            Object().values(localization["values"]).forEach(fun(value: dynamic) {
                values.put(value["lang_key"].toString(), value["lang_value"].toString())
            })
            array.add(TableRowData(name, key, comment, isMobile, values, localization.isEditing as Boolean))
        })
    }
    return array.toTypedArray()
}

fun tableRowElementFromTableRowData(tableRowData: TableRowData, index: Int): HTMLTableRowElement {
    val tr = document.createElement("tr") as HTMLTableRowElement
    val tableIndex = document.createElement("td")
    tableIndex.addClass("table_index")
    tableIndex.innerHTML = "$index"
    tableIndex.setAttribute("mobile", if (tableRowData.isMobile) "true" else "false")
    val tableScreen = document.createElement("td")
    tableScreen.addClass("table_screen")
    tableScreen.innerHTML = tableRowData.screen
    val tableKey = document.createElement("td")
    tableKey.innerHTML = tableRowData.key

    val tableComment = document.createElement("td") as HTMLElement
    tableComment.innerHTML = tableRowData.comment.orEmpty()
    tableComment.hidden = true

    tr.append(tableIndex, tableScreen, tableKey, tableComment)

    // Add values

    tableRowData.values.forEach {
        val languageValue = it.value
        val td = document.createElement("td")
        td.innerHTML = languageValue
        tr.appendChild(td)
    }

    val tdOptions = document.createElement("td")
    tdOptions.addClass("head_options")

    val deleteElem = document.createElement("i") as HTMLElement
    deleteElem.addClass("small")
    deleteElem.addClass("material-icons")
    deleteElem.addClass("action")
    deleteElem.innerText = "delete"
    deleteElem.hidden = tableRowData.isEditing

    deleteElem.addEventListener("click", fun(event: Event) {
        val modal = document.getElementById("confirm_modal")
        var instance = js("M").Modal.getInstance(modal)
        instance.open()
        val delete = document.getElementById("confirm_modal_delete")
        delete?.addEventListener("click", fun(e: Event) {
            console.log(projectName, tableRowData.screen, tableRowData.key)
            removeRow(projectName, tableRowData.screen, tableRowData.key)

        })
        console.log("delete")
    })


    val editElem = document.createElement("i") as HTMLElement
    editElem.addClass("small")
    editElem.addClass("material-icons")
    editElem.addClass("action")
    editElem.innerText = "edit"
    editElem.hidden = tableRowData.isEditing

    editElem.addEventListener("click", fun(event: Event) {
        console.log("edit")

        val screenNameInput = document.getElementById("screen_autocomplete_input") as HTMLInputElement
        val typeInput = document.getElementById("type_autocomplete_input") as HTMLInputElement
        val keyInput = document.getElementById("localization_value") as HTMLInputElement
        val generatedKeyInput = document.getElementById("disabled") as HTMLInputElement
        val commentInput = document.getElementById("localization_comment") as HTMLInputElement
        val languageElements = document.querySelectorAll("input.validate.language_input")
        val mobileSwitchElem = document.getElementById("is_mobile") as HTMLInputElement
        val generateValuesElem = document.getElementById("generate_values") as HTMLElement
        val addLocalizationHeading = document.getElementById("add_localization_heading") as HTMLElement

        addLocalizationHeading.innerText = "Edit Localization"

        val trElement = editElem.parentElement?.parentElement as HTMLTableRowElement

        val screenName = trElement.children[1]?.innerHTML as String
        val key = trElement.children[2]?.innerHTML as String
        val comment = trElement.children[3]?.innerHTML as? String

        val indexElem = trElement.children[0]
        console.log(indexElem)
        val mobileAttribute = indexElem?.getAttribute("mobile") as String

        val isMobile = mobileAttribute == "true"

        screenNameInput.value = screenName

        val normalizedKey = key.replaceFirst("_m", "")

        keyInput.value = normalizedKey.substringAfterLast("_")
        typeInput.value = normalizedKey.substringAfter("_").substringBeforeLast("_")
        generatedKeyInput.value = key
        commentInput.value = comment.orEmpty()
        mobileSwitchElem.checked = isMobile


        var element: HTMLInputElement
        var value: String

        for (i in 4..trElement.childElementCount - 2) {
            val element = languageElements[i - 4] as HTMLInputElement
            val value = trElement.children[i]?.innerHTML as String
            element.value = value
        }

        val modal = document.getElementById("modal1")
        modal?.setAttribute("data-mode", "editing")
        var instance = js("M").Modal.getInstance(modal)
        instance.open()


        screenNameInput.select()
        typeInput.select()
        keyInput.select()
        commentInput.select()

        screenNameInput.className = ""
        typeInput.className = ""
        keyInput.className = ""

        screenNameInput.disabled = true
        typeInput.disabled = true
        keyInput.disabled = true
        commentInput.disabled = true
        mobileSwitchElem.disabled = true

        generateValuesElem.addClass("disabled")

        for (elem in languageElements.asList()) {
            (elem as HTMLInputElement).focus()
        }

        setEditing(true, projectName, screenName, key)

    })

    tdOptions.append(deleteElem, editElem)

    tr.appendChild(tdOptions)

    tr.className = if (tableRowData.isEditing) "background_yellow"  else ""


    return tr
}

fun addLanguageInputsToPopup(json: Json) {


    val elems = document.querySelectorAll("i.material-icons.prefix.value")

    if (elems.length > 0) {
        return
    }

    val element = document.getElementById("localization_input")
    if (element != null) {
        var innerHtml = ""

        val languagesJson = json["languages"] as Json
        js("Object").values(languagesJson).forEach(fun(language: dynamic) {
            val languageName = language["langName"] as String
            val languageCode = language["langCode"] as String

            innerHtml += "" +
                    "<div class=\"row\">" +
                    "   <div class=\"input-field col s12\">\n" +
                    "       <i class=\"material-icons prefix value\">g_translate</i>\n" +
                    "       <input id=\"language_input_${languageCode}\" data-key=\"${languageCode}\" type=\"text\" autocomplete=\"off\" class=\"validate language_input\" pattern=\".{1,}\" required title=\"\">" +
                    "       <label for=\"language_input_${languageCode}\">${languageName}</label>" +
                    "   </div>" +
                    "</div>"
        })

        element.innerHTML = innerHtml

        val elems = document.querySelectorAll("i.material-icons.prefix.value")

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

fun setupLanguageOptions() {

    YandexHelper.supportedLanguages().then {
        val select = document.createElement("select") as HTMLSelectElement
        select.id = "language_select"
        select.multiple = true
        val index = it.keys.indexOf("en")

        it.forEach {
            val option = document.createElement("option") as HTMLOptionElement
            option.value = it.key
            option.text = it.value
            select.appendChild(option)
        }

        val enOption = select.options[index] as HTMLOptionElement
        enOption.selected = true
        enOption.disabled = true

        val div = document.getElementById("languages-combobox") as HTMLDivElement
        div.insertBefore(select, div.firstChild)
        val elems = document.querySelectorAll("select")
        js("M").FormSelect.init(elems, {})

        setupAddProjectButton(select)
    }
}


fun setupAddProjectButton(select: HTMLSelectElement) {

    val addButton = document.getElementById("add_project")
    addButton?.addEventListener("click", {

        val modalElem = document.getElementById("modal1")
        val mode = modalElem?.getAttribute("mode")

        val createProjectForm = document.getElementById("create_project_form") as HTMLFormElement

        if (!createProjectForm.checkValidity()) {
            createProjectForm.reportValidity()
            return@addEventListener
        }

        val projectName = (document.getElementById("project_name") as HTMLInputElement).value
        val projectAlias = (document.getElementById("project_alias") as HTMLInputElement).value
        val languages = arrayListOf<Pair<String, String>>()

        for (i in 0..(select.selectedOptions.length - 1)) {
            val option = select.selectedOptions[i] as HTMLOptionElement
            languages.add(option.value to option.text)
        }

        if (mode == "editing") {
            addLanguages(projectName, languages.toTypedArray())
        } else {
            createProject(projectName, projectAlias, languages.toTypedArray())
        }

        val modal = js("M").Modal.getInstance(modalElem)
        modal.close()
    })
}


class TableRowData {
    var screen: String = ""
    var key: String = ""
    var values = mutableMapOf<String, String>()
    var comment: String?
    var isMobile: Boolean = false
    var isEditing = false

    constructor(screen: String, key: String, comment: String?, isMobile: Boolean, values: MutableMap<String, String>, isEditing: Boolean) {
        this.screen = screen
        this.key = key
        this.comment = comment
        this.isMobile = isMobile
        this.values = values
        this.isEditing = isEditing
    }

    override fun toString(): String {
        return "screen = $screen, key = $key, comment = $comment, values = ${values.toString()}"
    }
}

/// Helpers

@Deprecated("As we use Yandex translate, so we need to support same languages like Yandex, use YandexHelper.supportedLanguages() instead", ReplaceWith("YandexHelper.supportedLanguages()"), DeprecationLevel.WARNING)
fun loadJSON(callBack: (HashMap<String, String>) -> Unit) {
    val json = js("langs")
    val map = hashMapOf<String, String>()
    js("Object").keys(json).forEach(fun(key: String) {
        map[key] = json[key] as String
    })
    callBack(map)
}


external fun alert(message: Any?)
external fun encodeURIComponent(uri: String): String
external fun initScreenAutocompleteList(screenNames: Array<String>)
external fun initTypeAutocompleteList(types: Array<String>)
external fun saveiOS(project: Json)
external fun saveAndroid(project: Json)
external fun saveWeb(project: Json)
external fun addLocalization(projectName: String, screanName: String, type: String, newKey: String, valuesMap: Json, isMobile: Boolean, comment: String?): Boolean
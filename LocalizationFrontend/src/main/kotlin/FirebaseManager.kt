import kotlin.js.Json

fun createProject(name: String, alias: String): String {
    val code = hashMapOf<String, HashMap<String, String>>(name to hashMapOf(
            "name" to name,
            "alias" to alias
    ))
    val json = createJson()
    json[name] = JSON.parse<Json>("{ \"name\" : \"$name\"," +
                                        "\"alias\" : \"$alias\" }")
    dbRef.update(json, fun(error: Any) {
        if (error == null) {
            addTypes(name, "button", "label")
            addScreens(name, "login", "register")
        } else {
            console.log(error)
        }
    })
    return "success"
}

fun addScreens(name: String, vararg names: String) {
    addArray("screens", name, *names)
}

fun addTypes(name: String, vararg  names: String) {
    addArray("types", name, *names)
}

fun addArray(child: String, name: String, vararg names: String) {
    var arrayString = "["
    for (name in names) {
        arrayString += "\"$name\"" + ','
    }
    arrayString = arrayString.removeSuffix(",") + "]"
    val json = createJson()
    json[child] = JSON.parse<Json>(arrayString)
    val childRef = dbRef.child(name)
    childRef.update(json, fun(error: Any) {
        if (error == null) {
            console.log("success")
        } else {
            console.log(error)
        }
    })
}

fun createJson(): dynamic {
    return js("{}")
}
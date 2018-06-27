import kotlin.js.Json

fun createProject(name: String, alias: String): String {
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

fun getProjects(completion: (Array<HashMap<String, String>>) -> Unit) {
    var projects = ArrayList<HashMap<String, String>>()
    dbRef.on(Constants.FIREBASE.contentType.VALUE, fun (snapshot: dynamic) {
                snapshot.forEach(fun (child: dynamic) {
                    val element = hashMapOf<String, String>("name" to child.toJSON().name,
                            "alias" to child.toJSON().alias)
                    projects.add(element)
                })
                completion(projects.toTypedArray())
            })
}


/// Helpers

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
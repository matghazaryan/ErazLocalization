import kotlin.js.Json
import kotlin.js.json

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
    addStrings("screens", name, *names)
}

fun addTypes(name: String, vararg  names: String) {
    addStrings("types", name, *names)
}

fun getProjects(completion: (Array<HashMap<String, String>>) -> Unit) {
    var projects = ArrayList<HashMap<String, String>>()
    dbRef.on(Constants.FIREBASE.contentType.VALUE, fun (snapshot: dynamic) {
        snapshot.forEach(fun (child: dynamic) {
            val element = hashMapOf<String, String>(
                    "name" to child.toJSON().name,
                    "alias" to child.toJSON().alias
            )
            projects.add(element)
        })
        completion(projects.toTypedArray())
    })
}

fun getProject(name: String, listener: (Json) -> Unit) {
    val childRef = dbRef.child(name)
    childRef.on(Constants.FIREBASE.contentType.VALUE, fun (snapshot: dynamic) {
        listener(snapshot.toJSON() as Json)
    })
}

fun addLanguages(name: String, languages: Array<Pair<String, String>>) {
    val childRef = dbRef.child("$name/languages")
    childRef.once(Constants.FIREBASE.contentType.VALUE)
            .then(fun(snapshot: dynamic) {
                val snapshotArray = js("Object").values(snapshot.toJSON())
                var needsToUpdate = false
                for (language in languages) {
                    val element = json("langCode" to language.first,
                            "langName" to language.second)
                    var contains = false
                    snapshotArray.forEach(fun (elem: dynamic) {
                        if (elem["langCode"] == element["langCode"]) {
                            contains = true
                        }
                    })
                    if (!contains) {
                        snapshotArray.push(element)
                        needsToUpdate = true
                    }
                }
                if (needsToUpdate) {
                    childRef.set(snapshotArray, fun(error: Any?) {
                        if (error == null) {
                            console.log("success")
                        } else {
                            console.log(error)
                        }
                    })
                }
            })
            .catch(fun (error: dynamic) {
                var snapshotArray = json()
                for (language in languages) {
                    snapshotArray[languages.indexOf(language).toString()] = json("langCode" to language.first,
                            "langName" to language.second)
                }
                childRef.set(snapshotArray, fun(error: Any?) {
                    if (error == null) {
                        console.log("success")
                    } else {
                        console.log(error)
                    }
                })
            })

}


/// Helpers

fun addStrings(child: String, name: String, vararg names: String) {
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
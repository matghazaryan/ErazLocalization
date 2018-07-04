import kotlin.js.Json
import kotlin.js.Promise
import kotlin.js.json
import kotlin.math.cos

fun createProject(name: String, alias: String, languages: Array<Pair<String, String>>): String {
    val json = createJson()
    json[name] = JSON.parse<Json>("{ \"name\" : \"$name\"," +
            "\"alias\" : \"$alias\" }")
    dbRef.update(json, fun(error: Any) {
        if (error == null) {
            addLanguages(name, languages).then {
                alert(it)
            }.catch {
                alert(it.message)
            }
        } else {
            alert("error")
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
        console.log(projects.toTypedArray())
        completion(projects.toTypedArray())
    })
}

fun getProject(name: String, listener: (Json) -> Unit) {
    val childRef = dbRef.child(name)
    childRef.on(Constants.FIREBASE.contentType.VALUE, fun (snapshot: dynamic) {
        listener(snapshot.toJSON() as Json)
    })
}

fun addLanguages(name: String, languages: Array<Pair<String, String>>): Promise<String> {
    val childRef = dbRef.child("$name/languages")
    return Promise { success, failure ->
        childRef.once(Constants.FIREBASE.contentType.VALUE)
                .then(fun(snapshot: dynamic) {
                    val snapshotArray = js("Object").values(snapshot.toJSON())
                    var needsToUpdate = false
                    for (language in languages) {
                        val element = json("langCode" to language.first,
                                "langName" to language.second)
                        var contains = false
                        snapshotArray.forEach(fun(elem: dynamic) {
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
                                success("success")
                            } else {
                                failure(Throwable("error"))
                            }
                        })
                    }
                })
                .catch(fun(_: dynamic) {
                    var snapshotArray = json()
                    for (language in languages) {
                        snapshotArray[languages.indexOf(language).toString()] = json("langCode" to language.first,
                                "langName" to language.second)
                    }
                    childRef.set(snapshotArray, fun(error: Any?) {
                        if (error == null) {
                            success("success")
                        } else {
                            failure(Throwable("error"))
                        }
                    })
                }) as Unit
    }

}

fun filterScreens(projectName: String, name: String, callBack: (Json) -> Unit) {
    val project = dbRef.child("$projectName/localization/$name")
    project.once(Constants.FIREBASE.contentType.VALUE)
            .then(fun (snapshot: dynamic) {
                callBack(snapshot.toJSON() as Json)
            })
            .catch(fun (error: Throwable) {
                alert(error.message)
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
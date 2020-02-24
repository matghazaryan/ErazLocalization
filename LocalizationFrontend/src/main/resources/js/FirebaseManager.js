function createProject(name, alias, languages) {
    let json = new Object();
    let types = {"types": {
            0: "label",
            1: "button",
            2: "alert",
            3: "error",
            4: "placeholder",
            5: "warning"
            }};
    json[name] = { "name" : name,
    "alias" : alias,
             types };
    dbRef.update(json, function (error) {
        if (error == null) {
            addLanguages(name, languages).then( () => {
                window.location.href = "project.html?alias=$name"
//                alert(it)
            })
        } else {
//            alert("error")
        }
    });
    return "success"
}

function addScreens(name, ...names) {
    addStrings("screens", name, names)
}

function addTypes(name, ...names) {
    addStrings("types", name, names)
}

function getProjects() {
    return new Promise((resolve, reject) => {
        let projects = [Object];
        dbRef.on(Constants.FIREBASE.contentType.VALUE).then( (snapshot) => {
            snapshot.forEach(function (child) {
                let project = child.toJSON();
                let languages = [];
                Object().values(project["languages"]).forEach(  (lang, idx) => {
                    languages.set(languages.count(), lang["langCode"].toString())
                });
                let element = {
                    "name" : project["name"].toString(),
                    "alias" : project["alias"].toString(),
                    "languages" : languages
                };
                projects.add(element)
            });
            resolve(projects.toTypedArray())
        })
    })
}

function getProject(name) {
    return new Promise((resolve, reject) => {
        let childRef = dbRef.child(name);
        childRef.on(Constants.FIREBASE.contentType.VALUE).then( (snapshot) => {
            let projectJson = snapshot.toJSON();
            console.log(projectJson);
            let localizations = snapshot.toJSON()["localization"];
            if (localizations != null) {
                Object.values(localizations).forEach( (value) => {
                    Object.values(value).forEach( (value) => {
                        localizationKeys.add(value["key"].toString())
                    })
                })
            }
            resolve(snapshot.toJSON())
        })
    });
}

function addLanguages(name, languages) {
    let childRef = dbRef.child(name + "/languages");
    return new Promise( (success, failure) =>
        childRef.once(Constants.FIREBASE.contentType.VALUE)
            .then( (snapshot) => {
                let snapshotArray = Object.values(snapshot.toJSON());
                let addedLanguages = [];
                let needsToUpdate = false;
                for (language in languages) {
                    let element = { "langCode" : language.first,
                        "langName" : language.second};
                    let contains = false;
                    snapshotArray.forEach( (elem) => {
                        if (elem["langCode"] === element["langCode"]) {
                            contains = true
                        }
                    });
                    if (!contains) {
                        snapshotArray.push(element);
                        addedLanguages.add(language);
                        needsToUpdate = true;
                    }
                }
                if (needsToUpdate) {
                    let localizationRef = dbRef.child("$name/localization");
                    localizationRef.once(Constants.FIREBASE.contentType.VALUE)
                        .then( (snapshot) => {
                            let json = snapshot.toJSON();
                            if (json != null) {
                                Object.keys(json).forEach( (key) => {
                                    let objects = json[key];
                                    console.log("objects" , objects);
                                    Object.values(objects).forEach( (singleObject, idx) => {
                                        let values = singleObject["values"];
                                        console.log("singleObject", singleObject);
                                        let nextIndex = Object().keys(values).length;
                                        let firstValue = values["0"];
                                        let langKey = firstValue["lang_key"];
                                        let langValue = firstValue["lang_value"];
                                        let promises = [];
                                        addedLanguages.forEach( (it) => {
                                            let promise = YandexHelper.translate(it.first, langValue, false, langKey);
                                            promises.add(promise)
                                        });
                                        Promise.all(promises).then( it => {
                                            it.forEach( translations => {
                                                values[nextIndex.toString()] = {
                                                    "lang_key": addedLanguages[it.indexOf(translations)][0],
                                                    "lang_value": translations
                                                }
                                            });
                                            nextIndex += 1;
                                        });
                                        singleObject["values"] = values;
                                        console.log("singleObject", singleObject);
                                        localizationRef.child(key + idx.toString).set(singleObject)
                                    })
                                })
                            })
                        }
                });
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
        val snapshotArray = json()
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
    }) as? Unit
}

}
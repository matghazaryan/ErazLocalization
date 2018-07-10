var dbRef = firebase.database().ref("projects")


function addLocalization(projectName, screenName, type, newKey, valuesMap) {
    let includeScrean = false;
    let includeType = false;
    let screensRef = dbRef.child(projectName.toLowerCase() + "/screens");
    let typesRef = dbRef.child(projectName.toLowerCase() + "/types");

    console.log(screensRef);
    console.log(typesRef);

    let promise1 = screensRef.once("value")
        .then(function (snapshot) {
            let values = Object.values(snapshot.toJSON());
            includeScrean = values.includes(screenName);
            if (!includeScrean) {
                // Add screen to screens array
                let newValue = snapshot.toJSON();
                newValue[values.length] = screenName;
                screensRef.update(newValue);
                console.log("screen name added - ", screenName);
            }
        });

    let promise2 = typesRef.once("value")
        .then(function (snapshot) {
            let values = Object.values(snapshot.toJSON());
            includeType = values.includes(type);
            if (!includeType) {
                // Add type to types array
                let newValue = snapshot.toJSON();
                newValue[values.length] = type;
                typesRef.update(newValue);
            }
        });

    Promise.all([promise1, promise2])
        .then(function (snapshots) {
            // when know includes
            // get localizations
            let localization = dbRef.child(projectName.toLowerCase() + '/localization');
            if (includeScrean) {
                localization = dbRef.child(projectName.toLowerCase() + '/localization/' + screenName)
            }

            console.log("Localization - ", localization);

            localization.once('value')
                .then(function (snapshot) {
                    console.log("Snapshot - ", snapshot);
                    console.log(snapshot.toJSON());
                    let values = Object.values(snapshot.toJSON());
                    if (includeScrean) {
                        let newValue = snapshot.toJSON();
                        let localizatonValues = [];
                        Object.keys(valuesMap).forEach(function (key) {
                            localizatonValues.push({
                                'lang_key': key,
                                'lang_value': valuesMap[key]
                            });
                        })
                        newValue[values.length] = {
                            'key': newKey,
                            'values': localizatonValues
                        }
                        localization.update(newValue);
                    } else {
                        let newValue = snapshot.toJSON();
                        let localizatonValues = [];
                        Object.keys(valuesMap).forEach(function (key) {
                            localizatonValues.push({
                                'lang_key': key,
                                'lang_value': valuesMap[key]
                            });
                        })
                        newValue[screenName] = [{
                            'key': newKey,
                            'values': localizatonValues
                        }];
                        localization.update(newValue);
                    }
                })
        });
}


function initScreenAutocompleteList(screenNames) {
    var element = document.getElementById("screen_autocomplete_input");
    var map = new Object();
    screenNames.forEach(function (screenName) {
        map[screenName] = null;
    });
    M.Autocomplete.init(element, {data: map});
}

function initTypeAutocompleteList(types) {
    var element = document.getElementById("type_autocomplete_input");
    var map = new Object();
    types.forEach(function (type) {
        map[type] = null;
    });
    M.Autocomplete.init(element, {data: map});
}
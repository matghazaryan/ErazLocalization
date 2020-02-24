const dbRef = firebase.database().ref("projects");
let localizationKeys = new Set();

function addLocalization(projectName, screenName, type, newKey, valuesMap, isMobile, comment) {
    if (localizationKeys.has(newKey)) {
        alert("Key is already exist");
        return false;
    }
    let includeScrean = false;
    let includeType = false;
    let screensRef = dbRef.child(projectName + "/screens");
    let typesRef = dbRef.child(projectName + "/types");

    console.log(screensRef);
    console.log(typesRef);

    let promise1 = screensRef.once("value")
        .then(function (snapshot) {
            let values = [];
            let newValue = {};
            if (typeof snapshot !== 'undefined' && snapshot.toJSON() !== null) {
                // there is not screen
                newValue = snapshot.toJSON();
                values = Object.values(newValue);
            }
            includeScrean = values.includes(screenName);
            if (!includeScrean) {
                // Add screen to screens array
                newValue[values.length] = screenName;
                screensRef.update(newValue);
                console.log("screen name added - ", screenName);
            }
        });

    let promise2 = typesRef.once("value")
        .then(function (snapshot) {
            let values = [];
            let newValue = {};
            if (typeof snapshot !== 'undefined' && snapshot.toJSON() !== null) {
                let newValue = snapshot.toJSON();
                Object.values(newValue);
            }
            includeType = values.includes(type);
            if (!includeType) {
                // Add type to types array
                newValue[values.length] = type;
                typesRef.update(newValue);
            }
        });

    Promise.all([promise1, promise2])
        .then(function (snapshots) {
            // when know includes
            // get localizations
            let localization = dbRef.child(projectName + '/localization');
            if (includeScreen) {
                localization = dbRef.child(projectName + '/localization/' + screenName)
            }

            localization.once('value')
                .then(function (snapshot) {
                    let values = [];
                    let newValue = {};
                    let localisationValues = [];
                    if (typeof snapshot !== 'undefined' && snapshot.toJSON() !== null) {
                        newValue = snapshot.toJSON();
                        values = Object.values(newValue)
                    }
                    Object.keys(valuesMap).forEach(function (key) {
                        localisationValues.push({
                            'lang_key': key,
                            'lang_value': valuesMap[key]
                        });
                    });
                    if (typeof comment === "undefined") {
                        comment = ""
                    }
                    if (includeScreen) {
                        newValue[values.length] = {
                            'key': newKey,
                            'comment': comment,
                            'values': localisationValues,
                            'isMobile': isMobile,
                            'isEditing': false
                        };
                    } else {
                        newValue[screenName] = [{
                            'key': newKey,
                            'comment': comment,
                            'values': localisationValues,
                            'isMobile': isMobile,
                            'isEditing': false
                        }];
                    }
                    localization.update(newValue);
                })
        });
    return true;
}


function initScreenAutocompleteList(screenNames) {
    const element = document.getElementById("screen_autocomplete_input");
    let map = {};
    screenNames.forEach(function (screenName) {
        map[screenName] = null;
    });
    M.Autocomplete.init(element, {data: map});
}

function initTypeAutocompleteList(types) {
    const element = document.getElementById("type_autocomplete_input");
    let map = {};
    types.forEach(function (type) {
        map[type] = null;
    });
    M.Autocomplete.init(element, {data: map});
}
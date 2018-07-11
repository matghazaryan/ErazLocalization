function saveiOS(project) {
    const fileName = "Localizable.strings";
    let lprojects = createCstomJson(project);
    let zip = new JSZip();
    for (let lprojectName in lprojects) {
        let currentProj = zip.folder(lprojectName + ".lproj");
        let fileText = '';
        let lproject = lprojects[lprojectName];
        lproject.forEach(function (pair) {
            for (let first in pair) {
                if (pair[first].isMobile === true) {
                    fileText += '"' + first.toString() + '" = "' + pair[first].key.toString() + '";';
                    if (!pair[first].comment.toString().isEmpty) {
                        fileText += "// " + pair[first].comment.toString();
                    }
                    fileText += "\n";
                }
            }
        });
        currentProj.file(fileName, fileText);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-iOS.zip");
    })
}

/*
***** Structure ****
* { `languageCode` : [{ `key` : `value`},
*                     { `key` : `value`}...
*                    ],
*                    ....
* }
*/

function createCstomJson(project) {
    let localizations = project.localization;
    let languages = project.languages;
    let lprojectNames = [];
    Object.values(languages).forEach(function (value) {
        lprojectNames.push(value.langCode)
    });
    let lprojects = {};
    lprojectNames.forEach(function (projectName) {
        let projects = [];
        Object.values(localizations).forEach(function (localValue) {
            let proj = {};
            Object.values(localValue).forEach(function (value) {
                let object = Object.values(value.values).find(function (value1) {
                    return value1.lang_key === projectName;
                });
                proj[value.key] = {"key" : object.lang_value,
                                   "comment": value.comment,
                                   "isMobile": value.isMobile};
            });
            projects.push(proj);
        });
        lprojects[projectName] = projects;
    });
    return lprojects
}

function saveAndroid(project) {
    let projects = createCstomJson(project);
    let zip = new JSZip();
    for (let projectName in projects) {
        let xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<resources>\n";
        let currentproject = zip.folder("values-" + projectName);
        let proj = projects[projectName];
        proj.forEach(function (pair) {
            for (let first in pair) {
                if (pair[first].isMobile === true) {
                    xml += "\t<string name=\"" + first.toString() + '\">' + pair[first].key.toString() + "</string>";
                    if (typeof pair[first].comment !== 'undefined') {
                        xml += "<!--" + pair[first].comment.toString() + "-->"
                    }
                    xml += "\n"
                }
            }
        });
        xml += "</resources>";
        currentproject.file("strings.xml", xml);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-Android.zip");
    })
}

function saveWeb(project) {
    let projects = createCstomJson(project);
    let zip = new JSZip();
    for (let projectName in projects) {
        let currentproject = {};
        let proj = projects[projectName];
        proj.forEach(function (pair) {
            for (let first in pair) {
                currentproject[first] = pair[first].key
            }
        });
        zip.file("localization-" + projectName + ".json", JSON.stringify(currentproject, null, "\t"));
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-Web.zip");
    })
}
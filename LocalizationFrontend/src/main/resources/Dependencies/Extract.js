function saveiOS(project) {
    const fileName = "Localizable.strings";
    let lprojects = createCstomJson(project);
    let zip = new JSZip();
    for (let lprojectName in lprojects) {
        let currentProj = zip.folder(lprojectName + ".lproj");
        let fileText = generateIosString(project, lprojectName);
        currentProj.file(fileName, fileText);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-iOS.zip");
    })
}

function generateIosString(project, lprojectName) {
    let lprojects = createCstomJson(project);
    let fileText = '';
    let lproject = lprojects[lprojectName];
    lproject.forEach(function (pair) {
    for (let first in pair) {
             if (pair[first].isMobile === true) {
                 fileText += '"' + first.toString() + '" = "' + pair[first].key.toString().replace("%s", "%@") + '";';
                    if (pair[first].comment.toString() != "") {
                        fileText += "// " + pair[first].comment.toString();
                    }
                    fileText += "\n";
                }
            }
        });
     return fileText
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
                if (object === undefined) {
                } else {
                    proj[value.key] = {"key" : object.lang_value,
                                       "comment": value.comment,
                                       "isMobile": value.isMobile};
                }
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
        let xml = generateAndroidString(project, projectName);
        let currentproject = zip.folder("values-" + projectName);
        currentproject.file("strings.xml", xml);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-Android.zip");
    })
}

function generateAndroidString(project, projectName) {
    let projects = createCstomJson(project);
    let xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<resources>\n";
    let proj = projects[projectName];
            proj.forEach(function (pair) {
                for (let first in pair) {
                    if (pair[first].isMobile === true) {
                        xml += "\t<string name=\"" + first.toString() + '\">' + pair[first].key.toString().replace("%@", "%s") + "</string>";
                        if (pair[first].comment.toString() != "") {
                            console.log(pair[first].comment)
                            xml += "<!--" + pair[first].comment.toString() + "-->"
                        }
                        xml += "\n"
                    }
                }
            });
            xml += "</resources>";
            return xml
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

function htmlEntities(str) {
                    var htmlString = String(str).replace(/&/g, '&amp;').replace(/</g,     '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
                    return htmlString;
            }
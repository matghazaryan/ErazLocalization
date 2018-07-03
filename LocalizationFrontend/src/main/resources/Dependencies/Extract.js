function saveiOS(project) {
    var fileName = "Localizable.strings";
    var lprojects = createCstomJson(project)
    var zip = new JSZip();
    for (var lprojectName in lprojects) {
        var currentProj = zip.folder(lprojectName + ".lproj");
        var fileText = '';
        var lproject = lprojects[lprojectName];
        lproject.forEach(function (pair) {
            for (var first in pair) {
                fileText += '"' + first.toString() + '" = "' + pair[first].key.toString() + '";';
            }
            if (!pair[first].comment.toString().isEmpty) {
                fileText += "// " + pair[first].comment.toString();
            }
            fileText += "\n";
        });
        currentProj.file(fileName, fileText);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-iOS.zip");
    })
}

function createCstomJson(project) {
    var localizations = project.localization;
    var languages = project.languages;
    var lprojectNames = [];
    Object.values(languages).forEach(function (value) {
        lprojectNames.push(value.langCode)
    });
    var lprojects = {};
    lprojectNames.forEach(function (projectName) {
        var projects = [];
        Object.values(localizations).forEach(function (localValue) {
            var proj = {};
            Object.values(localValue).forEach(function (value) {
                var object = Object.values(value.values).find(function (value1) {
                    return value1.lang_key == projectName;
                });
                proj[value.key] = {"key" : object.lang_value,
                                   "comment" : object.lang_comment ? object.lang_comment : ''};
            });
            projects.push(proj);
        });
        lprojects[projectName] = projects;
    });
    return lprojects
}

function saveAndroid(project) {
    var projects = createCstomJson(project);
    var zip = new JSZip();
    for (var projectName in projects) {
        var xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<resources>\n    ";
        var currentproject = zip.folder("values-" + projectName);
        var proj = projects[projectName];
        proj.forEach(function (pair) {
            for (var first in pair) {
                xml += "\t<string name=\"" + first.toString() + '\">' + pair[first].key.toString() + "</string>";
                if (!pair[first].comment.toString().isEmpty) {
                    xml += "<!--" + pair[first].comment.toString() + "-->"
                }
                xml += "\n"
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
    var projects = createCstomJson(project);
    var zip = new JSZip();
    for (var projectName in projects) {
        var currentproject = {};
        var proj = projects[projectName];
        proj.forEach(function (pair) {
            for (var first in pair) {
                currentproject[first] = pair[first].key
            }
        });
        zip.file("localization-" + projectName + ".json", JSON.stringify(currentproject, null, "\t"));
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, project["name"] + "-Web.zip");
    })
}
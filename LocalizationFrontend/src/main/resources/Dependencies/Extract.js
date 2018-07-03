function saveiOS(project) {
    var localizations = project.localization;
    var languages = project.languages;
    var fileName = "Localizable.strings";
    var lprojectNames = [];
    Object.values(languages).forEach(function (value) {
        lprojectNames.push(value.langCode)
    });
    var lprojects = {};
    lprojectNames.forEach(function (projectName) {
        var projects = [];
        Object.values(localizations).forEach(function (localValue) {
            var project = {};
            Object.values(localValue).forEach(function (value) {
                var object = Object.values(value.values).find(function (value1) {
                    return value1.lang_key == projectName;
                });
                project[value.key] = object.lang_value;
            });
            projects.push(project);
        });
        lprojects[projectName] = projects;
    });
    var zip = new JSZip();
    for (var lprojectName in lprojects) {
        var currentProj = zip.folder(lprojectName + ".lproj");
        var fileText = '';
        var lproject = lprojects[lprojectName];
        lproject.forEach(function (pair) {
            for (first in pair) {
                fileText += '"' + first.toString() + '" = "' + pair[first].toString() + '";\n';
            }
        });
        currentProj.file(fileName, fileText);
    }
    zip.generateAsync({type: "blob"}).then(function (blob) {
        saveAs(blob, "iOS.zip");
    })
}
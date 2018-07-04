



function initScreenAutocompleteList(screenNames) {
    var element = document.getElementById("screen_autocomplete_input");
    var map = new Object();
    screenNames.forEach(function(screenName) {
        console.log(screenName);
        map[screenName] = null;
    });
    M.Autocomplete.init(element, { data: map });
}

function initTypeAutocompleteList(types) {
    var element = document.getElementById("type_autocomplete_input");
    var map = new Object();
    types.forEach(function(type) {
        console.log(type);
        map[type] = null;
    });
    M.Autocomplete.init(element, { data: map });
}
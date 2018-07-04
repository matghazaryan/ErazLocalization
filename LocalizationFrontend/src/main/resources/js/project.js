// window.onload = function(e){
//
//     var url = new URL(window.location.href);
//     var projectAlias = url.searchParams.get("alias");
//     console.log(projectAlias);
// }


function initScreenAutocompleteList(screenNames) {
    var element = document.getElementById("screen-autocomplete-input");
    var options = {};
    var dict = {};

    console.log(screenNames);

    var index, len;
    for (index = 0, len = screenNames.length; index < len; ++index) {
        dict[screenNames[index]] = "";
        console.log(screenNames[index]);
    }


    console.log(dict);

    options.data = dict;

    console.log(options);


    // var dict = screenNames.map(function(currentValue, index, arr) {
    //     return {currentValue: null};
    // });


    M.Autocomplete.init(element, options);
}

function initKeyAutocompleteList(keys) {

}
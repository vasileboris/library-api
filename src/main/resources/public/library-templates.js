(function() {
    "use strict";

    window.templates = {
        fill: fill,
        fillArray: fillArray
    }

    function fill(template, object) {
        var result = template;
        Object.keys(object).forEach(function(key){
            result = result.replace(new RegExp("{{"+key+"}}", "g"), object[key]);
        });
        return result.trim();
    }

    function fillArray(template, array) {
        var result = "";
        array.forEach(function(object){
            result +=fill(template, object);
        })
    }

})();

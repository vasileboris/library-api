(function() {
    "use strict";

    window.templates = {
        fill: fill,
        fillArray: fillArray,
        concatenateArray: concatenateArray
    }

    function fill(template, object) {
        var result = template;
        Object.keys(object).forEach(function(key){
            var value = object[key];
            if(value instanceof Array) {
                value = concatenateArray(value, ",");
            }
            result = result.replace(new RegExp("{{"+key+"}}", "g"), value);
        });
        return result.trim();
    }

    function fillArray(template, array) {
        var result = "";
        array.forEach(function(object){
            result +=fill(template, object);
        })
    }

    function concatenateArray(array, delimiter) {
        return array.reduce(function(temp, element, index){
            var result;
            if(temp) {
                result = temp + delimiter + element;
            } else {
                result = element;
            }
            return result;
        });
    }

})();

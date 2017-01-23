(function() {
    "use strict";

    window.css = {
        addStyle: addStyle,
        removeStyle: removeStyle
    }

    function addStyle(existingClass, style) {
        if(existingClass.match(new RegExp(style, "g"))) {
            return existingClass;
        }
        return existingClass + " " + style;
    }

    function removeStyle(existingClass, style) {
        var newClass = existingClass.replace(new RegExp(style, "g"), "");
        return newClass.replace(/\s{1,}/g, " ")
    }

})();

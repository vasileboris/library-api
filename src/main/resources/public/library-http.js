(function() {
    "use strict";

    window.http = {
        getRequestParameters: getRequestParameters,
        getRequestParameter: getRequestParameter,
        navigateTo: navigateTo
    }

    function getRequestParameters() {
        var queryString = location.search.replace("?", "");
        if(!queryString) return {};

        var response = {};
        queryString.split("&").forEach(function(queryElement) {
            var tokens = queryElement.split("=");
            if(tokens.length > 1) {
                response[tokens[0]] = tokens[1];
            }
        });
        return response;
    }

    function getRequestParameter(param) {
        return getRequestParameters()[param];
    }

    function navigateTo(url) {
        location.href = url;
    }

})();

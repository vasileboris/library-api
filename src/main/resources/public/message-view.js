(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.messageView = {
        render: render
    };

    function render(message) {
        var messageDiv = document.getElementById("message-div");
        messageDiv.innerHTML = getMessageInfo(message);
    }

    function getMessageInfo(message) {
        if(!message) return "";

        return templates.fill(document.getElementById("message-template").innerHTML, message);
    }

})();

(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.bookView = {
        render: render
    };

    function render(book) {
        var bookDiv = document.getElementById("book-div");
        bookDiv.innerHTML = getBookInfo(book);
    }

    function getBookInfo(book) {
        var info = "";
        if(book) {
            info += getBookDataInfo(book.getData());
        }
        return info;
    }

    function getBookDataInfo(bookData) {
        return templates.fill(document.getElementById("book-template").innerHTML, bookData);
    }

})();

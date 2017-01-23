(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.bookView = {
        render: render,
        clear: clear
    };

    function render(book) {
        var bookDiv = document.getElementById("book-div");
        bookDiv.innerHTML = getBookInfo(book);
    }

    function clear() {
        document.getElementById("add-book-isbn10-text").value="";
        document.getElementById("add-book-isbn13-text").value="";
        document.getElementById("add-book-title-text").value="";
        document.getElementById("add-book-authors-text").value="";
        document.getElementById("add-book-pages-text").value="";
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

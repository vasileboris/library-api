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
        $("#book-div").html(getBookInfo(book));
    }

    function clear() {
        $("#add-book-isbn10-text").val("");
        $("#add-book-isbn13-text").val("");
        $("#add-book-title-text").val("");
        $("#add-book-authors-text").val("");
        $("#add-book-pages-text").val("");
    }

    function getBookInfo(book) {
        var info = "";
        if(book) {
            info += getBookDataInfo(book.getData());
        }
        return info;
    }

    function getBookDataInfo(bookData) {
        return templates.fill($("#book-template").html(), bookData);
    }

})();

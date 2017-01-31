(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.booksView = {
        render: render,
        clear: clear
    };

    function render(books) {
        $("#books-div").html(getBooksInfo(books));
    }

    function clear() {
        $("#search-books-text").val("");
    }

    function getBooksInfo(books) {
        var info = "";
        books.getData().forEach(function(bookData, i) {
            info += getBookDataInfo(bookData);
        });
        return info;
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

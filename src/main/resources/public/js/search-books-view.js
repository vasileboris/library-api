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
        var booksDiv = document.getElementById("books-div");
        booksDiv.innerHTML = getBooksInfo(books);
    }

    function clear() {
        document.getElementById("search-books-text").value="";
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
        return templates.fill(document.getElementById("book-template").innerHTML, bookData);
    }

})();

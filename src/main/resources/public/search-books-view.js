(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.booksView = {
        render: render
    };

    function render(books) {
        var booksDiv = document.getElementById("books-div");
        booksDiv.innerHTML = getBooksInfo(books);
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

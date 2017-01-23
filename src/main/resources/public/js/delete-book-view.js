(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.bookView = {
        render: render
    };

    function render(book) {
        if(!book) {
            return;
        }

        var bookData = book.getData();
        document.getElementById("delete-book-isbn10-text").value = bookData.isbn10;
        document.getElementById("delete-book-isbn13-text").value = bookData.isbn13;
        document.getElementById("delete-book-title-text").value = bookData.title;
        document.getElementById("delete-book-authors-text").value = templates.concatenateArray(bookData.authors, ",");
        document.getElementById("delete-book-pages-text").value = bookData.pages;
    }

})();

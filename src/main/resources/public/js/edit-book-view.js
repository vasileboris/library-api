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
        document.getElementById("edit-book-isbn10-text").value = bookData.isbn10;
        document.getElementById("edit-book-isbn13-text").value = bookData.isbn13;
        document.getElementById("edit-book-title-text").value = bookData.title;
        document.getElementById("edit-book-authors-text").value = templates.concatenateArray(bookData.authors, ",");
        document.getElementById("edit-book-pages-text").value = bookData.pages;
    }

})();

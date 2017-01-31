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
        $("#delete-book-isbn10-text").val(bookData.isbn10);
        $("#delete-book-isbn13-text").val(bookData.isbn13);
        $("#delete-book-title-text").val(bookData.title);
        $("#delete-book-authors-text").val(templates.concatenateArray(bookData.authors, ","));
        $("#delete-book-pages-text").val(bookData.pages);
    }

})();

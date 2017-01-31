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
        $("#edit-book-isbn10-text").val(bookData.isbn10);
        $("#edit-book-isbn13-text").val(bookData.isbn13);
        $("#edit-book-title-text").val(bookData.title);
        $("#edit-book-authors-text").val(templates.concatenateArray(bookData.authors, ","));
        $("#edit-book-pages-text").val(bookData.pages);
    }

})();

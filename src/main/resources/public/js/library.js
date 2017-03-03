$(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    var books = [
        {
            uuid: '1e4014b1-a551-4310-9f30-590c3140b695',
            isbn10: 'ISBN 10',
            isbn13: 'ISBN 13',
            title: 'Title',
            authors: ['Author 1', 'Author 2'],
            pages: 101
        }
    ];

    new bookcase.BooksView(books);

});

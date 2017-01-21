(function() {
  "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.Book = function(uuid, isbn10, isbn13, title, authors, pages) {
        this.getData = function() {
            return {
                "uuid": uuid,
                "isbn10": isbn10,
                "isbn13": isbn13,
                "title": title,
                "authors": authors.slice(),
                "pages": pages
            };
        };
    }

    bookcase.Books = function() {
        var items = [];

        this.addBooks = function(booksData) {
                booksData.forEach(function(bookData) {
                items.push(addBook(bookData));
            });
        }

        function addBook(bookData) {
            return new bookcase.Book(bookData.uuid,
                bookData.isbn10,
                bookData.isbn13,
                bookData.title,
                bookData.authors,
                bookData.pages);
        };

        this.getData = function() {
            return items.map(function(book){
                return book.getData();
            });
        };
    }

})();

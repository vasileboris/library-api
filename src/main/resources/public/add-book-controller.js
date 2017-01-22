(function() {
    "use strict";

    var book;
    var message;

    function initModels() {
        book = null;
        message = null;
    }

    function init() {
        initModels();

        var saveBookButton = document.getElementById("save-book-button");
        saveBookButton.addEventListener("click", saveBookClick);

        render();
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);

        if(!message) {
            document.getElementById("add-book-isbn10-text").value="";
            document.getElementById("add-book-isbn13-text").value="";
            document.getElementById("add-book-title-text").value="";
            document.getElementById("add-book-authors-text").value="";
            document.getElementById("add-book-pages-text").value="";
        }
    }

    function saveBookClick() {
        initModels();

        var xhr = new XMLHttpRequest();
        var url = "/users/boris/books";
        xhr.addEventListener("load", function(){
            if(xhr.status === 201) {
                searchBook(xhr.getResponseHeader("Location"));
            } else {
                message = {"message": "Cannot save the book on the server! (" + xhr.status + ")"};
            }
            render();
        });
        xhr.addEventListener("error", function(){
            message = {"message" : "Cannot save the book on the server!"};
            render();
        });
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-type", "application/json;charset=UTF-8");
        xhr.send(JSON.stringify(createBook().getData()));
    }

    function createBook() {
        return new bookcase.Book(null,
            document.getElementById("add-book-isbn10-text").value,
            document.getElementById("add-book-isbn13-text").value,
            document.getElementById("add-book-title-text").value,
            document.getElementById("add-book-authors-text").value.split(","),
            document.getElementById("add-book-pages-text").value);
    }

    function searchBook(url) {
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", function(){
            if(xhr.status === 200) {
                var bookData = JSON.parse(xhr.responseText);
                book = new bookcase.Book(bookData.uuid,
                    bookData.isbn10,
                    bookData.isbn13,
                    bookData.title,
                    bookData.authors,
                    bookData.pages);
            } else {
                message = {"message": "Cannot retrieve the book from the server! (" + xhr.status + ")"};
            }
            render();
        });
        xhr.addEventListener("error", function(){
            message = {"message" : "Cannot retrieve the book from the server!"};
            render();
        });
        xhr.open("GET", url);
        xhr.send();
    }

    init();

})();

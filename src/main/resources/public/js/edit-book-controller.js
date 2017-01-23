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

        var updateBookButton = document.getElementById("update-book-button");
        updateBookButton.addEventListener("click", updateBookClick);

        render();

        searchBook()
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);
    }

    function updateBookClick() {
        initModels();

        var editBookDiv = document.getElementById("edit-book-div");
        editBookDiv.className = css.addStyle(editBookDiv.className, "waiting");

        var xhr = new XMLHttpRequest();
        var uuid = http.getRequestParameter("uuid");
        var url = "/users/boris/books/" + uuid;
        xhr.addEventListener("load", function(){
            editBookDiv.className = css.removeStyle(editBookDiv.className, "waiting");

            if(xhr.status === 200) {
                searchBook();
            } else {
                message = {"message": "Cannot update the book on the server! (" + xhr.status + ")"};
            }
            render();
        });
        xhr.addEventListener("error", function(){
            editBookDiv.className = css.removeStyle(editBookDiv.className, "waiting");

            message = {"message" : "Cannot update the book on the server!"};
            render();
        });
        xhr.open("PUT", url);
        xhr.setRequestHeader("Content-type", "application/json;charset=UTF-8");
        xhr.send(JSON.stringify(createBook().getData()));
    }

    function createBook() {
        return new bookcase.Book(http.getRequestParameter("uuid"),
            document.getElementById("edit-book-isbn10-text").value,
            document.getElementById("edit-book-isbn13-text").value,
            document.getElementById("edit-book-title-text").value,
            document.getElementById("edit-book-authors-text").value.split(","),
            document.getElementById("edit-book-pages-text").value);
    }

    function searchBook() {
        var editBookDiv = document.getElementById("edit-book-div");
        editBookDiv.className = css.addStyle(editBookDiv.className, "waiting");

        var uuid = http.getRequestParameter("uuid");
        var url = "/users/boris/books/" + uuid;
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", function(){
            editBookDiv.className = css.removeStyle(editBookDiv.className, "waiting");

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
            editBookDiv.className = css.removeStyle(editBookDiv.className, "waiting");

            message = {"message" : "Cannot retrieve the book from the server!"};
            render();
        });
        xhr.open("GET", url);
        xhr.send();
    }

    init();

})();

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

        var deleteBookButton = document.getElementById("delete-book-button");
        deleteBookButton.addEventListener("click", deleteBookClick);

        render();

        searchBook()
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);
    }

    function deleteBookClick() {
        initModels();

        var deleteBookDiv = document.getElementById("delete-book-div");
        deleteBookDiv.className = css.addStyle(deleteBookDiv.className, "waiting");

        var xhr = new XMLHttpRequest();
        var uuid = http.getRequestParameter("uuid");
        var url = "/users/boris/books/" + uuid;
        xhr.addEventListener("load", function(){
            deleteBookDiv.className = css.removeStyle(deleteBookDiv.className, "waiting");

            if(xhr.status === 200) {
                http.navigateTo("/search-books");
            } else {
                message = {"message": "Cannot delete the book on the server! (" + xhr.status + ")"};
            }
            render();
        });
        xhr.addEventListener("error", function(){
            deleteBookDiv.className = css.removeStyle(deleteBookDiv.className, "waiting");

            message = {"message" : "Cannot delete the book on the server!"};
            render();
        });
        xhr.open("DELETE", url);
        xhr.send();
    }

    function createBook() {
        return new bookcase.Book(http.getRequestParameter("uuid"),
            document.getElementById("delete-book-isbn10-text").value,
            document.getElementById("delete-book-isbn13-text").value,
            document.getElementById("delete-book-title-text").value,
            document.getElementById("delete-book-authors-text").value.split(","),
            document.getElementById("delete-book-pages-text").value);
    }

    function searchBook() {
        var deleteBookDiv = document.getElementById("delete-book-div");
        deleteBookDiv.className = css.addStyle(deleteBookDiv.className, "waiting");

        var uuid = http.getRequestParameter("uuid");
        var url = "/users/boris/books/" + uuid;
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", function(){
            deleteBookDiv.className = css.removeStyle(deleteBookDiv.className, "waiting");

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
            deleteBookDiv.className = css.removeStyle(deleteBookDiv.className, "waiting");

            message = {"message" : "Cannot retrieve the book from the server!"};
            render();
        });
        xhr.open("GET", url);
        xhr.send();
    }

    init();

})();

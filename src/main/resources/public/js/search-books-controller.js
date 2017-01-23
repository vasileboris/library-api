(function() {
    "use strict";

    var books;
    var message;

    function initModels() {
        books = new bookcase.Books();
        message = null;
    }

    function init() {
        initModels();

        var searchBooksButton = document.getElementById("search-books-button");
        searchBooksButton.addEventListener("click", searchBooksClick);

        render();
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.booksView.render(books);

        if(!message) {
            bookcase.booksView.clear();
        }
    }

    function searchBooksClick() {
        initModels();

        var searchBooksDiv = document.getElementById("search-books-div");
        searchBooksDiv.className = css.addStyle(searchBooksDiv.className, "waiting");

        var xhr = new XMLHttpRequest();
        var url = "/users/boris/books?searchText=" + document.getElementById("search-books-text").value;
        xhr.addEventListener("load", function(){
            searchBooksDiv.className = css.removeStyle(searchBooksDiv.className, "waiting");

            if(xhr.status === 200) {
                var booksData = JSON.parse(xhr.responseText);
                if(booksData.length != 0) {
                    books.addBooks(booksData);
                }
            } else {
                message = {"message": "Cannot retrieve the books from the server! (" + xhr.status + ")"};
            }
            render();
        });
        xhr.addEventListener("error", function(){
            searchBooksDiv.className = css.removeStyle(searchBooksDiv.className, "waiting");

            message = {"message" : "Cannot retrieve the books from the server!"};
            render();
        });
        xhr.open("GET", url);
        xhr.send();
    }

    init();

})();

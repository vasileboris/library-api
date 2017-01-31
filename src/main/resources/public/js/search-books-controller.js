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

        $("#search-books-button").on("click", searchBooksClick);

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

        var searchBooksDiv = $("#search-books-div");
        searchBooksDiv.addClass("waiting");

        var url = "/users/boris/books?searchText=" + $("#search-books-text").val();
        $.get(url).done(function(booksData, textStatus, jqXHR) {
            if(jqXHR.status === 200) {
                if(booksData && booksData.length != 0) {
                    books.addBooks(booksData);
                }
            } else {
                message = {"message": "Cannot retrieve the books from the server! (" + jqXHR.status + ")"};
            }
        }).fail(function(jqXHR, textStatus, errorThrown) {
                message = {"message": "Cannot retrieve the books from the server! (" + jqXHR.status + ")"};
        }).always(function() {
            searchBooksDiv.removeClass("waiting");
            render();
        });
    }

    init();

})();

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

        $("#save-book-button").on("click", saveBookClick);

        render();
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);

        if(!message) {
            bookcase.bookView.clear();
        }
    }

    function saveBookClick() {
        initModels();

        var addBookDiv = $("#add-book-div");
        addBookDiv.addClass("waiting");

        $.ajax({
            type: "POST",
            url: "/users/boris/books",
            data: JSON.stringify(createBook().getData()),
            contentType: "application/json;charset=UTF-8",
            success: function(bookData, textStatus, jqXHR) {
                if(jqXHR.status === 201) {
                    searchBook(jqXHR.getResponseHeader("Location"));
                } else {
                    message = {"message": "Cannot save the book on the server! (" + jqXHR.status + ")"};
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                message = {"message": "Cannot save the book on the server! (" + jqXHR.status + ")"};
            },
            complete: function() {
                addBookDiv.removeClass("waiting");
                render();
            }
        });
    }

    function createBook() {
        return new bookcase.Book(null,
            $("#add-book-isbn10-text").val(),
            $("#add-book-isbn13-text").val(),
            $("#add-book-title-text").val(),
            $("#add-book-authors-text").val().split(","),
            $("#add-book-pages-text").val());
    }

    function searchBook(url) {
        var addBookDiv = $("#add-book-div");
        addBookDiv.addClass("waiting");

        $.get(url).done(function(bookData, textStatus, jqXHR) {
            if(jqXHR.status === 200) {
                book = new bookcase.Book(bookData.uuid,
                    bookData.isbn10,
                    bookData.isbn13,
                    bookData.title,
                    bookData.authors,
                    bookData.pages);
            } else {
                message = {"message": "Cannot retrieve the book from the server! (" + jqXHR.status + ")"};
            }
        }).fail(function(jqXHR, textStatus, errorThrown) {
                message = {"message": "Cannot retrieve the book from the server! (" + jqXHR.status + ")"};
        }).always(function() {
            addBookDiv.removeClass("waiting");
            render();
        });
    }

    init();

})();

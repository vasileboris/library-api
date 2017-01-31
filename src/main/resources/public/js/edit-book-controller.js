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

        $("#update-book-button").on("click", updateBookClick);

        render();

        searchBook()
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);
    }

    function updateBookClick() {
        initModels();

        var editBookDiv = $("#edit-book-div");
        editBookDiv.addClass("waiting");

        $.ajax({
            method: "PUT",
            url: "/users/boris/books/" + http.getRequestParameter("uuid"),
            data: JSON.stringify(createBook().getData()),
            contentType: "application/json;charset=UTF-8",
            success: function(bookData, textStatus, jqXHR) {
                if(jqXHR.status === 200) {
                    searchBook();
                } else {
                    message = {"message": "Cannot update the book on the server! (" + jqXHR.status + ")"};
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                message = {"message": "Cannot update the book on the server! (" + jqXHR.status + ")"};
            },
            complete: function() {
                editBookDiv.removeClass("waiting");
                render();
            }
        });
    }

    function createBook() {
        return new bookcase.Book(http.getRequestParameter("uuid"),
            $("#edit-book-isbn10-text").val(),
            $("#edit-book-isbn13-text").val(),
            $("#edit-book-title-text").val(),
            $("#edit-book-authors-text").val().split(","),
            $("#edit-book-pages-text").val());
    }

    function searchBook() {
        var editBookDiv = $("#edit-book-div");
        editBookDiv.addClass("waiting");

        var uuid = http.getRequestParameter("uuid");
        var url = "/users/boris/books/" + uuid;
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
            editBookDiv.removeClass("waiting");
            render();
        });
    }

    init();

})();

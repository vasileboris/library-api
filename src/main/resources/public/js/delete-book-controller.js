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

        $("#delete-book-button").on("click", deleteBookClick);

        render();

        searchBook()
    }

    function render() {
        bookcase.messageView.render(message);
        bookcase.bookView.render(book);
    }

    function deleteBookClick() {
        initModels();

        var deleteBookDiv = $("#delete-book-div");
        deleteBookDiv.addClass("waiting");

        $.ajax({
            method: "DELETE",
            url: "/users/boris/books/" + http.getRequestParameter("uuid"),
            success: function(bookData, textStatus, jqXHR) {
                if(jqXHR.status === 200) {
                    http.navigateTo("/search-books");
                } else {
                    message = {"message": "Cannot delete the book on the server! (" + jqXHR.status + ")"};
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                message = {"message": "Cannot delete the book on the server! (" + jqXHR.status + ")"};
            },
            complete: function() {
                deleteBookDiv.removeClass("waiting");
                render();
            }
        });

    }

    function createBook() {
        return new bookcase.Book(http.getRequestParameter("uuid"),
            $("#delete-book-isbn10-text").val(),
            $("#delete-book-isbn13-text").val(),
            $("#delete-book-title-text").val(),
            $("#delete-book-authors-text").val().split(","),
            $("#delete-book-pages-text").val());
    }

    function searchBook() {
        var deleteBookDiv = $("#delete-book-div");
        deleteBookDiv.addClass("waiting");

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
            deleteBookDiv.removeClass("waiting");
            render();
        });
    }

    init();

})();

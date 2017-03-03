(function() {
    "use strict";

    if(window.bookcase === undefined) {
        window.bookcase = {};
    }

    bookcase.Book = Backbone.Model.extend({
        uuid: '',
        isbn10: '',
        isbn13: '',
        title: '',
        authors: [],
        pages: 0
    });

})();

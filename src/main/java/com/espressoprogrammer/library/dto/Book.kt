package com.espressoprogrammer.library.dto

data class Book(val uuid: String?,
                val isbn10: String?,
                val isbn13: String?,
                val title: String?,
                val authors: List<Author>,
                val pages: Int?) {

    constructor() : this(null, null, null, null, emptyList(), null);
}
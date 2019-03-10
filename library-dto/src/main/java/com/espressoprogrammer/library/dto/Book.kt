package com.espressoprogrammer.library.dto

data class Book(val uuid: String? = null,
                val isbn10: String? = null,
                val isbn13: String? = null,
                val title: String? = null,
                val authors: List<String> = emptyList(),
                val image: String? = null,
                val pages: Int? = null)
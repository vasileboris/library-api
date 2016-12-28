package com.espressoprogrammer.library.dto

data class Author(val firstName: String?,
                  val lastName: String?) {

    constructor() : this(null, null);
}
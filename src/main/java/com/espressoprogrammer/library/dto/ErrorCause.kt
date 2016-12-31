package com.espressoprogrammer.library.dto

data class ErrorCause(val cause: String?,
                      val key: String?) {

    constructor() : this(null, null);
}
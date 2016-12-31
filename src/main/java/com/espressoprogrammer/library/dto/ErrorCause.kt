package com.espressoprogrammer.library.dto

data class ErrorCause(val causes: List<String>,
                      val key: String?) {

    constructor() : this(emptyList(), null);
}
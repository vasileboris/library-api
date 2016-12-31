package com.espressoprogrammer.library.dto

data class ErrorResponse(val type: Type,
                         val causes: List<ErrorCause>) {

    constructor() : this(Type.UNKNOWN, emptyList());

    enum class Type() {
        UNKNOWN,
        VALIDATION
    }

}
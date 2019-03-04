package com.espressoprogrammer.library.dto

data class ErrorResponse(val type: Type = Type.UNKNOWN,
                         val causes: List<ErrorCause> = emptyList()) {

    enum class Type {
        UNKNOWN,
        FIELD_VALIDATION,
        DATA_VALIDATION
    }

}
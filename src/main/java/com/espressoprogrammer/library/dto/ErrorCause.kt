package com.espressoprogrammer.library.dto

data class ErrorCause(val causes: List<String> = emptyList(),
                      val key: String? = null)
package com.espressoprogrammer.library.dto

data class DateReadingSession(val date: String?,
                              val lastReadPage: Int?,
                              val bookmark: String?) {

    constructor() : this(null, null, null);
}
package com.espressoprogrammer.library.dto

data class ReadingSession(val uuid: String?,
                          val bookUuid: String?,
                          val readingSessions: List<DateReadingSession>?) {

    constructor() : this(null, null, null);
}
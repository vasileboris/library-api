package com.espressoprogrammer.library.dto

data class ReadingSession(val uuid: String?,
                          val bookUuid: String?,
                          val deadline: String?,
                          val dateReadingSessions: List<DateReadingSession>) {

    constructor() : this(null, null, null, emptyList());
}
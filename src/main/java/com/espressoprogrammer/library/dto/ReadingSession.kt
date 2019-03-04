package com.espressoprogrammer.library.dto

data class ReadingSession(val uuid: String? = null,
                          val bookUuid: String? = null,
                          val deadline: String? = null,
                          val dateReadingSessions: List<DateReadingSession> = emptyList())
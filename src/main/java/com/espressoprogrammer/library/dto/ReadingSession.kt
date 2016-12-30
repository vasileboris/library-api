package com.espressoprogrammer.library.dto

data class ReadingSession(val uuid: String?,
                          val bookUuid: String?,
                          val date: String?,
                          val lastReadPage: Int?,
                          val bookmark: String?) {

    constructor() : this(null, null, null, null, null);
}
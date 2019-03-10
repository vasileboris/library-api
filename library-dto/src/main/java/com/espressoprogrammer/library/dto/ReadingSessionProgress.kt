package com.espressoprogrammer.library.dto

data class ReadingSessionProgress(val bookUuid: String? = null,
                                  val lastReadPage: Int? = null,
                                  val pagesTotal: Int? = null,
                                  val readPercentage: Int? = null,
                                  val averagePagesPerDay: Int? = null,
                                  val estimatedReadDaysLeft: Int? = null,
                                  val estimatedDaysLeft: Int? = null,
                                  val estimatedFinishDate: String? = null,
                                  val deadline: String? = null)

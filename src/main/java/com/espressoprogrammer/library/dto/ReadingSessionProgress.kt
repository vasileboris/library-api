package com.espressoprogrammer.library.dto

data class ReadingSessionProgress(val uuid: String,
                                  val bookUuid: String,
                                  val lastReadPage: Int,
                                  val pagesTotal: Int,
                                  val readPercentage: Int,
                                  val averagePagesPerDay: Int,
                                  val estimatedReadDaysLeft: Int,
                                  val estimatedDaysLeft: Int,
                                  val estimatedFinishDate: String)

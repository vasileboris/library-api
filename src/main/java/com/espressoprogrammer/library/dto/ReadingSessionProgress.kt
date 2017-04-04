package com.espressoprogrammer.library.dto

data class ReadingSessionProgress(val lastReadPage: Int,
                                  val pagesTotal: Int,
                                  val averagePagesPerDay: Int,
                                  val estimatedReadDaysLeft: Int,
                                  val estimatedDaysLeft: Int,
                                  val estimatedFinishDate: String)

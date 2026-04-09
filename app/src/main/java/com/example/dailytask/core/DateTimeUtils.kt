package com.example.dailytasks.core

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun Long.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    Instant.ofEpochMilli(this).atZone(zoneId).toLocalDateTime()

fun LocalDateTime.toEpochMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    atZone(zoneId).toInstant().toEpochMilli()

fun LocalDate.atStartOfDayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    atStartOfDay(zoneId).toInstant().toEpochMilli()

fun LocalDate.atEndOfDayMillis(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()

fun formatDate(date: LocalDate): String = date.format(dateFormatter)

fun formatTime(time: LocalTime): String = time.format(timeFormatter)

fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)

package com.example.dailytasks

import com.example.dailytasks.core.toEpochMillis
import com.example.dailytasks.domain.model.Task
import com.example.dailytasks.domain.service.DefaultScheduleService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import java.time.LocalDate

class ScheduleServiceTest {

    private val service = DefaultScheduleService()

    @Test
    fun `task spanning several hours is shown in each intersecting slot`() {
        val date = LocalDate.of(2025, 4, 10)
        val task = Task(
            id = 1,
            startTimeMillis = date.atTime(14, 30).toEpochMillis(),
            endTimeMillis = date.atTime(16, 10).toEpochMillis(),
            name = "Deep work",
            description = "Architecture refactoring"
        )

        val result = service.buildSchedule(listOf(task), date)

        assertEquals(1, result[14].tasks.size)
        assertEquals(1, result[15].tasks.size)
        assertEquals(1, result[16].tasks.size)
        assertTrue(result[13].tasks.isEmpty())
        assertTrue(result[17].tasks.isEmpty())
    }

    @Test
    fun `task crossing midnight appears in next day first slot`() {
        val date = LocalDate.of(2025, 4, 10)
        val nextDate = date.plusDays(1)
        val task = Task(
            id = 2,
            startTimeMillis = date.atTime(23, 30).toEpochMillis(),
            endTimeMillis = nextDate.atTime(1, 0).toEpochMillis(),
            name = "Night deploy",
            description = "Release monitoring"
        )

        val result = service.buildSchedule(listOf(task), nextDate)

        assertEquals(1, result[0].tasks.size)
        assertTrue(result[1].tasks.isEmpty())
        assertTrue(result[2].tasks.isEmpty())
    }
}

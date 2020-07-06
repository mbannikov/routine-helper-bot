package ru.mbannikov.routinehelperbot.timemeasurement.repository

import ru.mbannikov.routinehelperbot.config.property.CommonApplicationProperties
import ru.mbannikov.routinehelperbot.timemeasurement.entity.MeasureTimeLogItem
import java.time.DayOfWeek
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

interface MeasureTimeLogDao {
    fun getTodayDuration(): Duration

    fun getThisWeekDuration(): Duration

    fun saveMeasureTime(duration: Duration)

    fun saveNegativeMeasureTime(duration: Duration)
}

class MeasureTimeLogDaoImpl(
    commonProperties: CommonApplicationProperties,
    private val repository: MeasureTimeLogRepository
) : MeasureTimeLogDao {
    private val zoneId = commonProperties.timeZone

    override fun getTodayDuration(): Duration {
        val now = LocalDate.now()
        val today = now.atStartOfDay(zoneId).toInstant()
        val tomorrow = now.plusDays(1).atStartOfDay(zoneId).toInstant()

        return repository.findByDatetimeIsBetween(today, tomorrow)
            .asSequence()
            .map { it.duration }
            .sum()
            .let(Duration::ofSeconds)
    }

    override fun getThisWeekDuration(): Duration {
        val now = LocalDate.now()
        val thisWeekMonday = now.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).atStartOfDay(zoneId).toInstant()
        val nextWeekMonday = thisWeekMonday.plus(7, ChronoUnit.DAYS)

        return repository.findByDatetimeIsBetween(thisWeekMonday, nextWeekMonday)
            .asSequence()
            .map { it.duration }
            .sum()
            .let(Duration::ofSeconds)
    }

    override fun saveMeasureTime(duration: Duration) {
        val logItem = MeasureTimeLogItem(id = -1, datetime = Instant.now(), duration = duration.toSeconds())
        repository.save(logItem)
    }

    override fun saveNegativeMeasureTime(duration: Duration) {
        val logItem = MeasureTimeLogItem(id = -1, datetime = Instant.now(), duration = -duration.toSeconds())
        repository.save(logItem)
    }
}

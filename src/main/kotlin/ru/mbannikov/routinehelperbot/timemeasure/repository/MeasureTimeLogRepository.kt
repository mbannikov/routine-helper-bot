package ru.mbannikov.routinehelperbot.timemeasure.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.mbannikov.routinehelperbot.timemeasure.entity.MeasureTimeLogItem
import java.time.Instant

@Repository
interface MeasureTimeLogRepository : CrudRepository<MeasureTimeLogItem, Long>, MeasureTimeLogDao {

    fun findByDatetimeIsBetween(begin: Instant, end: Instant): Collection<MeasureTimeLogItem>

//    @Query(value = "SELECT SUM(duration) FROM measure_time_log where datetime = CURRENT_DATE", nativeQuery = true)
//    fun getTodayDuration(): Long
}

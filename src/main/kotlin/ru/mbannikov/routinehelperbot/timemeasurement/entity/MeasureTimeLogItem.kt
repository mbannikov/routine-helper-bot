package ru.mbannikov.routinehelperbot.timemeasurement.entity

import java.time.Instant
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "measure_time_log")
data class MeasureTimeLogItem(

    @Id
    @GeneratedValue
    val id: Long,

    val datetime: Instant,

    val duration: Long
)

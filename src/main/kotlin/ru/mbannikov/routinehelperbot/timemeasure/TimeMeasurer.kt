package ru.mbannikov.routinehelperbot.timemeasure

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalTime

@Component
class TimeMeasurer {
    private var timer: Timer? = null

    fun start() {
        timer = Timer()
    }

    fun finish(): Duration =
        timer?.run {
            timer = null
            duration
        } ?: throw TimerWasNotStarted()
}

private class Timer {
    private val start = LocalTime.now()

    val duration: Duration
        get() = Duration.between(start, LocalTime.now())
}

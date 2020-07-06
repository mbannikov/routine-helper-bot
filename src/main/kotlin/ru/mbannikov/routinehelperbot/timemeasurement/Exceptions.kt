package ru.mbannikov.routinehelperbot.timemeasurement

class TimerWasNotStarted : RuntimeException("timer was not started")

class MinusCommandWithoutArgs : RuntimeException("command requires MIN argument")

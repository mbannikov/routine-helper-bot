package ru.mbannikov.routinehelperbot.timemeasure

class TimerWasNotStarted : RuntimeException("timer was not started")

class MinusCommandWithoutArgs : RuntimeException("command requires MIN argument")

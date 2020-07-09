package ru.mbannikov.routinehelperbot.timemeasurement

class TimerHasToBeStartedException : RuntimeException("timer was not started")

class CommandWithoutArgsException : RuntimeException("the command requires argument")

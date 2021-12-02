package com.flounderguy.knifefightutilities.util

val <T> T.exhaustive: T
    get() = this

enum class DieType(val sides: Int) {
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D20(20)
}

fun rollDice(dieType: DieType): Int {
    return (1..dieType.sides).random()
}

enum class ResultRequest {
    ATTACK,
    DAMAGE,
    COUNTER,
    TIE,
    SECOND_WIND
}
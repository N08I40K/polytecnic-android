package ru.n08i40k.polytechnic.next.utils

infix fun <T> T?.or(data: T): T {
    if (this == null)
        return data
    return this
}

fun Int.fmtAsClockEntry(): String {
    return "0".repeat(if (this <= 9) 1 else 0) + this.toString()
}

fun Int.fmtAsClock(): String {
    val hours = this / 60
    val minutes = this % 60

    return hours.fmtAsClockEntry() + ":" + minutes.fmtAsClockEntry()
}

infix fun String.limit(count: Int): String {
    if (this.length <= count)
        return this

    return this
        .substring(0, count - 1)
        .trimEnd()
        .plus("…")
}
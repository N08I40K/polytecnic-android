package ru.n08i40k.polytechnic.next.utils

infix fun <T> T?.or(data: T): T {
    if (this == null)
        return data
    return this
}
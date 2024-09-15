package ru.n08i40k.polytechnic.next.data

import java.lang.Exception

sealed interface MyResult<out R> {
    data class Success<out T>(val data: T) : MyResult<T>
    data class Failure(val exception: Exception) : MyResult<Nothing>
}


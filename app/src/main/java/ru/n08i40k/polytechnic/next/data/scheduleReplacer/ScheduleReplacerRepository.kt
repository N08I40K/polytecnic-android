package ru.n08i40k.polytechnic.next.data.scheduleReplacer

import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer

interface ScheduleReplacerRepository {
    suspend fun getAll(): MyResult<List<ScheduleReplacer>>

    suspend fun setCurrent(
        fileName: String,
        fileData: ByteArray,
        fileType: String
    ): MyResult<Unit>

    suspend fun clear(): MyResult<Int>
}
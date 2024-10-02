package ru.n08i40k.polytechnic.next.data.schedule.impl

import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.data.scheduleReplacer.ScheduleReplacerRepository
import ru.n08i40k.polytechnic.next.model.ScheduleReplacer

class FakeScheduleReplacerRepository : ScheduleReplacerRepository {
    companion object {
        @Suppress("SpellCheckingInspection")
        val exampleReplacers: List<ScheduleReplacer> = listOf(
            ScheduleReplacer("test-etag", 236 * 1024),
            ScheduleReplacer("frgsjkfhg", 623 * 1024),
        )
    }

    override suspend fun getAll(): MyResult<List<ScheduleReplacer>> {
        return MyResult.Success(exampleReplacers)
    }

    override suspend fun setCurrent(
        fileName: String,
        fileData: ByteArray,
        fileType: String
    ): MyResult<Unit> {
        return MyResult.Success(Unit)
    }

    override suspend fun clear(): MyResult<Int> {
        return MyResult.Success(1)
    }
}
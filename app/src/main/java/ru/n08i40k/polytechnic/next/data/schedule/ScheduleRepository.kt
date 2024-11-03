package ru.n08i40k.polytechnic.next.data.schedule

import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.GroupOrTeacher

interface ScheduleRepository {
    suspend fun getGroup(): MyResult<GroupOrTeacher>

    suspend fun getTeacher(name: String): MyResult<GroupOrTeacher>
}
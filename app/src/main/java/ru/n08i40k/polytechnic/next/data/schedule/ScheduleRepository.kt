package ru.n08i40k.polytechnic.next.data.schedule

import ru.n08i40k.polytechnic.next.data.MyResult
import ru.n08i40k.polytechnic.next.model.Group

interface ScheduleRepository {
    suspend fun getGroup(): MyResult<Group>
}
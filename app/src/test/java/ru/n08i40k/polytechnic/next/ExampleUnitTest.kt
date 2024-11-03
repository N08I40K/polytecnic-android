package ru.n08i40k.polytechnic.next

import android.content.Context
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import ru.n08i40k.polytechnic.next.data.schedule.impl.FakeScheduleRepository

@RunWith(MockitoJUnitRunner.Silent::class)
class ExampleUnitTest {
    @Test
    fun getNameAndCabinetsShort_isNotThrow() {
        val mockContext = mock<Context> {
            on { getString(R.string.in_gym_lc) } doReturn "с/з"
            on { getString(R.string.lesson_break) } doReturn "Перемена"
        }
        val group = FakeScheduleRepository.exampleGroup

        for (day in group.days) {
            for (lesson in day.lessons) {
                lesson.getNameAndCabinetsShort(mockContext)
            }
        }
    }
}
package ru.n08i40k.polytechnic.next.data.schedule.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import ru.n08i40k.polytechnic.next.model.Day
import ru.n08i40k.polytechnic.next.model.Group
import ru.n08i40k.polytechnic.next.data.schedule.ScheduleRepository
import ru.n08i40k.polytechnic.next.model.Lesson
import ru.n08i40k.polytechnic.next.model.LessonTime
import ru.n08i40k.polytechnic.next.model.LessonType
import ru.n08i40k.polytechnic.next.data.MyResult

class FakeScheduleRepository : ScheduleRepository {
    @Suppress("SpellCheckingInspection")
    companion object {
        val exampleGroup = Group(
            name = "ИС-214/23", days = arrayListOf(
                Day(
                    name = "Понедельник",
                    nonNullIndices = arrayListOf(0, 1, 2, 3, 4, 5),
                    defaultIndices = arrayListOf(2, 3, 4, 5),
                    customIndices = arrayListOf(0, 1),
                    lessons = arrayListOf(
                        Lesson(
                            type = LessonType.CUSTOM,
                            defaultIndex = -1,
                            name = "Линейка",
                            time = LessonTime(510, 520),
                            cabinets = arrayListOf(),
                            teacherNames = arrayListOf(),
                        ),
                        Lesson(
                            type = LessonType.CUSTOM,
                            defaultIndex = -1,
                            name = "Разговор о важном",
                            time = LessonTime(525, 555),
                            cabinets = arrayListOf(),
                            teacherNames = arrayListOf(),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 1,
                            name = "Элементы высшей математики",
                            time = LessonTime(565, 645),
                            cabinets = arrayListOf("31", "12"),
                            teacherNames = arrayListOf("Цацаева Т.Н."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 2,
                            name = "Операционные системы и среды",
                            time = LessonTime(655, 735),
                            cabinets = arrayListOf("42", "52"),
                            teacherNames = arrayListOf("Сергачева А.О.", "Не Сергачева А.О."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 3,
                            name = "Физическая культура",
                            time = LessonTime(755, 835),
                            cabinets = arrayListOf("c/3"),
                            teacherNames = arrayListOf("Васюнин В.Г.", "Не Васюнин В.Г."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 4,
                            name = "МДК.05.01 Проектирование и дизайн информационных систем",
                            time = LessonTime(845, 925),
                            cabinets = arrayListOf("43"),
                            teacherNames = arrayListOf("Ивашова А.Н."),
                        ),
                        null,
                        null,
                    )
                ), Day(
                    name = "Вторник",
                    nonNullIndices = arrayListOf(0, 1, 2),
                    defaultIndices = arrayListOf(0, 1, 2),
                    customIndices = arrayListOf(),
                    lessons = arrayListOf(
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 1,
                            name = "Стандартизация, сертификация и техническое документоведение",
                            time = LessonTime(525, 605),
                            cabinets = arrayListOf("42"),
                            teacherNames = arrayListOf("Сергачева А.О."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 2,
                            name = "Элементы высшей математики",
                            time = LessonTime(620, 700),
                            cabinets = arrayListOf("31"),
                            teacherNames = arrayListOf("Цацаева Т.Н."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 3,
                            name = "Основы проектирования баз данных",
                            time = LessonTime(720, 800),
                            cabinets = arrayListOf("21"),
                            teacherNames = arrayListOf("Чинарева Е.А."),
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                    )
                ), Day(
                    name = "Среда",
                    nonNullIndices = arrayListOf(0, 1, 2),
                    defaultIndices = arrayListOf(0, 1, 2),
                    customIndices = arrayListOf(),
                    lessons = arrayListOf(
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 1,
                            name = "Операционные системы и среды",
                            time = LessonTime(525, 605),
                            cabinets = arrayListOf("42"),
                            teacherNames = arrayListOf("Сергачева А.О."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 2,
                            name = "Элементы высшей математики",
                            time = LessonTime(620, 700),
                            cabinets = arrayListOf("31"),
                            teacherNames = arrayListOf("Цацаева Т.Н."),
                        ),
                        Lesson(
                            type = LessonType.DEFAULT,
                            defaultIndex = 3,
                            name = "МДК.05.01 Проектирование и дизайн информационных систем",
                            time = LessonTime(720, 800),
                            cabinets = arrayListOf("43"),
                            teacherNames = arrayListOf("Ивашова А.Н."),
                        ),
                        null,
                        null,
                        null,
                        null,
                        null,
                    )
                )
            )
        )
    }

    private val group = MutableStateFlow<Group?>(exampleGroup)

    private var updateCounter: Int = 0

    override suspend fun getGroup(): MyResult<Group> {
        return withContext(Dispatchers.IO) {
            delay(1500)
            if (updateCounter++ % 3 == 0) MyResult.Failure(
                IllegalStateException()
            )
            else MyResult.Success(group.value!!)
        }
    }
}
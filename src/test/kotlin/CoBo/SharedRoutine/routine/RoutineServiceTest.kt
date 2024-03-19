package CoBo.SharedRoutine.routine

import CoBo.SharedRoutine.global.data.entity.Participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.data.entity.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class RoutineServiceTest {

    @Test
    fun getWeekTest() {
        val user = User(1, "테스트 유저", "refreshToken", 0, 0, "image")
        val routine = Routine(1, user, "테스트 루틴 제목", "테스트 루틴 내용", 1)
        val participation = Participation(1, user, routine, LocalDate.of(2022, 1, 1),
            LocalDate.of(2024, 12, 12), null, 0, "1101111".toInt(2))
        val checkedDate = listOf(
            LocalDate.of(2024, 3, 17),
            LocalDate.of(2024, 3, 18),
            LocalDate.of(2024, 3, 19),
            LocalDate.of(2024, 3, 20),
            LocalDate.of(2024, 3, 22),
            LocalDate.of(2024, 3, 24)
            )

        var checkedBit = 0
        for ((index, value) in participation.week.toString(2).padStart(7, '0').withIndex()) {
            if (value == '1' && checkedDate.contains(LocalDate.now().plusDays((index- LocalDate.now().dayOfWeek.value).toLong())))
                checkedBit = checkedBit or (1 shl (6 - index))
        }

        assertThat(checkedBit).isEqualTo("1101010".toInt(2))
    }
}
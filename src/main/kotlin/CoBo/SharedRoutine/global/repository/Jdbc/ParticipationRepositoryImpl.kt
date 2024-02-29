package CoBo.SharedRoutine.global.repository.Jdbc

import CoBo.SharedRoutine.global.repository.Custom.ParticipationRepositoryCustom
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class ParticipationRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
): ParticipationRepositoryCustom {

    @Transactional
    override fun insertParticipation(userId: Int, routineId: Int, goalDate: LocalDate, week: Int) {
        val sql = "INSERT INTO participation (user_kakao_id, routine_id, start_date, goal_date, check_count, week) VALUES (?, ?, ?, ?, ?, ?)"
        jdbcTemplate.update(sql, userId, routineId, LocalDate.now(), goalDate, 0, week)
    }

    @Transactional
    override fun updateGoalDate(userId: Int, routineId: Int, goalDate: LocalDate): Int {
        val sql = "UPDATE participation SET goal_date = ? WHERE user_kakao_id = ? AND routine_id = ?"
        return jdbcTemplate.update(sql, goalDate, userId, routineId)
    }

    override fun checkIfRecordExists(userId: Int, routineId: Int): Boolean {
        val sql = "SELECT COUNT(*) FROM participation WHERE user_kakao_id = ? AND routine_id = ?"
        val count = jdbcTemplate.queryForObject(sql, arrayOf(userId, routineId), Int::class.java)
        return count > 0
    }
}
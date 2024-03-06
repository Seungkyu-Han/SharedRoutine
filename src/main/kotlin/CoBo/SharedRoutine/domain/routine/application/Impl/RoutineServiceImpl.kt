package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetParticipationElementRes
import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.CheckedRoutine
import CoBo.SharedRoutine.global.data.entity.Participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.CheckedRoutineRepository
import CoBo.SharedRoutine.global.repository.ParticipationRepository
import CoBo.SharedRoutine.global.repository.RoutineRepository
import CoBo.SharedRoutine.global.repository.UserRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

@Service
class RoutineServiceImpl(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val participationRepository: ParticipationRepository,
    private val checkedRoutineRepository: CheckedRoutineRepository
): RoutineService {
    override fun post(routinePostReq: RoutinePostReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()
        val user = userRepository.findById(userId).orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = Routine(
            id = null,
            admin = user,
            title = routinePostReq.title,
            description = routinePostReq.description
        )

        routineRepository.save(routine)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun postParticipation(routinePostParticipationReq: RoutinePostParticipationReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routinePostParticipationReq.routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        if (participationRepository.existsByUserAndRoutine(user, routine))
            throw DuplicateKeyException("이미 참여한 루틴입니다.")

        participationRepository.save(Participation(
            id = null,
            user = user,
            routine = routine,
            startDate = LocalDate.now(),
            goalDate = routinePostParticipationReq.goalDate,
            lastCheckDate = null,
            checkCount = 0,
            week = routinePostParticipationReq.weekBit.toInt(2)
        ))

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patch(routineId: Int, description: String, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        if (routine.admin != user)
            throw IllegalAccessException("수정 권한이 없습니다.")

        routine.description = description
        routineRepository.save(routine)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patchParticipation(routineId: Int, goalDate: LocalDate, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        val participation = participationRepository.findByUserAndRoutine(user, routine)
            .orElseThrow{throw NoSuchElementException("일치하는 참여 정보가 없습니다.")}

        participation.goalDate = goalDate
        participationRepository.save(participation)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun postCheck(routineId: Int, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        val participation = participationRepository.findByUserAndRoutine(user, routine)
            .orElseThrow{throw NoSuchElementException("일치하는 참여 정보가 없습니다.")}

        if (checkedRoutineRepository.existsByDateAndParticipation(LocalDate.now(), participation))
            throw DuplicateKeyException("오늘 이미 완료한 루틴입니다.")

        checkedRoutineRepository.save(CheckedRoutine(
            id = null,
            participation = participation,
            date = LocalDate.now()
        ))

        participation.checkCount += 1
        participation.lastCheckDate = LocalDate.now()
        participationRepository.save(participation)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun getParticipation(authentication: Authentication): ResponseEntity<CoBoResponseDto<ArrayList<RoutineGetParticipationElementRes>>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routineGetParticipationElementResList = ArrayList<RoutineGetParticipationElementRes>()

        for (participation in participationRepository.findAllByUser(user)) {
            var checked = false
            if (participation.lastCheckDate == LocalDate.now()) checked = true

            routineGetParticipationElementResList.add(
                RoutineGetParticipationElementRes(
                    title = participation.routine.title,
                    achievementRate = calculateAchievementRate(participation),
                    checked = checked
                ))
        }

        return CoBoResponse(routineGetParticipationElementResList, CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    private fun calculateAchievementRate(participation: Participation): Int {
        val start = participation.startDate
        val goal = participation.goalDate.plusDays(1)

        var count = ChronoUnit.WEEKS.between(start, goal) * Integer.bitCount(participation.week)

        var idx = start.dayOfWeek.value % 7
        while (idx != (goal.dayOfWeek.value % 7)) {
            if ((participation.week shr (6 - idx)) and 1 == 1) count++
            idx = (idx + 1) % 7
        }

        return (participation.checkCount.toDouble() / count * 100).roundToInt()
    }
}
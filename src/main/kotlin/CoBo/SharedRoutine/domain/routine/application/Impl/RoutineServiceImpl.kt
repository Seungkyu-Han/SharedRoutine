package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.Participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.ParticipationRepository
import CoBo.SharedRoutine.global.repository.RoutineRepository
import CoBo.SharedRoutine.global.repository.UserRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.NoSuchElementException

@Service
class RoutineServiceImpl(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val participationRepository: ParticipationRepository
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
}
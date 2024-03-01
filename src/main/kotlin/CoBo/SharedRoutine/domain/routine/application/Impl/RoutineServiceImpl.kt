package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.ParticipationRepository
import CoBo.SharedRoutine.global.repository.RoutineRepository
import CoBo.SharedRoutine.global.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class RoutineServiceImpl(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val participationRepository: ParticipationRepository
): RoutineService {
    override fun post(routinePostReq: RoutinePostReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()
        val user = userRepository.findById(userId)

        val routine = Routine(
            id = null,
            admin = user.get(),
            title = routinePostReq.title,
            description = routinePostReq.description
        )

        routineRepository.save(routine)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun postParticipation(routinePostParticipationReq: RoutinePostParticipationReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()

        val routineId = routinePostParticipationReq.routineId
        val routine = routineRepository.findById(routineId)

        if (routine.isEmpty)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        if (participationRepository.checkIfRecordExists(userId, routineId))
            return ResponseEntity(HttpStatus.BAD_REQUEST)

        participationRepository.insertParticipation(userId, routineId, routinePostParticipationReq.goalDate, routinePostParticipationReq.weekBit.toInt(2))

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patch(routineId: Int, description: String, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()

        val routine = routineRepository.findById(routineId)

        if (routine.isEmpty)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        if (routine.get().admin.kakaoId != userId)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        routine.get().description = description
        routineRepository.save(routine.get())

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patchParticipation(routineId: Int, goalDate: LocalDate, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()

        if (participationRepository.updateGoalDate(userId, routineId, goalDate) == 0)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }
}
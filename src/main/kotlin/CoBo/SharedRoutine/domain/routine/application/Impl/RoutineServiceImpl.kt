package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.domain.routine.data.dto.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.data.dto.RoutinePostReq
import CoBo.SharedRoutine.global.config.jwt.JwtTokenProvider
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.ParticipationRepository
import CoBo.SharedRoutine.global.repository.RoutineRepository
import CoBo.SharedRoutine.global.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class RoutineServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository,
    private val participationRepository: ParticipationRepository
): RoutineService {
    override fun post(routinePostReq: RoutinePostReq, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val token = authorization.split(" ")[1]
        val userId = jwtTokenProvider.getUserId(token)
        val user = userRepository.findById(userId!!)

        val routine = Routine(
            id = null,
            admin = user.get(),
            title = routinePostReq.title,
            description = routinePostReq.description
        )

        routineRepository.save(routine)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun postParticipation(routinePostParticipationReq: RoutinePostParticipationReq, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val token = authorization.split(" ")[1]
        val userId = jwtTokenProvider.getUserId(token)

        val routineId = routinePostParticipationReq.routineId
        val routine = routineRepository.findById(routineId)

        if (routine.isEmpty)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        participationRepository.insertParticipation(userId!!, routineId, routinePostParticipationReq.goalDate, routinePostParticipationReq.weekBit.toInt(2))

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patch(routineId: Int, description: String, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val token = authorization.split(" ")[1]
        val userId = jwtTokenProvider.getUserId(token)

        val routine = routineRepository.findById(routineId)

        if (routine.isEmpty)
            return ResponseEntity(HttpStatus.NOT_FOUND)

        if (routine.get().admin.kakaoId != userId)
            return ResponseEntity(HttpStatus.UNAUTHORIZED)

        routine.get().description = description
        routineRepository.save(routine.get())

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }
}
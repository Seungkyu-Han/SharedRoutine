package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.RoutineRepository
import CoBo.SharedRoutine.global.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class RoutineServiceImpl(
    private val userRepository: UserRepository,
    private val routineRepository: RoutineRepository
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
}
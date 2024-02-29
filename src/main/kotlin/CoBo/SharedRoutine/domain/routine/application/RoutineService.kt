package CoBo.SharedRoutine.domain.routine.application

import CoBo.SharedRoutine.domain.routine.data.dto.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.data.dto.RoutinePostReq
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import org.springframework.http.ResponseEntity

interface RoutineService {

    fun post(routinePostReq: RoutinePostReq, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun postParticipation(routinePostParticipationReq: RoutinePostParticipationReq, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun patch(routineId: Int, description: String, authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
}
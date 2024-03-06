package CoBo.SharedRoutine.domain.routine.application

import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetParticipationElementRes
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import java.time.LocalDate

interface RoutineService {
    fun post(routinePostReq: RoutinePostReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun postParticipation(routinePostParticipationReq: RoutinePostParticipationReq, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun patch(routineId: Int, description: String, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun patchParticipation(routineId: Int, goalDate: LocalDate, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun postCheck(routineId: Int, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>
    fun getParticipation(authentication: Authentication): ResponseEntity<CoBoResponseDto<ArrayList<RoutineGetParticipationElementRes>>>
}
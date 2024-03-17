package CoBo.SharedRoutine.domain.routine.application.Impl

import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Req.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetMemberElementRes
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetParticipationElementRes
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetRankAndSearchElementRes
import CoBo.SharedRoutine.domain.routine.Data.Dto.Res.RoutineGetRes
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
import java.util.concurrent.CompletableFuture
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
            description = routinePostReq.description,
            memberCount = 1
        )

        routineRepository.save(routine)

        participationRepository.save(Participation(
            id = null,
            user = user,
            routine = routine,
            startDate = LocalDate.now(),
            goalDate = routinePostReq.goalDate,
            lastCheckDate = null,
            checkCount = 0,
            week = routinePostReq.weekBit.toInt(2)
        ))

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

        routine.memberCount += 1
        routineRepository.save(routine)

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
            routineGetParticipationElementResList.add(
                RoutineGetParticipationElementRes(
                    routineId = participation.routine.id,
                    title = participation.routine.title,
                    achievementRate = calculateAchievementRate(participation),
                    checked = participation.lastCheckDate == LocalDate.now(),
                    weekBit = participation.week.toString(2).padStart(7, '0')
                ))
        }

        return CoBoResponse(routineGetParticipationElementResList, CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun get(routineId: Int, authentication: Authentication): ResponseEntity<CoBoResponseDto<RoutineGetRes>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        val memberList = ArrayList<RoutineGetMemberElementRes>()

        for (participation in participationRepository.findAllByRoutine(routine))
            memberList.add(
                RoutineGetMemberElementRes(
                    id = participation.user.kakaoId,
                    name = participation.user.name,
                    image = participation.user.image,
                    achievementRate = calculateAchievementRate(participation)
                ))

        return CoBoResponse(RoutineGetRes(
            title = routine.title,
            description = routine.description,
            joined = participationRepository.existsByUserAndRoutine(user, routine),
            memberList = memberList
        ), CoBoResponseStatus.SUCCESS).getResponseEntity()
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

    override fun getRank(): ResponseEntity<CoBoResponseDto<ArrayList<RoutineGetRankAndSearchElementRes>>> {
        val routineGetRankElementResList = ArrayList<RoutineGetRankAndSearchElementRes>()

        for (routine in routineRepository.findTopTen())
            routineGetRankElementResList.add(
                RoutineGetRankAndSearchElementRes(
                routineId = routine.id,
                title = routine.title,
                memberCount = routine.memberCount
            ))

        return CoBoResponse(routineGetRankElementResList, CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun patchAdmin(routineId: Int, newAdminId: Int, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {

        val userCompletableFuture = CompletableFuture.supplyAsync{
            userRepository.findById(authentication.name.toInt())
                .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}
        }

        val newAdminFuture = CompletableFuture.supplyAsync {
            userRepository.findById(newAdminId)
                .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}
        }

        val routineFuture = CompletableFuture.supplyAsync{
            routineRepository.findById(routineId)
                .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}
        }

        val isParticipationFuture = CompletableFuture.supplyAsync{
            participationRepository.existsByUserIdAndRoutineIdByQueryDsl(newAdminId, routineId)
        }

        return CompletableFuture.allOf(userCompletableFuture, newAdminFuture, routineFuture, isParticipationFuture)
            .thenApplyAsync {

                val user = userCompletableFuture.get()
                val newAdmin = newAdminFuture.get()
                val routine = routineFuture.get()
                val isParticipation = isParticipationFuture.get()

                if (routine.admin != user)
                    throw IllegalAccessException("수정 권한이 없습니다.")

                if (!isParticipation)
                    throw NoSuchElementException("참여 정보가 없는 사용자입니다.")

                routine.admin = newAdmin
                routineRepository.save(routine)

                CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
            }.get()
    }

    override fun getSearch(keyword: String): ResponseEntity<CoBoResponseDto<ArrayList<RoutineGetRankAndSearchElementRes>>> {
        val routineGetSearchElementResList =  ArrayList<RoutineGetRankAndSearchElementRes>()

        for (routine in routineRepository.findAllByTitleContains(keyword))
            routineGetSearchElementResList.add(
                RoutineGetRankAndSearchElementRes(
                    routineId = routine.id,
                    title = routine.title,
                    memberCount = routine.memberCount
                ))

        return CoBoResponse(routineGetSearchElementResList, CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun deleteParticipation(routineId: Int, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        val routine = routineRepository.findById(routineId)
            .orElseThrow{throw NoSuchElementException("일치하는 루틴이 없습니다.")}

        val participation = participationRepository.findByUserAndRoutine(user, routine)
            .orElseThrow{throw NoSuchElementException("일치하는 참여 정보가 없습니다.")}

        participationRepository.delete(participation)

        routine.memberCount -= 1
        routineRepository.save(routine)

        if (routine.memberCount == 0) // 루틴 참여 멤버가 없을 시 루틴 삭제
            routineRepository.delete(routine)
        else if (routine.admin == user) // 방장이 퇴장할 시 랜덤으로 방장 위임
            randomAdmin(routine)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    private fun randomAdmin(routine: Routine) {
        val newAdmin = participationRepository.findAllByRoutine(routine).random().user
        routine.admin = newAdmin
        routineRepository.save(routine)
    }
}
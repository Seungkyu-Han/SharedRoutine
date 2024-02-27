package CoBo.SharedRoutine.global.data.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import java.time.LocalDate

@Entity
data class Participation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,
    @ManyToOne
    var user: User,
    @ManyToOne
    var routine: Routine,
    var startDate: LocalDate,
    var goalDate: LocalDate,
    var lastCheckDate: LocalDate?,
    var checkCount: Int,
    var week: Int
)

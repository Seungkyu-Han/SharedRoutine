package CoBo.SharedRoutine.global.data.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where

@Entity
@SQLDelete(sql = "UPDATE routine SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
data class Routine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int?,
    @ManyToOne
    var admin: User,
    var title: String,
    var description: String,
    var memberCount: Int,
    var deleted: Boolean = false
)

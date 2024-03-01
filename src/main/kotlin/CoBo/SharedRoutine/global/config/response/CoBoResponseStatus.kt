package CoBo.SharedRoutine.global.config.response

enum class CoBoResponseStatus(
    val code: Int,
    val message: String
) {

    SUCCESS(2000, "성공"),
    NOT_FOUND_ELEMENT(4000, "일치하는 요소를 찾을 수 없습니다."),
    CONFLICT(4090, "기존 정보와 충돌합니다.")
}
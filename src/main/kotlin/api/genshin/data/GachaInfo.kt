package icu.dnddl.plugin.genshin.api.genshin.data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GachaInfo(
    @SerialName("begin_time")
    val originBeginTime: String,
    @SerialName("end_time")
    val originEndTime: String,
    @SerialName("gacha_id")
    val gachaId: String,
    @SerialName("gacha_name")
    val gachaName: String,
    @SerialName("gacha_type")
    val gachaType: Int,
) {
    val beginTime get() = originBeginTime.parseToLocalDateTime()
    val endTime get() = originEndTime.parseToLocalDateTime()

    private fun String.parseToLocalDateTime(): LocalDateTime {
        val datePart = split(" ")

        require(datePart.size == 2) { "Unexpected date: $this" }
        val date = datePart[0].split("-")
        val time = datePart[1].split(":")
        return LocalDateTime(
            date[0].toInt(),
            date[1].toInt(),
            date[2].toInt(),
            time[0].toInt(),
            time[1].toInt(),
            time[2].toInt(),
        )
    }
}
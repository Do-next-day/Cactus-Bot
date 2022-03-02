package org.laolittle.plugin.genshin.api.genshin.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyNote(
    /**
     * 当前树脂
     */
    @SerialName("current_resin") val currentResin: Short,


    /**
     * 最大树脂
     */
    @SerialName("max_resin") val maxResin: Short,


    /**
     * 树脂恢复完毕时间
     */
    @SerialName("resin_recovery_time") val resinRecoveryTime: Long,
    @SerialName("finished_task_num") val finishedTask: Short,
    @SerialName("total_task_num") val totalTask: Short,
    @SerialName("is_extra_task_reward_received") val hasExtraTask: Boolean,
    @SerialName("remain_resin_discount_num") val resinDiscountRemain: Short,
    @SerialName("resin_discount_num_limit") val resinDiscountLimit: Short,
    @SerialName("current_expedition_num") val currentExpedition: Short,
    @SerialName("max_expedition_num") val maxExpedition: Short,
    @SerialName("expeditions") val expeditions: List<AvatarExploreStatus>,
    @SerialName("current_home_coin") val currentHomeCoin: Int,
    @SerialName("max_home_coin") val maxHomeCoin: Int,
    @SerialName("home_coin_recovery_time") val homeCoinRecoveryTime: Long,
    @SerialName("calendar_url") val calendarUrl: String,
) {
    @Serializable
    data class AvatarExploreStatus(
        @SerialName("avatar_side_icon") val avatarIconUrl: String,
        val status: Status,
        @SerialName("remained_time") val remaining: Long
    ) {
        enum class Status {
            /**
             * 已完成
             */
            Finished,

            /**
             * 派遣中
             */
            Ongoing
        }
    }
}
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

    /**
     * 每日委托完成数量
     */
    @SerialName("finished_task_num") val finishedTask: Short,

    /**
     * 每日委托总数
     */
    @SerialName("total_task_num") val totalTask: Short,

    /**
     * ??
     */
    @SerialName("is_extra_task_reward_received") val hasExtraTask: Boolean,

    /**
     * 周本减半剩余次数
     */
    @SerialName("remain_resin_discount_num") val resinDiscountRemain: Short,

    /**
     * 周本减半最大次数
     */
    @SerialName("resin_discount_num_limit") val resinDiscountLimit: Short,

    /**
     * 目前派遣数量
     */
    @SerialName("current_expedition_num") val currentExpedition: Short,

    /**
     * 最大派遣数量
     */
    @SerialName("max_expedition_num") val maxExpedition: Short,

    /**
     * 派遣角色具体状态
     */
    @SerialName("expeditions") val expeditions: List<AvatarExploreStatus>,

    /**
     * 家园币
     */
    @SerialName("current_home_coin") val currentHomeCoin: Int,

    /**
     * 最大家园币
     */
    @SerialName("max_home_coin") val maxHomeCoin: Int,

    /**
     * 家园币剩余恢复时间
     */
    @SerialName("home_coin_recovery_time") val homeCoinRecoveryTime: Long,

    /**
     * 日历URL (可能为Empty)
     */
    @SerialName("calendar_url") val calendarUrl: String,
) {
    /**
     * 角色派遣状态
     */
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
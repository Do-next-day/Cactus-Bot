package icu.dnddl.plugin.genshin.api.bilibili

import icu.dnddl.plugin.genshin.api.bilibili.data.DynamicList
import icu.dnddl.plugin.genshin.api.internal.biliGet
import icu.dnddl.plugin.genshin.util.decode

object BiliBiliApi {
    private const val BILIBILI_API = "https://api.bilibili.com"
    private const val BILIBILI_VC_API = "https://api.vc.bilibili.com"

    private const val DYNAMIC_ACTION = "/dynamic_svr/v1/dynamic_svr"

    /**
     * host_uid  查询UID
     * offset_dynamic_id  动态偏移量(无偏为0)
     */
    private const val HISTORY_DYNAMIC_LIST = "$BILIBILI_VC_API$DYNAMIC_ACTION/space_history?visitor_uid=0&platform=web"


    suspend fun getNewDynamic(biliUid: Long): DynamicList {
        return getHistoryDynamic(biliUid, 0)
    }

    suspend fun getHistoryDynamic(biliUid: Long, offsetDid: Long): DynamicList {
        val response = biliGet("$HISTORY_DYNAMIC_LIST&offset_dynamic_id=$offsetDid&host_uid=$biliUid")
        return response.data!!.decode()
    }


}
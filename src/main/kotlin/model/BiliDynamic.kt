package icu.dnddl.plugin.genshin.model

import icu.dnddl.plugin.genshin.api.bilibili.data.*
import icu.dnddl.plugin.genshin.util.decode

//fun String.dynamicContent(type: Int){
//    return when (type) {
//        DynamicType.REPLY -> decode<DynamicReply>()
//        DynamicType.PICTURE -> decode<DynamicPicture>()
//        DynamicType.TEXT -> decode<DynamicText>()
//        DynamicType.VIDEO -> decode<DynamicVideo>()
//        DynamicType.ARTICLE -> decode<DynamicArticle>()
//        DynamicType.MUSIC -> decode<DynamicMusic>()
//        DynamicType.EPISODE -> decode<DynamicEpisode>()
//        DynamicType.SKETCH -> decode<DynamicSketch>()
//        DynamicType.LIVE, DynamicType.LIVE_ING -> decode<DynamicLive>()
//        else -> DynamicNull()
//    }
//}
package icu.dnddl.plugin.genshin.api

class UrlParameters internal constructor(private val map: Map<String, String>) :
    Map<String, String> by map {
    override fun toString(): String =
        buildString {
            var paramSize = 0
            this@UrlParameters.forEach { (k, v) ->
                paramSize++
                append("$k=$v")
                if (paramSize != this@UrlParameters.size) append('&')
            }
        }
}

class UrlParametersBuilder private constructor(private val map: MutableMap<String, String>) :
    MutableMap<String, String> by map {
    constructor() : this(mutableMapOf())

    fun build() = UrlParameters(map)

    infix fun String.sets(value: Any) = set(this, value.toString())
}

inline fun buildUrlParameters(block: UrlParametersBuilder.() -> Unit) = UrlParametersBuilder().apply(block).build()
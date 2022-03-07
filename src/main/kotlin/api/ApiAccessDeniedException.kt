package icu.dnddl.plugin.genshin.api

class ApiAccessDeniedException
    (
    override val message: String? = null,
    override val cause: Throwable? = null,
    val code: Int
) : Exception()
package org.laolittle.plugin.genshin.api

class ApiFailedAccessException(override val message: String? = null, override val cause: Throwable? = null) :
    Exception(message, cause)
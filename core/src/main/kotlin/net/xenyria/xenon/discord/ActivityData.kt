package net.xenyria.xenon.discord

@JvmRecord
data class ActivityData(
    val state: String? = null,
    val details: String? = null,
    val start: Long? = null,
    val remaining: Int? = null
)
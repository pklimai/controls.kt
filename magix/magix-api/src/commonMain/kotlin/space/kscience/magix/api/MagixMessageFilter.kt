package space.kscience.magix.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.serialization.Serializable

@Serializable
public data class MagixMessageFilter(
    val format: Collection<String?>? = null,
    val origin: Collection<String?>? = null,
    val target: Collection<String?>? = null,
) {

    public fun accepts(message: MagixMessage): Boolean =
        format?.contains(message.format) ?: true
                && origin?.contains(message.origin) ?: true
                && target?.contains(message.target) ?: true

    public companion object {
        public val ALL: MagixMessageFilter = MagixMessageFilter()
    }
}

/**
 * Filter a [Flow] of messages based on given filter
 */
public fun Flow<MagixMessage>.filter(filter: MagixMessageFilter): Flow<MagixMessage> {
    if (filter == MagixMessageFilter.ALL) {
        return this
    }
    return filter(filter::accepts)
}
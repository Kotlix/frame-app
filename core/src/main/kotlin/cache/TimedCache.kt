package cache

class TimedCache<K, V>(
    private val ttlMillis: Long
) {
    private data class CacheEntry<V>(val value: V, val timestamp: Long)

    private val cache = mutableMapOf<K, CacheEntry<V>>()

    fun get(key: K): V? {
        val now = System.currentTimeMillis()
        val entry = cache[key]

        return if (entry != null && now - entry.timestamp < ttlMillis) {
            entry.value
        } else {
            invalidate(key)
            return null
        }
    }

    fun set(key: K, value: V) {
        val now = System.currentTimeMillis()
        cache[key] = CacheEntry(value, now)
    }

    private fun invalidate(key: K) {
        cache.remove(key)
    }

    fun clear() {
        cache.clear()
    }
}
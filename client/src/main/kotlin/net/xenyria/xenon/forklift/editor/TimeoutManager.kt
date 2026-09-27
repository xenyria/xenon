package net.xenyria.xenon.forklift.editor

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Manages timeouts of requests.
 * Requests are identified by a unique string ID.
 * If no call to [removeTimeout] is made in time, the provided "onExpire" callback is called.
 */
class TimeoutManager {

    private data class Request(
        val id: String,
        val onExpire: () -> Unit,
        val requestTime: Long = System.currentTimeMillis()
    )

    private val requests: MutableList<Request> = ArrayList()
    private val requestLock = ReentrantLock()

    fun removeTimeout(id: String) {
        requestLock.withLock {
            val iterator = requests.iterator()
            while (iterator.hasNext()) {
                val request = iterator.next()
                if (request.id == id) iterator.remove()
            }
        }
    }

    /**
     * Adds a timeout to the timeout manager.
     * If a timeout with the same ID already exists, false is returned.
     */
    fun addTimeout(
        id: String,
        onTimeout: () -> Unit
    ): Boolean {
        requestLock.withLock {
            if (requests.find { it.id == id } != null) return false
            val request = Request(id, onTimeout)
            requests.add(request)
            return true
        }
    }

    fun cancelAllRequests() {
        try {
            requestLock.lock()
            for ((_, callback) in requests) callback()
            requests.clear()
        } finally {
            requestLock.unlock()
        }
    }

    fun timeoutExpiredRequests() {
        requestLock.withLock {
            val iterator = requests.iterator()
            while (iterator.hasNext()) {
                val request = iterator.next()
                if (System.currentTimeMillis() - request.requestTime > 1000 * 5) {
                    request.onExpire()
                    iterator.remove()
                }
            }
        }
    }

}
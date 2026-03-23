package net.xenyria.xenon.forklift.editor

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TimeoutManager {

    data class Request(val id: String, val callback: () -> Unit, val requestTime: Long = System.currentTimeMillis())

    fun removeTimeout(id: String) {
        _requestLock.withLock {
            val iterator = _requests.iterator()
            while (iterator.hasNext()) {
                val request = iterator.next()
                if (request.id == id) {
                    iterator.remove()
                }
            }
        }
    }

    private val _requests: MutableList<Request> = ArrayList<Request>()
    private val _requestLock = ReentrantLock()

    fun addTimeout(
        id: String,
        onTimeout: () -> Unit
    ): Boolean {
        _requestLock.withLock {
            if (_requests.find { it.id == id } != null) return false
            val request = Request(id, onTimeout)
            _requests.add(request)
            return true
        }
    }

    fun cancelAllRequests() {
        try {
            _requestLock.lock()
            for (request in _requests) {
                request.callback()
            }
            _requests.clear()
        } finally {
            _requestLock.unlock()
        }
    }

    fun timeoutExpiredRequests() {
        _requestLock.withLock {
            val iterator = _requests.iterator()
            while (iterator.hasNext()) {
                val request = iterator.next()
                if (System.currentTimeMillis() - request.requestTime > 1000 * 5) {
                    request.callback()
                    iterator.remove()
                }
            }
        }
    }

}
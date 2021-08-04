package com.bda.omnilibrary.util

import android.os.Handler
import android.os.Looper
import java.util.*

class TickleManager private constructor() {
    private val mHandler = Handler(Looper.getMainLooper())
    private val mUpdateHandler = Runnable { updateTickle() }
    // Usually listener is a view. So use weak refs to not hold it forever.
    private val mListeners =
        Collections.newSetFromMap(WeakHashMap<TickleListener, Boolean>())
    private var mIsEnabled = true
    var intervalMs = DEFAULT_INTERVAL_MS

    fun addListener(listener: TickleListener?) {
        if (listener != null) {
            mListeners.add(listener)
            updateTickle()
        }
    }

    fun removeListener(listener: TickleListener?) {
        mListeners.remove(listener)
    }

    var isEnabled: Boolean
        get() = mIsEnabled
        set(enabled) {
            mIsEnabled = enabled
            updateTickle()
        }

    fun clear() {
        mListeners.clear()
        updateTickle()
    }

    fun runTask(task: Runnable?, delayMs: Long) {
        mHandler.removeCallbacks(task!!)
        mHandler.postDelayed(task!!, delayMs)
    }

    private fun updateTickle() {
        mHandler.removeCallbacks(mUpdateHandler)
        if (isEnabled && !mListeners.isEmpty()) {
            for (listener in mListeners) {
                listener.onTickle()
            }
            mHandler.postDelayed(mUpdateHandler, intervalMs)
        }
    }

    interface TickleListener {
        fun onTickle()
    }

    companion object {
        private var mTickleManager: TickleManager? = null
        private const val DEFAULT_INTERVAL_MS: Long = 10000
        fun instance(): TickleManager? {
            if (mTickleManager == null) {
                mTickleManager = TickleManager()
            }
            return mTickleManager
        }
    }
}

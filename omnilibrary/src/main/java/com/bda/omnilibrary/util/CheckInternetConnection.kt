package com.bda.omnilibrary.util

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress


class CheckInternetConnection {
    private var connectionChangeListener: ConnectionChangeListener? = null
    private var mHandlerThread: HandlerThread? = null
    private var mConnectionCheckerHandler: Handler? = null
    var updateInterval = 3000
    private var quite = false
    private fun initHandler() {
        quite = false
        mHandlerThread = HandlerThread("MyHandlerThread")
        mHandlerThread!!.priority = 3
        mHandlerThread!!.start()
        val looper = mHandlerThread!!.looper
        mConnectionCheckerHandler = Handler(looper)
    }

    private fun updateListenerInMainThread(connectionAvailability: Boolean) {
        Handler(Looper.getMainLooper()).post(Runnable {
            connectionChangeListener!!.onConnectionChanged(
                connectionAvailability
            )
        })
    }

    private fun sleep(timeInMilli: Int) {
        try {
            Thread.sleep(timeInMilli.toLong())
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    fun addConnectionChangeListener(connectionChangeListener: ConnectionChangeListener?) {
        this.connectionChangeListener = connectionChangeListener
        initHandler()
        mConnectionCheckerHandler!!.post(ConnectionCheckRunnable())
    }

    fun removeConnectionChangeListener() {
        quite = true
        mHandlerThread!!.quit()
    }

    internal inner class ConnectionCheckRunnable : Runnable {
        override fun run() {
            sleep(1000)
            while (!quite) {
                try {
                    val timeoutMs = 1500
                    val sock = Socket()
                    val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
                    sock.connect(sockaddr, timeoutMs)
                    sock.close()
                    updateListenerInMainThread(true)
                    sleep(updateInterval)
                } catch (e: IOException) {
                    updateListenerInMainThread(false)
                    sleep(updateInterval)
                }
            }
        }
    }
}

interface ConnectionChangeListener {
    fun onConnectionChanged(isConnectionAvailable: Boolean)
}
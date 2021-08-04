package com.bda.omnilibrary.util

import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TLSSocketFactory : SSLSocketFactory() {

    private var delegate: SSLSocketFactory

    init {
        val context: SSLContext = SSLContext.getInstance("TLS")
        context.init(null, null, null)
        delegate = context.socketFactory
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {

        val ss = delegate.createSocket(s!!, host!!, port, autoClose)

        return this.enableTLSOnSocket(ss)!!
    }

    override fun createSocket(host: String?, port: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port))!!
    }

    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int,
    ): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))!!
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket {
        return enableTLSOnSocket(delegate.createSocket(host, port))!!
    }

    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int,
    ): Socket {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))!!
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }


    private fun enableTLSOnSocket(socket: Socket?): Socket? {
        if (socket != null && socket is SSLSocket) {
            socket.enabledProtocols = arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
        }
        return socket
    }
}
package com.bda.omnilibrary.api


import android.util.Log
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Contract
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.TLSSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class APIGenerator(baseUrl: String) {
    private var logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
    private var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    private val F_LINK_SECURE = "st"
    private val F_EXPIRE_TIME = "e"
    private val S_CLIENT_ID = "client_id"
    private val S_TOKEN = "token"
    private val S_TIMESTAMP = "timestamp"
    private val S_CHECKSUM = "checksum"
    private var builder: Retrofit.Builder? = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>): S {
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        val expiredTime = Functions.expireTime(86400000) //24h
        httpClient.addNetworkInterceptor { chain ->
            var request = chain.request()
            if (request.url().toString().contains("fptplay.net", true)) {
                var httpUrl = request.url()
                val segmentsPath: String =
                    Functions.convertSegmentsToString(httpUrl.pathSegments())
                httpUrl = httpUrl.newBuilder()
                    .addQueryParameter(F_EXPIRE_TIME, expiredTime.toString())
                    .addQueryParameter(
                        F_LINK_SECURE,
                        Functions.secureLink(LibConfig.FPT_KEY, segmentsPath, expiredTime)
                    )
                    .build();
                val builder = request.newBuilder().url(httpUrl)
                builder.addHeader("Authorization", "Bearer " + Contract.token)
                builder.addHeader("Content-Type", "application/json")
                request = builder.build()
                chain.proceed(request)
            } else if (request.url().toString()
                    .contains(QuickstartPreferences.BASE_TTS_URL, true)
            ) {
                val builder = request.newBuilder()
                builder.addHeader("Authorization", "Bearer " + Contract.googleTTStoken)
                request = builder.build()
                chain.proceed(request)
            } else if (request.url().toString()
                    .contains("skysoundtrack", true)
            ) {
                var httpUrl = request.url()
                var timestamp = Functions.expireTime(0)
                httpUrl = httpUrl.newBuilder()
                    .addQueryParameter(S_CLIENT_ID, LibConfig.SKY_CLIENT_ID)
                    .addQueryParameter(
                        S_TOKEN,
                        LibConfig.SKY_TOKEN
                    )
                    .addQueryParameter(S_TIMESTAMP, timestamp.toString())
                    .addQueryParameter(
                        S_CHECKSUM,
                        Functions.checksum(
                            LibConfig.SKY_CLIENT_ID,
                            timestamp,
                            LibConfig.SKY_SECRET_KEY
                        )
                    )
                    .build()
                val builder = request.newBuilder().url(httpUrl)
                request = builder.build()
                chain.proceed(request)
            } else {
                val builder = request.newBuilder()
                var xApiKey = LibConfig.xApiKey
                if (request.url().toString()
                        .contains("tracking.shoppingtv.vn", true)
                ) {
                    xApiKey = LibConfig.xApiKey_tracking
                    if (Config.platform == "boxvnpt") {
                        xApiKey = LibConfig.xApiKey_tracking
                    }
                }
                if(Config.evironment.equals("development")){
                    builder.addHeader("is-dev", "yes")
                }
                builder.addHeader("x-api-key", xApiKey)
                builder.addHeader("customer_id", Config.uid)

                request = builder.build()
                chain.proceed(request)
            }
        }.interceptors().add(logging)
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

        httpClient.sslSocketFactory(TLSSocketFactory(), trustAllCerts[0] as X509TrustManager)
        builder!!.client(httpClient.build())
        val retrofit = builder!!.build()
        return retrofit.create(serviceClass)
    }

    fun getRetrofit(): Retrofit? {
        return if (builder == null) {
            null
        } else builder!!.build()
    }
}

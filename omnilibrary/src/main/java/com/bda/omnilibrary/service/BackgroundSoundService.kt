package com.bda.omnilibrary.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.bda.omnilibrary.model.SkyMusicResponse
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class BackgroundSoundService : Service() {
    private var player: MediaPlayer? = null
    private val myBinder = MyLocalBinder()
    private var mListMusic = ArrayList<SkyMusicResponse.Data>()
    private var currentPlayingPosition = 0
    private var volume: Float = 0f
    private val FADE_DURATION = 3000L
    private val FADE_INTERVAL = 250L
    private val MAX_VOLUME = 0.2f
    private val numberOfSteps = FADE_DURATION / FADE_INTERVAL
    private var doingJob = false  ///1 fadeout  ///2 fade in
    private val deltaVolume = MAX_VOLUME / numberOfSteps.toFloat()

    inner class MyLocalBinder : Binder() {
        fun getService(): BackgroundSoundService {
            return this@BackgroundSoundService
        }

    }

    override fun onBind(arg0: Intent): IBinder? {
        return myBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    fun setMusic(listMusic: ArrayList<SkyMusicResponse.Data>) {
        mListMusic = listMusic
        volume = 0f
        loadMusic()
    }

    fun loadMusic() {
        if (doingJob) {
            startFadeIn()
        } else {
            if (!checkPlaying()) {
                volume = 0f
                currentPlayingPosition = Random.nextInt(0, mListMusic.size)
                createMediaplayer(mListMusic[currentPlayingPosition].streamUrl)
            }
        }


    }

    private fun createMediaplayer(videoUri: String) {
        if (player != null) {
            player?.reset()
        } else {
            player = MediaPlayer()
            player?.setOnCompletionListener {
                loadMusic()
            }
            player?.setOnPreparedListener {
                start()
            }
        }

        Thread(Runnable {
            try {
                player?.setDataSource(videoUri)
                player?.prepareAsync()
            } catch (e: Exception) {
            }
        }).start()
    }

    private fun release() {
        try {
            player?.stop()
            player?.release()
        } catch (e: Exception) {
        }
        player = null
    }

    fun stop() {
        startFadeOut()
    }

    fun pauseToPlayVideo() {
        if (checkPlaying()) {
            player?.pause()
        }
    }

    fun start() {
        startFadeIn()
    }

    fun checkPlaying(): Boolean {
        if (player != null) {
            return player!!.isPlaying
        }
        return false

    }

    override fun onStart(intent: Intent, startId: Int) {
        // TO DO
    }


    override fun onDestroy() {
        release()
    }

    override fun onLowMemory() {

    }

    var timerFadeOut: Timer? = null
    private var timerTaskFadeOut: TimerTask? = null
    private fun startFadeOut() {
        timerFadeIn?.cancel()
        timerFadeIn?.purge()
        timerTaskFadeIn?.cancel()
        timerFadeOut?.cancel()
        timerFadeOut?.purge()
        timerTaskFadeOut?.cancel()
        timerFadeOut = Timer(true)
        doingJob = true
        timerTaskFadeOut = object : TimerTask() {
            override fun run() {
                fadeOutStep()
                if (volume <= 0f) {
                    timerFadeOut?.cancel()
                    timerFadeOut?.purge()
                    player?.pause()
                    doingJob = false
                }
            }
        }
        timerFadeOut?.schedule(timerTaskFadeOut, FADE_INTERVAL, FADE_INTERVAL)
    }

    private fun fadeOutStep() {
        if (player != null) {
            try {
                player?.setVolume(volume, volume)
                volume -= deltaVolume
            } catch (e: Exception) {

            }
        }
    }

    var timerFadeIn: Timer? = null
    private var timerTaskFadeIn: TimerTask? = null
    private fun startFadeIn() {
        doingJob = true
        player?.start()
        timerFadeOut?.cancel()
        timerFadeOut?.purge()
        timerTaskFadeOut?.cancel()

        timerFadeIn?.cancel()
        timerFadeIn?.purge()
        timerTaskFadeIn?.cancel()
        timerFadeIn = Timer(true)
        timerTaskFadeIn = object : TimerTask() {
            override fun run() {
                fadeInStep()
                if (volume > 0.2f) {
                    timerFadeIn?.cancel()
                    timerFadeIn?.purge()
                    doingJob = false
                }
            }
        }
        timerFadeIn?.schedule(timerTaskFadeIn, FADE_INTERVAL, FADE_INTERVAL)


    }

    private fun fadeInStep() {
        if (player != null) {
            try {
                player?.setVolume(volume, volume)
                volume += deltaVolume
            } catch (e: Exception) {

            }
        }
    }

}
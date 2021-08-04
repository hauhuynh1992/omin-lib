package com.bda.omnilibrary.ui.liveStreamActivity.playbackExo

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.view.SurfaceHolder
import androidx.leanback.media.PlaybackGlueHost
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.media.SurfaceHolderGlueHost
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util

class ExoPlayerAdapter(context: Context) : PlayerAdapter(), Player.EventListener {

    var mContext: Context = context
    var mPlayer: SimpleExoPlayer? = null
    var mSurfaceHolderGlueHost: SurfaceHolderGlueHost? = null
    val mRunnable: Runnable = object : Runnable {
        override fun run() {
            callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
            callback.onBufferedPositionChanged(this@ExoPlayerAdapter)
            mHandler.postDelayed(this, getUpdatePeriod().toLong())
        }
    }
    val mHandler = Handler()
    var mInitialized = false
    var mMediaSourceUri: Uri? = null
    var mHasDisplay = false
    var mBufferingStart = false

    @C.StreamType
    var mAudioStreamType = 0

    init {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
            mContext,
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
        mPlayer!!.addListener(this)
    }

    override fun onAttachedToHost(host: PlaybackGlueHost?) {
        if (host is SurfaceHolderGlueHost) {
            mSurfaceHolderGlueHost = host
            mSurfaceHolderGlueHost!!.setSurfaceHolderCallback(VideoPlayerSurfaceHolderCallback())
        }
    }

    fun reset() {
        changeToUninitialized()
        mPlayer!!.stop()
    }

    fun changeToUninitialized() {
        if (mInitialized) {
            mInitialized = false
            notifyBufferingStartEnd()
            if (mHasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    /**
     * Notify the state of buffering. For example, an app may enable/disable a loading figure
     * according to the state of buffering.
     */
    fun notifyBufferingStartEnd() {
        callback.onBufferingStateChanged(
            this@ExoPlayerAdapter,
            mBufferingStart || !mInitialized
        )
    }

    /**
     * Release internal [SimpleExoPlayer]. Should not use the object after call release().
     */
    fun release() {
        changeToUninitialized()
        mHasDisplay = false
        mPlayer!!.release()
    }

    override fun onDetachedFromHost() {
        if (mSurfaceHolderGlueHost != null) {
            mSurfaceHolderGlueHost!!.setSurfaceHolderCallback(null)
            mSurfaceHolderGlueHost = null
        }
        reset()
        release()
    }

    fun setDisplay(surfaceHolder: SurfaceHolder?) {
        val hadDisplay = mHasDisplay
        mHasDisplay = surfaceHolder != null
        if (hadDisplay == mHasDisplay) {
            return
        }
        mPlayer!!.setVideoSurfaceHolder(surfaceHolder)
        if (mHasDisplay) {
            if (mInitialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else {
            if (mInitialized) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        }
    }

    override fun setProgressUpdatingEnabled(enable: Boolean) {
        mHandler.removeCallbacks(mRunnable)
        if (!enable) {
            return
        }
        mHandler.postDelayed(mRunnable, getUpdatePeriod().toLong())
    }

    fun getUpdatePeriod(): Int {
        return 1
    }

    override fun isPlaying(): Boolean {
        val exoPlayerIsPlaying =
            (mPlayer!!.playbackState == ExoPlayer.STATE_READY
                    && mPlayer!!.playWhenReady)
        return mInitialized && exoPlayerIsPlaying
    }

    override fun getDuration(): Long = if (mInitialized) mPlayer!!.duration else -1

    override fun getCurrentPosition(): Long = if (mInitialized) mPlayer!!.currentPosition else -1

    override fun play() {
        if (!mInitialized || isPlaying) {
            return
        }

        mPlayer!!.playWhenReady = true
        callback.onPlayStateChanged(this@ExoPlayerAdapter)
        callback.onCurrentPositionChanged(this@ExoPlayerAdapter)
    }

    override fun pause() {
        if (isPlaying) {
            mPlayer!!.playWhenReady = false
            callback.onPlayStateChanged(this@ExoPlayerAdapter)
        }
    }

    override fun seekTo(positionInMs: Long) {
        if (!mInitialized) {
            return
        }
        mPlayer!!.seekTo(positionInMs)
    }

    override fun getBufferedPosition(): Long = mPlayer!!.bufferedPosition

    fun setDataSource(uri: Uri): Boolean {
        if (if (mMediaSourceUri != null) mMediaSourceUri == uri else uri == null) {
            return false
        }
        mMediaSourceUri = uri
        prepareMediaForPlaying()
        return true
    }

    fun getAudioStreamType(): Int {
        return mAudioStreamType
    }

    fun setAudioStreamType(@C.StreamType audioStreamType: Int) {
        mAudioStreamType = audioStreamType
    }

    fun onCreateMediaSource(uri: Uri?): MediaSource? {
        val userAgent =
            Util.getUserAgent(mContext, "omnishop")
         /*return ExtractorMediaSource(
             uri,
             DefaultDataSourceFactory(mContext, userAgent),
             DefaultExtractorsFactory(),
             null,
             null
         )*/

        return HlsMediaSource.Factory(DefaultHttpDataSourceFactory(userAgent))
            .createMediaSource(uri)
    }

    private fun prepareMediaForPlaying() {
        reset()
        if (mMediaSourceUri != null) {
            val mediaSource = onCreateMediaSource(mMediaSourceUri)
            mPlayer!!.prepare(mediaSource!!)
        } else {
            return
        }
        mPlayer!!.audioStreamType = mAudioStreamType
        mPlayer!!.setVideoListener(object : SimpleExoPlayer.VideoListener {
            override fun onVideoSizeChanged(
                width: Int, height: Int, unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
            ) {
                callback.onVideoSizeChanged(this@ExoPlayerAdapter, width, height)
            }

            override fun onRenderedFirstFrame() {}
        })
        notifyBufferingStartEnd()
        callback.onPlayStateChanged(this@ExoPlayerAdapter)
    }

    override fun isPrepared(): Boolean =
        mInitialized && (mSurfaceHolderGlueHost == null || mHasDisplay)

    inner class VideoPlayerSurfaceHolderCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
            setDisplay(surfaceHolder)
        }

        override fun surfaceChanged(
            surfaceHolder: SurfaceHolder,
            i: Int,
            i1: Int,
            i2: Int
        ) {
        }

        override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
            setDisplay(null)
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        mBufferingStart = false
        if (playbackState == ExoPlayer.STATE_READY && !mInitialized) {
            mInitialized = true
            if (mSurfaceHolderGlueHost == null || mHasDisplay) {
                callback.onPreparedStateChanged(this@ExoPlayerAdapter)
            }
        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            mBufferingStart = true
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            callback.onPlayStateChanged(this@ExoPlayerAdapter)
            callback.onPlayCompleted(this@ExoPlayerAdapter)
        }
        notifyBufferingStartEnd()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        callback.onError(
            this@ExoPlayerAdapter, error!!.type,
            "Error"
            /*mContext.getString(
                android.R.string.lb_media_player_error,
                error.type,
                error.rendererIndex
            )*/
        )
    }


    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray,
        trackSelections: TrackSelectionArray
    ) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }
}
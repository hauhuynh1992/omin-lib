    package com.bda.omnilibrary.ui.liveStreamActivity.playbackExo

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.leanback.app.PlaybackSupportFragment
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.PlaybackGlue
import com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.glue.VideoMediaPlayerGlue

class VideoConsumptionWithExoPlayerFragment : VideoSupportFragment() {
    private var URL: String? = null//"https://tvcommerce-st.fptplay.net/prod/videos/products/encoded/RealmeC20/index.m3u8"

    companion object {
        val TAG = "VideoConsumptionWithExoPlayer"

        @JvmStatic
        fun newInstance(url: String) =
            VideoConsumptionWithExoPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString("url", url)
                }
            }
    }

    private var mMediaPlayerGlue: VideoMediaPlayerGlue<ExoPlayerAdapter>? = null
    private var playerAdapter: ExoPlayerAdapter? = null
    val mHost = VideoSupportFragmentGlueHost(this)

    private fun playWhenReady(glue: PlaybackGlue) {
        if (glue.isPrepared) {
            glue.play()
        } else {
            glue.addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                override fun onPreparedStateChanged(glue: PlaybackGlue) {
                    if (glue.isPrepared) {
                        glue.removePlayerCallback(this)
                        glue.play()
                        //showControlsOverlay(false)
                    }
                }
            })
        }
    }

    var mOnAudioFocusChangeListener =
        OnAudioFocusChangeListener { }

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            URL = it.getString("url", "")
        }

        playerAdapter = ExoPlayerAdapter(activity!!)
        playerAdapter!!.setAudioStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE)
        mMediaPlayerGlue =
            VideoMediaPlayerGlue(
                activity!!,
                playerAdapter!!
            )
        mMediaPlayerGlue!!.host = mHost
        val audioManager = activity!!
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.requestAudioFocus(
                mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        ) {
            Log.w(
                "VideoConsumption",
                "video player cannot obtain audio focus!"
            )
        }

        mMediaPlayerGlue!!.playerAdapter
            .setDataSource(Uri.parse(URL))

        PlaybackSeekDiskDataProvider.setSeekProvider(mMediaPlayerGlue!!)
        playWhenReady(mMediaPlayerGlue!!)
        backgroundType = PlaybackSupportFragment.BG_NONE

        hideControlsOverlay(false)
        isControlsOverlayAutoHideEnabled = true
    }

    fun seekTo(ms: Long) {
        mMediaPlayerGlue?.seekTo(ms)
    }

    fun isPlaying() = mMediaPlayerGlue != null && mMediaPlayerGlue!!.isPlaying

    override fun onPause() {
        if (mMediaPlayerGlue != null) {
            mMediaPlayerGlue!!.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        if (mMediaPlayerGlue != null && !mMediaPlayerGlue!!.isPlaying)
            mMediaPlayerGlue!!.play()

        super.onResume()
    }

    override fun onDestroy() {
        mMediaPlayerGlue = null
        playerAdapter!!.release()

        super.onDestroy()
    }
}
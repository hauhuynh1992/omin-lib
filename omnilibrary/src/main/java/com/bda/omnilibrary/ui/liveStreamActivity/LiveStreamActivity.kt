package com.bda.omnilibrary.ui.liveStreamActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import com.bda.omnilibrary.R
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.VideoConsumptionWithExoPlayerFragment
import java.lang.ref.WeakReference

class LiveStreamActivity : BaseActivity() {
    private var url: String? = ""
    private val GAMEPAD_TRIGGER_INTENSITY_ON = 0.5f

    // Off-condition slightly smaller for button debouncing.
    private val GAMEPAD_TRIGGER_INTENSITY_OFF = 0.45f
    private var gamepadTriggerPressed = false

    //private var mPlaybackFragment: PlaybackFragment? = null
    private val mBackPressedMs: Long = 0
    private var mFinishCalledMs: Long = 0
    /*private val mViewManager: ViewManager? = null
    private val mMainUIData: MainUIData? = null*/

    companion object {
        private var weakActivity: WeakReference<LiveStreamActivity>? = null

        fun getActivity(): LiveStreamActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_stream)

        weakActivity = WeakReference(this@LiveStreamActivity)

        /*val fragment =
            supportFragmentManager.findFragmentByTag("playback_tag")
        if (fragment is PlaybackFragment) {
            mPlaybackFragment = fragment
        }*/

        val ft = supportFragmentManager.beginTransaction()
        ft.add(
            R.id.videoFragment, VideoConsumptionWithExoPlayerFragment(),
            "VideoConsumptionWithExoPlayer"
        )
        ft.commit()
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {


        return super.dispatchKeyEvent(event)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    /*@SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (mPlaybackFragment != null && event != null) {
            mPlaybackFragment!!.onDispatchKeyEvent(event)
        }

        return super.dispatchKeyEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (mPlaybackFragment != null) {
            mPlaybackFragment!!.onDispatchTouchEvent(ev!!)
        }

        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_R1) {
            mPlaybackFragment!!.skipToNext()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L1) {
            mPlaybackFragment!!.skipToPrevious()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_L2) {
            mPlaybackFragment!!.rewind()
            return true
        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_R2) {
            mPlaybackFragment!!.fastForward()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        // This method will handle gamepad events.

        // This method will handle gamepad events.
        if (event!!.getAxisValue(MotionEvent.AXIS_LTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
            && !gamepadTriggerPressed
        ) {
            mPlaybackFragment!!.rewind()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_RTRIGGER) > GAMEPAD_TRIGGER_INTENSITY_ON
            && !gamepadTriggerPressed
        ) {
            mPlaybackFragment!!.fastForward()
            gamepadTriggerPressed = true
        } else if (event.getAxisValue(MotionEvent.AXIS_LTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
            && event.getAxisValue(MotionEvent.AXIS_RTRIGGER) < GAMEPAD_TRIGGER_INTENSITY_OFF
        ) {
            gamepadTriggerPressed = false
        }

        return super.onGenericMotionEvent(event)
    }

    override fun finish() {
        mFinishCalledMs = System.currentTimeMillis()

        // NOTE: When exiting PIP mode onPause is called immediately after onResume

        // Also, avoid enter pip on stop!
        // More info: https://developer.android.com/guide/topics/ui/picture-in-picture#continuing_playback

        // NOTE: block back button for PIP.
        // User pressed PIP button in the player.

        // NOTE: When exiting PIP mode onPause is called immediately after onResume

        // Also, avoid enter pip on stop!
        // More info: https://developer.android.com/guide/topics/ui/picture-in-picture#continuing_playback

        // NOTE: block back button for PIP.
        // User pressed PIP button in the player.
        mPlaybackFragment!!.onFinish()
        super.finish()
    }

    @Suppress("DEPRECATION")
    override fun onVisibleBehindCanceled() {

        // App-specific method to stop playback and release resources
        mPlaybackFragment!!.onDestroy()
        super.onVisibleBehindCanceled()
    }*/
}

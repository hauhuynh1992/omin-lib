package com.bda.omnilibrary.custome

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.*
import android.view.TextureView.SurfaceTextureListener
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.bda.omnilibrary.R
import com.bda.omnilibrary.util.Functions


class VideoPlayer : RelativeLayout {
    // all possible internal states
    private val STATE_ERROR = -1
    private val STATE_IDLE = 0
    private val STATE_PREPARING = 1
    private val STATE_PREPARED = 2
    private val STATE_PLAYING = 3
    private val STATE_PLAYBACK_COMPLETED = 5

    private var mediaPlayer: MediaPlayer? = null
    private var progressBar: LinearLayout? = null
    private var textureView: TextureView? = null
    private var framelayout: FrameLayout? = null
    private var mSurface: Surface? = null
    private var mVideoTracker: VideoPlaybackTracker? = null
    private var loop = false
    private var stopSystemAudio = false
    private var muted = false
    private var fadeInTime = false
    private var speed = false
    private var scalefit = false
    private var showSpinner = false
    private var showProgress = false
    private var showTimeStick = false
    private var videoUri: String? = null

    private var progressHorizontal: RelativeLayout? = null
    private var progressHorizontalBar: ProgressBar? = null

    private var mCurrentState: Int = STATE_IDLE

    private var mCurrentBufferPercentage = 0

    private val mTimeStick: Runnable = object : Runnable {
        override fun run() {
            if (mVideoTracker != null) {
                mVideoTracker!!.onTimeStick(getCurrentPosition())
            }
            if (mediaPlayer!!.isPlaying) {
                setProgress()
                postDelayed(this, 1000)
            }
        }
    }

    private fun setProgress(): Int {
        if (mediaPlayer == null) {
            return 0
        }
        val position: Int = getCurrentPosition()
        val duration: Int = mediaPlayer!!.duration
        if (progressHorizontalBar != null) {
            if (duration > 0) {
                // use long to avoid overflow
                val pos = (1000 * position).toFloat() / duration
                progressHorizontalBar!!.progress = pos.toInt()

            }
        }
        return position
    }


    private fun getCurrentPosition(): Int {
        return if (isInPlaybackState()) {
            mediaPlayer!!.currentPosition
        } else 0
    }

    private fun isInPlaybackState(): Boolean {
        return mediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING
    }

    private val mBufferingUpdateListener =
        MediaPlayer.OnBufferingUpdateListener { mp, percent -> mCurrentBufferPercentage = percent }

    /**
     * Default constructor
     * @param context context for the activity
     */
    constructor(context: Context?) : super(context) {
        init()
    }

    /**
     * Constructor for XML layout
     * @param context activity context
     * @param attrs xml attributes
     */
    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SimpleVideoView, 0, 0)
        loop = a.getBoolean(R.styleable.SimpleVideoView_loop, false)
        stopSystemAudio = a.getBoolean(R.styleable.SimpleVideoView_stopSystemAudio, false)
        muted = a.getBoolean(R.styleable.SimpleVideoView_muted, false)
        showSpinner = a.getBoolean(R.styleable.SimpleVideoView_showSpinner, false)
        showProgress = a.getBoolean(R.styleable.SimpleVideoView_showProgress, false)
        showTimeStick = a.getBoolean(R.styleable.SimpleVideoView_showTimeStick, false)
        a.recycle()
        init()
    }

    /**
     * Initialize the layout for the SimpleVideoView.
     */
    private fun init() {
        mCurrentState = STATE_IDLE
        // add a progress spinner
        progressBar = LayoutInflater.from(context)
            .inflate(R.layout.progress_bar, this, false) as LinearLayout?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar!!.elevation = 6f
        }
        addView(progressBar)

        progressHorizontal = LayoutInflater.from(context)
            .inflate(R.layout.horizontal_progress_bar, this, false) as RelativeLayout?
        progressHorizontalBar = progressHorizontal!!.findViewById(R.id.progress_horizontal)

        progressHorizontalBar!!.max = 1000

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar!!.elevation = 6f
        }
        progressHorizontal!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        addView(progressHorizontal)
        gravity = Gravity.CENTER
    }

    /**
     * Add the SurfaceView to the layout.
     */
    private fun addSurfaceView() {
        // disable the spinner if we don't want it
        if (progressBar!!.visibility != View.GONE) {
            progressBar!!.visibility = View.GONE
        }

        if (progressHorizontal!!.visibility != View.GONE) {
            progressHorizontal!!.visibility = View.GONE
        }
        framelayout = LayoutInflater.from(context)
            .inflate(R.layout.textureview, this, false) as FrameLayout?
        textureView = framelayout!!.findViewById(R.id.tt_view)
        framelayout!!.alpha = 0f
        addView(framelayout, 0)
        textureView!!.surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                i: Int,
                i1: Int
            ) {
                mSurface = Surface(surfaceTexture)
                setMediaPlayerDataSource()
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                i: Int,
                i1: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                release()
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
        }
    }

    /**
     * Prepare to play the media.
     */
    private fun prepareMediaPlayer() {
        if (mediaPlayer != null) {
            release()
        }
        // initialize the media player
        mediaPlayer = MediaPlayer()

        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

        mediaPlayer!!.setOnBufferingUpdateListener(mBufferingUpdateListener)

        mCurrentBufferPercentage = 0

        mediaPlayer!!.setOnPreparedListener { mediaPlayer ->

            if (showProgress && progressHorizontal!!.visibility != View.VISIBLE) {
                progressHorizontal!!.visibility = View.VISIBLE

                mCurrentState = STATE_PREPARED
            }


            scalePlayer()
            if (stopSystemAudio) {
                val am =
                    context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                am.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
            if (muted) {
                mediaPlayer.setVolume(0f, 0f)
            }
            if (progressBar!!.visibility != View.GONE) {
                progressBar!!.visibility = View.GONE
            }
            try {
                mediaPlayer.setSurface(null)
                mediaPlayer.setSurface(mSurface)
                mediaPlayer.start()
                mVideoTracker?.onReady()
                mCurrentState = STATE_PLAYING

                if (showTimeStick) {
                    post(mTimeStick)
                }

                if (fadeInTime) {
                    Handler().postDelayed({
                        Functions.alphaAnimation(framelayout!!, 1f) {}
                    }, 500)
                } else {
                    Functions.alphaAnimation(framelayout!!, 1f) {}
                }

                if (speed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mediaPlayer!!.playbackParams = mediaPlayer.playbackParams.setSpeed(4.0f)
                    }
                }
            } catch (e: IllegalArgumentException) {
                // the surface has already been released
                mCurrentState = STATE_ERROR
            }

        }
        mediaPlayer!!.setOnCompletionListener { mp ->
            mCurrentState = STATE_PLAYBACK_COMPLETED

            if (loop) {
                mp.seekTo(0)
                mp.start()

                mCurrentState = STATE_PLAYING

                if (showProgress) {
                    progressHorizontalBar!!.progress = mp.currentPosition / 100
                }

            } else {
                mVideoTracker?.onCompleteVideo()
                Functions.alphaAnimation(framelayout!!, 0f) {}

                if (progressHorizontal!!.visibility != View.GONE) {
                    progressHorizontal!!.visibility = View.GONE
                }

                removeCallbacks(mTimeStick)
            }
        }
        mediaPlayer!!.setOnErrorListener { mp, what, extra ->
            mCurrentState = STATE_ERROR

            if (mVideoTracker != null) {
                mVideoTracker!!.onPlaybackError(
                    RuntimeException("Error playing video! what code: $what, extra code: $extra")
                )
            }
            true
        }
    }

    private fun setMediaPlayerDataSource() {
        Thread(Runnable {
            try {
                videoUri?.let {

                    mediaPlayer!!.setDataSource(videoUri!!)
                    mediaPlayer!!.prepareAsync()
                }
            } catch (e: Exception) {
                if (mVideoTracker != null) {
                    mVideoTracker!!.onPlaybackError(e)
                }
            }
        }).start()
    }

    /**
     * Adjust the size of the player so it fits on the screen.
     */
    private fun scalePlayer() {
        var videoWidth = mediaPlayer!!.videoWidth
        var videoHeight = mediaPlayer!!.videoHeight
        if (scalefit) {
            //xác định tỉ lệ 16:9
            if (videoHeight / (videoWidth / 16) == 9) {
                videoWidth = this.width
                videoHeight = this.height
            }
        }
        val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
        val screenProportion =
            width.toFloat() / height.toFloat()
        val lp = textureView!!.layoutParams
        if (videoProportion > screenProportion) {
            lp.width = width
            lp.height = (width.toFloat() / videoProportion).toInt()
        } else {
            lp.width = (videoProportion * height.toFloat()).toInt()
            lp.height = height
        }
        textureView!!.layoutParams = lp
    }

    fun start(videoUrl: String?) {
        mCurrentState = STATE_PREPARING
        this.videoUri = videoUrl
        if (textureView == null) {
            addSurfaceView()
            prepareMediaPlayer()
        } else {
            prepareMediaPlayer()
            Handler().postDelayed({
                setMediaPlayerDataSource()
            }, 100)

        }
    }

    /**
     * Start video playback. Called automatically with the SimpleVideoPlayer#start method
     */
    fun play() {
        if (mediaPlayer != null && !mediaPlayer!!.isPlaying) mediaPlayer!!.start()
    }

    fun setScaleFit(isScalefit: Boolean) {
        scalefit = isScalefit
    }

    /**
     * Pause video playback
     */
    fun pause() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
        }
    }

    /**
     * Release the video to stop playback immediately.
     *
     * Should be called when you are leaving the playback activity
     */
    fun release() {
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null

            removeCallbacks(mTimeStick)

            if (progressHorizontal!!.visibility != View.GONE) {
                progressHorizontal!!.visibility = View.GONE
            }

            if (!fadeInTime) {
                Functions.alphaAnimation(framelayout!!, 0f) {
                    removeViewAt(0)
                    textureView = null
                }
            } else {
                Functions.alphaAnimation(framelayout!!, 0f) {}
            }

            mCurrentState = STATE_IDLE

        } catch (e: Exception) {
        }

        mediaPlayer = null

    }

    /**
     * Whether you want the video to loop or not
     *
     */
    fun setFadeInTime(isFadeinTime: Boolean) {
        fadeInTime = isFadeinTime
    }

    fun setShouldLoop(shouldLoop: Boolean) {
        loop = shouldLoop
    }

    /**
     * Whether you want the app to stop the currently playing audio when you start the video
     *
     * @param stopSystemAudio
     */
    @Suppress("unused")
    fun setStopSystemAudio(stopSystemAudio: Boolean) {
        this.stopSystemAudio = stopSystemAudio
    }

    /**
     * Whether or not you want to show the spinner while loading the video
     *
     * @param showSpinner
     */
    fun setShowSpinner(showSpinner: Boolean) {
        this.showSpinner = showSpinner
        if (showSpinner) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    /**
     * Get whether or not the video is playing
     *
     * @return true if the video is playing, false otherwise
     */
    val isPlaying: Boolean
        get() = try {
            mediaPlayer != null && mediaPlayer!!.isPlaying
        } catch (e: Exception) {
            false
        }

    /**
     * Will return a result if there is an error playing the video
     *
     * @param tracker
     */
    fun setVideoTracker(tracker: VideoPlaybackTracker?) {
        mVideoTracker = tracker
    }

    fun seekTo(time: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaPlayer?.seekTo(time.toLong(), MediaPlayer.SEEK_CLOSEST)
        } else {
            mediaPlayer?.seekTo(time)
        }
    }

    fun setMute(boolean: Boolean) {
        muted = boolean
    }

    @Suppress("unused")
    fun setSpeed(boolean: Boolean) {
        speed = boolean
    }

    interface VideoPlaybackTracker {
        fun onPlaybackError(e: Exception?)
        fun onCompleteVideo()
        fun onTimeStick(time: Int)
        fun onReady()
    }
}
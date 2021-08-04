package com.bda.omnilibrary.ui.liveStreamActivity.playbackExo

import android.graphics.*
import androidx.leanback.media.PlaybackGlue
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackSeekDataProvider
import com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.glue.CustomPlaybackTransportControlGlue
import java.io.File

class PlaybackSeekDiskDataProvider internal constructor(
    duration: Long,
    interval: Long,
    val mPathPattern: String
) :
    PlaybackSeekAsyncDataProvider() {
    val mPaint: Paint

    override fun doInBackground(task: Any?, index: Int, position: Long): Bitmap? {
        try {
            Thread.sleep(100)
        } catch (ex: InterruptedException) {
            // Thread might be interrupted by cancel() call.
        }
        if (isCancelled(task!!)) {
            return null
        }
        val path = String.format(mPathPattern, index + 1)
        return if (File(path).exists()) {
            BitmapFactory.decodeFile(path)
        } else {
            val bmp = Bitmap.createBitmap(160, 160, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            canvas.drawColor(Color.YELLOW)
            canvas.drawText(path, 10f, 80f, mPaint)
            canvas.drawText(Integer.toString(index), 10f, 150f, mPaint)
            bmp
        }
    }

    companion object {
        fun setSeekProvider(glue: CustomPlaybackTransportControlGlue<*>) {
            if (glue.isPrepared) {
                glue.setSeekProvider(
                    PlaybackSeekDataProvider()
                    /*PlaybackSeekDiskDataProvider(
                        glue.duration,
                        glue.duration / 100,
                        "/sdcard/seek/frame_%04d.jpg"
                    )*/
                )
            } else {
                glue.addPlayerCallback(object : PlaybackGlue.PlayerCallback() {
                    override fun onPreparedStateChanged(glue: PlaybackGlue) {
                        if (glue.isPrepared) {
                            glue.removePlayerCallback(this)
                            val transportControlGlue =
                                glue as CustomPlaybackTransportControlGlue<*>
                            transportControlGlue.seekProvider = PlaybackSeekDataProvider()

                            /*PlaybackSeekDiskDataProvider(
                                transportControlGlue.duration,
                                transportControlGlue.duration / 100,
                                "/sdcard/seek/frame_%04d.jpg"
                            )*/
                        }
                    }
                })
            }
        }
    }

    init {
        val size = (duration / interval).toInt() + 1
        val pos = LongArray(size)
        for (i in pos.indices) {
            pos[i] = i * duration / pos.size
        }
        seekPositions = pos
        mPaint = Paint()
        mPaint.textSize = 16f
        mPaint.color = Color.BLUE
    }
}

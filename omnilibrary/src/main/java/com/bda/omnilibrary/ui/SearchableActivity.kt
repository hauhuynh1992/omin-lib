package com.bda.omnilibrary.ui

import android.os.Bundle
import android.util.Log
import androidx.core.content.IntentCompat.EXTRA_START_PLAYBACK
import com.bda.omnilibrary.ui.baseActivity.BaseActivity

class SearchableActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null && intent.data != null) {
            val uri = intent.data
            val id = Integer.valueOf(uri!!.lastPathSegment)
            val startPlayback = intent.getBooleanExtra(EXTRA_START_PLAYBACK, false)
            if (startPlayback) {
               // startActivity(PlaybackActivity.createIntent(this, id))
            } else {
//                val movie: Movie = MockDatabase.findMovieWithId(id)
//                startActivity(VideoDetailsActivity.createIntent(this, movie))
            }
        }
        finish()
    }
}

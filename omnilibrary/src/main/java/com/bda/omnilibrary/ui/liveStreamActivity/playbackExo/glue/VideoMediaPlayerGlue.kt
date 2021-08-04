package com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.glue

import android.app.Activity
import android.widget.Toast
import androidx.leanback.media.PlayerAdapter
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.PlaybackControlsRow

class VideoMediaPlayerGlue<T : PlayerAdapter>(context: Activity, implement: T) :
    CustomPlaybackTransportControlGlue<T>(context, implement) {

    //private val mPlayAction = PlaybackControlsRow.PlayPauseAction(context)

    private fun shouldDispatchAction(action: Action): Boolean {
        return false//action == mPlayAction
    }

    override fun onActionClicked(action: Action?) {

        if (shouldDispatchAction(action!!)) {
            dispatchAction(action)
            return
        }

        super.onActionClicked(action)
    }

    private fun dispatchAction(action: Action) {
        /*if (action === mPipAction) {
            (context as Activity).enterPictureInPictureMode()
        } else {*/
        Toast.makeText(context, action.toString(), Toast.LENGTH_SHORT).show()
        val multiAction =
            action as PlaybackControlsRow.MultiAction
        multiAction.nextIndex()
        notifyActionChanged(multiAction)
        //}
    }

    private fun notifyActionChanged(action: PlaybackControlsRow.MultiAction) {
        var index = -1
        if (getPrimaryActionsAdapter() != null) {
            index = getPrimaryActionsAdapter()!!.indexOf(action)
        }
        if (index >= 0) {
            getPrimaryActionsAdapter()?.notifyArrayItemRangeChanged(index, 1)
        } else {
            if (getSecondaryActionsAdapter() != null) {
                index = getSecondaryActionsAdapter()!!.indexOf(action)
                if (index >= 0) {
                    getSecondaryActionsAdapter()?.notifyArrayItemRangeChanged(index, 1)
                }
            }
        }
    }

    private fun getPrimaryActionsAdapter(): ArrayObjectAdapter? {
        return if (controlsRow == null) {
            null
        } else controlsRow.primaryActionsAdapter as ArrayObjectAdapter
    }

    private fun getSecondaryActionsAdapter(): ArrayObjectAdapter? {
        return if (controlsRow == null) {
            null
        } else controlsRow.secondaryActionsAdapter as ArrayObjectAdapter
    }

    /*var mHandler = Handler()

    override fun onPlayCompleted() {
        super.onPlayCompleted()
        mHandler.post {
            if (mRepeatAction.getIndex() != PlaybackControlsRow.RepeatAction.NONE) {
                play()
            }
        }
    }*/

    /*fun setMode(mode: Int) {
        mRepeatAction.setIndex(mode)
        if (getPrimaryActionsAdapter() == null) {
            return
        }
        notifyActionChanged(mRepeatAction)
    }*/

}
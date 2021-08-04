package com.bda.omnilibrary.ui.liveStreamActivity

import android.content.Intent
import android.os.Bundle
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson

class LiveStreamTransitionActivity : BaseActivity(), LiveStreamContract.View {
    private lateinit var presenter: LiveStreamContract.Presenter
    private var streamID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // todo change
        streamID = intent?.getStringExtra(QuickstartPreferences.LIVESTREAM)
        if (streamID == null) {
            finish()
        }

        presenter = LiveStreamPresenter(this, this)

        presenter.loadPresenter(streamID!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAPI()
    }

    override fun sendSuccess(liveStream: LiveStream) {
        val mIntent =
            if (liveStream.is_portrait) Intent(this, LiveStreamPortraitActivity::class.java)
            else Intent(this, LiveStreamLandscapeActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.LIVESTREAM, Gson().toJson(liveStream))
        startActivity(mIntent)
        finish()
    }

    override fun sendFalsed(error: String) {
        Functions.showMessage(this, error)
        finish()
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {
    }

    override fun sendAddressFailed(message: String) {
    }

    override fun finishThis(order_Id: String) {
    }

    override fun sendFailedOrder(message: String) {
    }

    override fun seekToProductTime(time: Int) {
    }
}
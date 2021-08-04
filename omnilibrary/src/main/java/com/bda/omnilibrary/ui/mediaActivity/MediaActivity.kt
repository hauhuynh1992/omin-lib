package com.bda.omnilibrary.ui.mediaActivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.get
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.DetailProductAdapter.DetailPresentAdapter
import com.bda.omnilibrary.adapter.checkout.ProductCheckoutAdapter
import com.bda.omnilibrary.custome.VideoPlayer
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.REQUEST_CUSTOMER_RESULT_CODE
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_media.*
import kotlinx.android.synthetic.main.activity_media.bn_confirm
import kotlinx.android.synthetic.main.activity_media.bn_delete_name
import kotlinx.android.synthetic.main.activity_media.bn_delete_phone
import kotlinx.android.synthetic.main.activity_media.bn_voice_name
import kotlinx.android.synthetic.main.activity_media.bn_voice_phone
import kotlinx.android.synthetic.main.activity_media.dim
import kotlinx.android.synthetic.main.activity_media.edt_name
import kotlinx.android.synthetic.main.activity_media.edt_phone__
import kotlinx.android.synthetic.main.activity_media.ln_product
import kotlinx.android.synthetic.main.activity_media.price
import kotlinx.android.synthetic.main.activity_media.rl_ship_time
import kotlinx.android.synthetic.main.activity_media.text_bn_confirm
import kotlinx.android.synthetic.main.activity_media.text_ship_time
import kotlinx.android.synthetic.main.activity_media.total_vat
import kotlinx.android.synthetic.main.activity_media.vg_product
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit


class MediaActivity : BaseActivity(), MediaContract.View {

    private var listVideo = ArrayList<Product.MediaType>()
    private var ssaiTimeout: Disposable? = null

    private var mProduct: Product? = null
    private var url: String? = ""
    private lateinit var detailPresentAdapter: DetailPresentAdapter

    private var checkForUserPauseAndSpeak = Handler()

    private var startPosition = 0

    private lateinit var mPresent: MediaContract.Presenter
    private var quickPayInfo: CheckCustomerResponse? = null

    private lateinit var qPadapter: ProductCheckoutAdapter
    private var voucherCode = ""
    private var voucherUid = ""
    private var voucherValue = 0.0

    companion object {
        private var weakActivity: WeakReference<MediaActivity>? = null
        fun getActivity(): MediaActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)

        weakActivity = WeakReference(this@MediaActivity)


        mProduct = Gson().fromJson(
            intent.getStringExtra(QuickstartPreferences.PRODUCT_MODEL),
            object : TypeToken<Product>() {}.type
        )

        mPresent = MediaPresenter(this, this)

        startPosition = intent.getIntExtra("POSITION", 0)

        val video = Functions.isVideoInProduct(mProduct!!)
        if (video != null) {
            url = Functions.getVideoUrl(video)
        }

        listVideo.addAll(mProduct!!.videos)
        listVideo.addAll(mProduct!!.images)

        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val layoutParams = layoutDetailCover.layoutParams
        layoutParams.height = width / 16 * 9
        layoutDetailCover.layoutParams = layoutParams
        rv_detail_present!!.isNestedScrollingEnabled = false
        detailPresentAdapter =
            DetailPresentAdapter(
                this,
                listVideo,
                mProduct!!.is_disabled_quickpay
            )
        timer(layout_contain_product_video)
        detailPresentAdapter.setOnItemFocusListener(object :
            DetailPresentAdapter.OnFocusChangeListener {
            override fun onItemFocus(position: Int, hasFocus: Boolean) {
                if (hasFocus) {

                    val params =
                        imgDetailCover.layoutParams
                    params.height = MATCH_PARENT
                    params.width = MATCH_PARENT
                    imgDetailCover.layoutParams = params
                    if (url != null && listVideo[position].mediaType == "video") {
                        ImageUtils.loadImage(
                            this@MediaActivity,
                            imgDetailCover!!, listVideo[position].icon,
                            ImageUtils.TYPE_PRIVIEW_LAGE
                        )
                        checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
                        checkForUserPauseAndSpeak.postDelayed({
                            videoDetailCover?.setShowSpinner(true)
                            videoDetailCover?.setFadeInTime(true)
                            videoDetailCover?.start(url)
                            videoDetailCover?.setVideoTracker(object :
                                VideoPlayer.VideoPlaybackTracker {
                                override fun onPlaybackError(e: Exception?) {

                                }

                                override fun onCompleteVideo() {
                                    rv_detail_present?.let {
                                        it.layoutManager?.scrollToPosition(
                                            position + 1
                                        )
                                    }
                                }

                                override fun onTimeStick(time: Int) {

                                }

                                override fun onReady() {

                                }

                            })
                        }, 1000)


                    } else {
//                        if (ssaiTimeout != null) {
//                            ssaiTimeout?.dispose()
//                        }

                        if (url != null && listVideo[position].mediaType == "video") {
                            checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
                            releaseVideo()
                        }
                        if (listVideo[position].square) {
                            val metrics = DisplayMetrics()
                            windowManager.defaultDisplay.getMetrics(metrics)
                            val height = metrics.heightPixels
                            val params1 =
                                imgDetailCover.layoutParams
                            params1.height = height
                            params1.width = height
                            imgDetailCover.layoutParams = params1
                        }
                        ImageUtils.loadImage(
                            this@MediaActivity,
                            imgDetailCover!!, listVideo[position].icon,
                            ImageUtils.TYPE_SHOW.takeIf { listVideo[position].square }
                                ?: ImageUtils.TYPE_PRIVIEW_LAGE
                        )
                    }
                } else {
//                    if (ssaiTimeout != null) {
//                        ssaiTimeout?.dispose()
//                    }
                    if (url != null && listVideo[position].mediaType == "video") {
                        checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
                        releaseVideo()
                    }
                }
            }

            override fun onItemVideoClick(position: Int, isPlayVideo: Boolean) {
                if (url != null && listVideo[position].mediaType == "video") {
                    if (videoDetailCover!!.isPlaying)
                        videoDetailCover!!.pause()
                    else {
                        videoDetailCover!!.play()
                    }
                }
            }

            override fun onClickBuyNow() {
                if (mProduct != null && !mProduct!!.is_disabled_cod) {
                    if (getCheckCustomerResponse() != null){
                        val dataObject = LogDataRequest()
                        dataObject.itemName = mProduct?.name.toString()
                        mProduct?.let {
                            if (it.collection.size >= 1) {
                                dataObject.itemCategoryId = it.collection[0].uid
                                dataObject.itemCategoryName = it.collection[0].collection_name
                            }
                        }
                        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                        dataObject.itemId = mProduct?.uid.toString()
                        dataObject.itemBrand = mProduct?.brand?.name
                        dataObject.itemListPriceVat = mProduct?.price.toString()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                                QuickstartPreferences.CLICK_QUICKPAY_BUTTON_v2,
                                data
                        )

                        //gotoQuickPayActivity(product, CLICK_QUICK_PAY_FROM_PRODUCT_DETAIL)
                        mPresent.fetchProfile(getCheckCustomerResponse()!!)
                    } else gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)


                } else {
                    Functions.showMessage(
                        this@MediaActivity,
                        String.format(
                            getString(R.string.only_online),
                            mProduct!!.display_name_detail
                        )
                    )
                }
            }

            override fun onClickBack() {
                val dataObject = LogDataRequest()
                dataObject.itemName = mProduct?.name
                mProduct?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.itemId = mProduct?.uid
                dataObject.itemBrand = mProduct?.brand?.name
                dataObject.itemListPriceVat = mProduct?.price.toString()
                dataObject.voucherId = voucherUid
                dataObject.voucherCode = voucherCode
                dataObject.voucherValue = voucherValue.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_QUICKPAY_CANCEL_v2,
                    data
                )
                finish()
            }
        })
        rv_detail_present!!.setItemViewCacheSize(10)
        rv_detail_present!!.hasFixedSize()
        rv_detail_present!!.adapter = detailPresentAdapter

        Handler().postDelayed({
            rv_detail_present!![startPosition].requestFocus()
        }, 200)
    }

    private fun timer(layout_contain_product_video: LinearLayout?) {
        if (ssaiTimeout != null) {
            ssaiTimeout?.dispose()
        }

        ssaiTimeout = Completable.timer(
            2,
            TimeUnit.SECONDS,
            AndroidSchedulers.mainThread()
        )
            .subscribe {
//                if (videoDetailCover != null && videoDetailCover!!.isPlaying) {
                //Functions.alphaAnimation(layout_contain_product_video!!, 0.3f) {}

                Functions.translateYAnimation(
                    layout_contain_product_video!!,
                    resources.getDimensionPixelSize(R.dimen._73sdp).toFloat()
                ) {
                    layout_contain_product_video!!.alpha = 0f
                }
//                }
            }
    }

    override fun onStop() {
        releaseVideo()
        super.onStop()
    }

    private fun releaseVideo() {
        videoDetailCover.pause()
        videoDetailCover.release()

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (ssaiTimeout != null) {
            ssaiTimeout?.dispose()
            layout_contain_product_video!!.alpha = 1f
            timer(layout_contain_product_video!!)
            Functions.translateYAnimation(
                layout_contain_product_video!!,
                0f
            ) {}

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {

        quickPayInfo = data

        initQuickPay(data)
    }

    private fun initQuickPay(data: CheckCustomerResponse) {
        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice_name.visibility = View.VISIBLE
            bn_voice_phone.visibility = View.VISIBLE
        } else {
            bn_voice_name.visibility = View.GONE
            bn_voice_phone.visibility = View.GONE
        }

        qPadapter = ProductCheckoutAdapter(this, arrayListOf(mProduct!!))
        vg_product.adapter = qPadapter

        loadData()

        edt_name.setText(data.data.alt_info[0].customer_name)
        edt_phone__.setText(data.data.alt_info[0].phone_number)

        if (mProduct!!.price >= 200000) {
            shipping_price.text = getString(R.string.text_mien_phi)
        } else {
            shipping_price.text = getString(R.string.text_xac_dinh_sau)
        }

        if (mProduct!!.supplier.shipping_time > 0) {
            rl_ship_time.visibility = View.VISIBLE
            text_ship_time.text =
                Functions.getShippingTimeBySupplier(mProduct!!.supplier.shipping_time)
        }

        // todo
        edt_name.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.QUICK_PAY.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                Gson().toJson(dataObject).toString()
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_NAME_TYPE,
                edt_name.text.toString(),
                edt_name, edt_phone__
            )
            keyboardDialog.show(supportFragmentManager, keyboardDialog.tag)
        }

        edt_name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edt_name.setSelection(edt_name.text.length)
            }
        }

        edt_name.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    if (bn_voice_name.visibility == View.VISIBLE) {
                        bn_voice_name.requestFocus()
                    } else {
                        bn_delete_name.requestFocus()
                    }
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone__.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.QUICK_PAY.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                Gson().toJson(dataObject).toString()
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone__.text.toString(),
                edt_phone__
            )
            keyboardDialog.show(supportFragmentManager, keyboardDialog.tag)
        }

        edt_phone__.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edt_phone__.setSelection(edt_phone__.text.length)
            }
        }

        edt_phone__.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    if (bn_voice_phone.visibility == View.VISIBLE) {
                        bn_voice_phone.requestFocus()
                    } else {
                        bn_delete_phone.requestFocus()
                    }
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        bn_delete_name.setOnClickListener {
            edt_name.setText("")

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
        }
        bn_delete_phone.setOnClickListener {
            edt_phone__.setText("")

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
        }

        bn_voice_name.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
            dataObject.inputType = "Voice"
            dataObject.inputName = "Name"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
            gotoDiscoveryVoice(DiscoveryVoiceActivity.REQUEST_VOICE_NAME_CODE)

        }

        bn_voice_phone.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
            dataObject.inputType = "Voice"
            dataObject.inputName = "Phone"
            logTrackingVersion2(
                QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
            gotoDiscoveryVoice(DiscoveryVoiceActivity.REQUEST_VOICE_PHONE_CODE)

        }

        bn_confirm.setOnFocusChangeListener { _, hasFocus ->
            text_bn_confirm.isSelected = hasFocus
        }

        bn_confirm.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.itemName = mProduct?.name
            mProduct?.let {
                if (it.collection.size >= 1) {
                    dataObject.itemCategoryId = it.collection[0].uid
                    dataObject.itemCategoryName = it.collection[0].collection_name
                }
            }
            dataObject.itemId = mProduct?.uid
            dataObject.itemBrand = mProduct?.brand?.name
            dataObject.itemListPriceVat = mProduct?.price.toString()
            dataObject.voucherId = voucherUid
            dataObject.voucherCode = voucherCode
            dataObject.voucherValue = voucherValue.toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_QUICKPAY_CONFIRM_v2,
                Gson().toJson(dataObject).toString()
            )
            if (edt_phone__.text.isNotEmpty()
            ) {
                quickPayInfo!!.data.alt_info[0].customer_name = edt_name.text.toString()
                quickPayInfo!!.data.alt_info[0].phone_number = edt_phone__.text.toString()

                if (quickPayInfo!!.data.alt_info[0].phone_number.length > 9) {
                    mPresent.updateOrder(
                        quickPayInfo!!,
                        this.mProduct!!,
                        voucherCode,
                        voucherUid
                    )

                } else {
                    Functions.showMessage(this, getString(R.string.wrong_phone_number))
                }
            } else {
                Functions.showMessage(this, getString(R.string.enter_your_phone_number))
            }
        }

        // Here
        bn_back_.setOnFocusChangeListener { _, hasFocus ->
            text_bn_back_.isSelected = hasFocus
        }

        bn_back_.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.itemName = mProduct?.name
            mProduct?.let {
                if (it.collection.size >= 1) {
                    dataObject.itemCategoryId = it.collection[0].uid
                    dataObject.itemCategoryName = it.collection[0].collection_name
                }
            }
            dataObject.itemId = mProduct?.uid
            dataObject.itemBrand = mProduct?.brand?.name
            dataObject.itemListPriceVat = mProduct?.price.toString()
            dataObject.voucherId = voucherUid
            dataObject.voucherCode = voucherCode
            dataObject.voucherValue = voucherValue.toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_QUICKPAY_CANCEL_v2,
                Gson().toJson(dataObject).toString()
            )
            hideQuickPay()
        }

        if (bn_voice_name.visibility == View.VISIBLE) {
            bn_confirm.nextFocusUpId = bn_voice_name.id

        } else {
            bn_confirm.nextFocusUpId = bn_delete_name.id

        }

        showQuickPay()
    }

    var total = 0.0
    private fun loadData() {
        total = 0.0

        total += mProduct!!.price
        price.text = Functions.formatMoney(total)
        loadMoney()

    }

    private fun loadMoney() {
        total_vat.text =
            Functions.formatMoney(total - voucherValue)

    }

    private var isShowQuickPay = false
    private fun showQuickPay() {
        isShowQuickPay = true

        //Functions.faceIn(dim)
        //Functions.bounceInLeft(ln_product)

        // Prepare the View for the animation
        ln_product.visibility = View.VISIBLE
        ln_product.alpha = 0.0f
        ln_product.translationX = ln_product.width.toFloat()

        dim.visibility = View.VISIBLE
        Functions.faceIn(dim)

        // Start the animation
        ln_product.animate()
            .translationX(0f)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    edt_phone__.requestFocus()
                }
            })
    }

    private fun hideQuickPay() {
        isShowQuickPay = false

        ln_product.animate().translationX(ln_product.width.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    ln_product.visibility = View.GONE

                    dim.visibility = View.GONE

                    detailPresentAdapter.bnBuyNowExport?.requestFocus()
                }
            })
    }

    override fun sendAddressFailed(message: String) {
    }

    override fun finishThis(order_Id: String) {
        gotoCheckoutStep3Activity(order_Id, 4, isClearCart = false, isShowStep = false)

    }

    override fun sendFailedOrder(message: String) {
        Functions.showMessage(this, message)

    }

    override fun onBackPressed() {
        if (isShowQuickPay) {
            hideQuickPay()

        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)

        if (result != null && result.isNotBlank()) {
            when (requestCode) {
                DiscoveryVoiceActivity.REQUEST_VOICE_PHONE_CODE -> {
                    if (Functions.checkPhoneNumber(result.trim().replace(" ", ""))) {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
                        dataObject.inputName = "Phone"
                        dataObject.inputType = "Voice"
                        dataObject.result = result.trim()
                        logTrackingVersion2(
                            QuickstartPreferences.VOICE_RESULT_v2,
                            Gson().toJson(dataObject).toString()
                        )
                        edt_phone__.setText(result.trim().replace(" ", ""))

                    } else {
                        Functions.showMessage(
                            this,
                            getString(R.string.text_error_phone)
                        )
                    }
                }

                DiscoveryVoiceActivity.REQUEST_VOICE_NAME_CODE -> {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.VIDEO_FULL_SCREEN.name
                    dataObject.inputName = "Name"
                    dataObject.inputType = "Voice"
                    dataObject.result = result.trim()
                    logTrackingVersion2(
                        QuickstartPreferences.VOICE_RESULT_v2,
                        Gson().toJson(dataObject).toString()
                    )
                    edt_name.setText(result.trim())
                }
            }
        }
    }
}

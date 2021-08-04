package com.bda.omnilibrary.dialog.assitant

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.SuggestAssistantAdapter
import com.bda.omnilibrary.adapter.SuggestProductAdapter
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.dialog.assitant.helper.SpeechService
import com.bda.omnilibrary.dialog.assitant.helper.VoiceRecorder
import com.bda.omnilibrary.dialog.assitant.helper.VoiceRecorder.Callback
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity
import com.bda.omnilibrary.util.Config
import com.google.gson.Gson
import kotlinx.android.synthetic.main.assistant_bottom_sheet.*
import java.io.IOException

class AssistantDialog : DialogFragment(), AssistantContract.View,
    MediaPlayer.OnCompletionListener,
    MediaPlayer.OnPreparedListener,
    MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnErrorListener {

    private lateinit var rvSuggest: RecyclerView
    private lateinit var rvResult: RecyclerView
    private lateinit var layout_result: LinearLayout
    private lateinit var lav_main: LottieAnimationView
    private lateinit var img_voice: ImageButton

    private var suggestAdapter: SuggestAssistantAdapter? = null
    private var resultAdapter: SuggestProductAdapter? = null
    private lateinit var presenter: AssistantContract.Presenter
    private var player: MediaPlayer? = null
    private var isShowResult: Boolean = false
    private lateinit var preferencesHelper: PreferencesHelper
    private var products: ArrayList<Product>? = null
    private var suggessions: ArrayList<String>? = null
    private lateinit var databaseHandler: DatabaseHandler

    private var mSpeechService: SpeechService? = null
    private var mVoiceRecorder: VoiceRecorder? = null
    private val mVoiceCallback: Callback = object : VoiceRecorder.Callback() {
        override fun onVoiceStart() {
            Log.d("AAA", "onVoiceStart")
            activity?.runOnUiThread {
                showVoiceAssistantStartListening()
            }
            if (mSpeechService != null) {
                mVoiceRecorder?.let {
                    mSpeechService?.startRecognizing(it.sampleRate)
                }
            }
        }

        override fun onVoice(data: ByteArray, size: Int) {
            if (mSpeechService != null) {
                mSpeechService?.recognize(data, size)
            }
        }

        override fun onVoiceEnd() {
            Log.d("AAA", "onVoiceEnd")
            stopVoiceRecorder()
            activity?.runOnUiThread {
                showVoiceAssistantPending()
                if (mSpeechService != null) {
                    mSpeechService?.finishRecognizing()
                }
            }
        }
    }
    private val mSpeechServiceListener = SpeechService.Listener { text, isFinal ->
        Log.d("AAA", "mSpeechServiceListener: " + text)
        activity?.runOnUiThread {
            showSpeechToTextResult(text)
        }
        if (isFinal) {
            mVoiceRecorder?.dismiss()
        }
        if (text != null) {
            activity?.runOnUiThread {
                if (isFinal) {
                    loadSearch(text, true)
                }
            }
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            binder: IBinder
        ) {
            Log.d("AAA", "onServiceConnected")
            mSpeechService = SpeechService.from(binder)
            mSpeechService?.addListener(mSpeechServiceListener)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d("AAA", "onServiceDisconnected")
            mSpeechService = null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.assistant_bottom_sheet, container).apply {
            this@AssistantDialog.activity?.let {
                preferencesHelper = PreferencesHelper(it)
                databaseHandler = DatabaseHandler(it)
                suggestAdapter = SuggestAssistantAdapter(it, arrayListOf()) { text ->
                    player?.pause()
                    presenter.getAssistant(text)
                }
                resultAdapter = SuggestProductAdapter(it, arrayListOf()) { product ->
                    gotoDetailProduct(product, false)
                }

                presenter = AssistantPresenter(this@AssistantDialog, it)
            }

            // Binding
            layout_result = findViewById(R.id.ll_result)
            lav_main = findViewById(R.id.lav_main)
            img_voice = findViewById(R.id.img_voice)

            img_voice?.setOnClickListener {
                if (LibConfig.APP_ORIGIN == Config.PARTNER.OMNISHOPEU.toString()) {
                    activity?.let {
                        if (ContextCompat.checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            (it as MainActivity)?.gotoDiscoveryVoice(DiscoveryVoiceActivity.REQUEST_VOICE_SEARCH_CODE)
                        } else {
                            ActivityCompat.requestPermissions(
                                it,
                                arrayOf(
                                    Manifest.permission.RECORD_AUDIO
                                ),
                                0
                            )
                        }
                    }
                } else {
                    showVoiceAssistantStartListening()
                    startVoiceRecorder()
                }
            }

            img_voice?.setOnFocusChangeListener { view, b ->
                if (b) {
                    tv_title?.visibility = View.VISIBLE
                    tv_title?.text = getString(R.string.text_nhan_ok_de_noi)//"Nhấn OK để nói"
                } else {
                    tv_title?.text = ""
                }
            }


            rvSuggest = findViewById<RecyclerView>(R.id.vp_suggest).apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = this@AssistantDialog.suggestAdapter
            }

            rvResult = findViewById<RecyclerView>(R.id.vp_result).apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = this@AssistantDialog.resultAdapter
            }

            Handler().postDelayed({
                rvSuggest.requestFocus()
            }, 100)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        activity?.let {
            (it as MainActivity).playMusicBackground()
        }
        presenter.disposeAPI()
        // Stop listening to voice
        stopVoiceRecorder()

        // Stop Cloud Speech API
        mSpeechService?.removeListener(mSpeechServiceListener)
        if (requireContext().isMyServiceRunning(SpeechService::class.java)) {
            activity?.unbindService(mServiceConnection)
        }
        mSpeechService = null
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {

            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(true)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.BOTTOM
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val lp = window.attributes
            dialog?.setOnKeyListener { dialog, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    Log.d("AAA-keycode", keyCode.toString())
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (rvSuggest.hasFocus() && suggessions != null) {
                            if (suggestAdapter!!.getCurrentPositionFocus() == suggessions!!.size - 1) {
                                Handler().postDelayed({
                                    rvSuggest.requestFocus(suggestAdapter!!.itemCount - 1)
                                }, 0)
                            }
                        }
                    }
                    if (keyCode == KeyEvent.KEYCODE_PROG_RED) {
                        startVoiceRecorder()
                    }
                    if (keyCode == KeyEvent.KEYCODE_F4) {
                        startVoiceRecorder()
                    }
                }
                false
            }
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog.window!!.attributes = lp
            isShowResult = false

            if (presenter.getDefaultLanguageCode().equals("vi-VN")) {
                presenter.getAssistant(getString(R.string.text_xin_chao)/*"Xin chào"*/)
            } else {
                presenter.getAssistant(getString(R.string.text_hello)/*"Xin chào"*/)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as MainActivity).pauseMusicToPlayVideo()
            it.bindService(
                Intent(requireContext(), SpeechService::class.java),
                mServiceConnection,
                BIND_AUTO_CREATE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            DiscoveryVoiceActivity.REQUEST_VOICE_SEARCH_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotBlank()) {
                    presenter.getAssistant(result)
                }
            }
        }
    }

    override fun sendFail(error_code: String, langCode: String) {
        loadAudio(error_code, langCode)
    }

    fun showAssistantSpeak(isShowResult: Boolean) {
        showVoiceAssistantSpeaking()
        products?.let {
            if (it.size == 0) {
                ll_result?.visibility = View.GONE
            } else {
                ll_result?.visibility = View.VISIBLE
                resultAdapter?.setData(it)
            }
        }

        suggessions?.let {
            suggestAdapter?.setData(suggessions!!)
        }
    }

    private fun loadSearch(text: String, isClickSearch: Boolean) {
        presenter.getAssistant(text)
        var dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.HOME.name
        dataObject.result = text
        val data = Gson().toJson(dataObject).toString()
        activity?.let {
            (it as BaseActivity)?.logTrackingVersion2(
                QuickstartPreferences.ASK_SHOPPING_ASSISTANT_v2,
                data
            )
        }
        ll_result?.visibility = View.GONE
    }

    override fun gotoDetailProduct(product: Product, isShowQuickPay: Boolean) {
        this@AssistantDialog.activity?.let {
            (it as MainActivity).gotoProductDetail(
                product,
                0,
                QuickstartPreferences.CLICK_PRODUCT_FROM_ASSISTANT
            )
        }
        dismiss()
    }

    private fun addToCart(product: Product) {
        this@AssistantDialog.activity?.let {
            val databaseHandler = DatabaseHandler(it)
            val dbproduct = databaseHandler.getExistItem(product.uid)
            product.order_quantity = 1
            if (dbproduct != null) {
                dbproduct.order_quantity += 1
                product.order_quantity = dbproduct.order_quantity
                databaseHandler.deleteItemCart(product.uid)
            }
            databaseHandler.insertProduct(product.uid, Gson().toJson(product))
        }

    }

    private fun createMediaPlayer() {
        player = MediaPlayer()
    }

    private fun releasePlayer() {
        if (player != null) {
            player?.stop()
            player?.release()
            player = null
        }
    }


    private fun loadAudio(audioUrl: String, langCode: String? = null) {
        if (player == null) createMediaPlayer()

        try {
            player!!.reset()
            if (!audioUrl.isNullOrBlank()) {
                activity?.let {
                    if (audioUrl.contains("error_code") || audioUrl.contains("ERROR_CODE")) {
                        if (langCode.equals("vi-VN")) {
                            var afd: AssetFileDescriptor =
                                it.assets.openFd("other_error_code_vi.mp3")
                            when (audioUrl) {
                                AssistantPresenter.OTHER_ERROR_CODE -> {
                                    afd = it.assets.openFd("other_error_code_vi.mp3")
                                }
                                AssistantPresenter.EMPTY_CART_ERROR_CODE -> {
                                    afd = it.assets.openFd("empty_cart_error_code_vi.mp3")
                                }
                                AssistantPresenter.EMPTY_DETAIL_PRODUCT_ERROR_CODE -> {
                                    afd = it.assets.openFd("empty_detail_product_error_code_vi.mp3")
                                }
                                AssistantPresenter.EMPTY_ADD_TO_CART_PRODUCT_ERROR_CODE -> {
                                    afd =
                                        it.assets.openFd("empty_add_to_cart_product_error_code_vi.mp3")
                                }
                                AssistantPresenter.MULTI_ADD_TO_CART_PRODUCT_ERROR_CODE -> {
                                    afd =
                                        it.assets.openFd("multi_add_to_cart_product_error_code_vi.mp3")
                                }
                                AssistantPresenter.MULTI_DETAIL_PRODUCT_ERROR_CODE -> {
                                    afd = it.assets.openFd("multi_detail_product_error_code_vi.mp3")
                                }
                                AssistantPresenter.ADD_TO_CART_CODE -> {
                                    afd = it.assets.openFd("add_to_cart_code_vi.mp3")
                                }
                            }
                            player!!.setDataSource(
                                afd.getFileDescriptor(),
                                afd.getStartOffset(),
                                afd.getLength()
                            )
                        } else {
                            var afd: AssetFileDescriptor =
                                it.assets.openFd("other_error_code_en.mp3")
                            when (audioUrl) {
                                AssistantPresenter.OTHER_ERROR_CODE -> {
                                    afd = it.assets.openFd("other_error_code_en.mp3")
                                }
                                AssistantPresenter.EMPTY_CART_ERROR_CODE -> {
                                    afd = it.assets.openFd("empty_cart_error_code_en.mp3")
                                }
                                AssistantPresenter.EMPTY_DETAIL_PRODUCT_ERROR_CODE -> {
                                    afd = it.assets.openFd("empty_detail_product_error_code_en.mp3")
                                }
                                AssistantPresenter.EMPTY_ADD_TO_CART_PRODUCT_ERROR_CODE -> {
                                    afd =
                                        it.assets.openFd("empty_add_to_cart_product_error_code_en.mp3")
                                }
                                AssistantPresenter.MULTI_ADD_TO_CART_PRODUCT_ERROR_CODE -> {
                                    afd =
                                        it.assets.openFd("multi_add_to_cart_product_error_code_en.mp3")
                                }
                                AssistantPresenter.MULTI_DETAIL_PRODUCT_ERROR_CODE -> {
                                    afd = it.assets.openFd("multi_detail_product_error_code_en.mp3")
                                }
                                AssistantPresenter.ADD_TO_CART_CODE -> {
                                    afd = it.assets.openFd("add_to_cart_code_en.mp3")
                                }
                            }
                            player!!.setDataSource(
                                afd.getFileDescriptor(),
                                afd.getStartOffset(),
                                afd.getLength()
                            )
                        }
                    } else {
                        showAssistantSpeak(true)
                        activity?.let {
                            val url = "data:audio/mp3;base64,$audioUrl"
                            player!!.setDataSource(url)
                        }
                    }
                }
            }
            player!!.setVolume(1f, 1f)
            player!!.setOnPreparedListener(this)
            player!!.setOnCompletionListener(this)
            player!!.setOnBufferingUpdateListener(this)
            player!!.setOnErrorListener(this)
            player!!.prepareAsync()
        } catch (e: IOException) {

        } catch (e: IllegalStateException) {

        }
    }

    private fun startVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder?.stop()
        }
        mVoiceRecorder = VoiceRecorder(mVoiceCallback)
        mVoiceRecorder?.start()
    }

    private fun stopVoiceRecorder() {
        if (mVoiceRecorder != null) {
            mVoiceRecorder?.stop()
            mVoiceRecorder = null
        }
    }

    override fun gotoCart() {
        (activity as MainActivity).gotoCart()
        dismiss()
    }

    override fun loadCart() {
        products?.let {
            if (it.size >= 1) {
                addToCart(products!!.get(0))
                (activity as MainActivity).loadCart()
                presenter.speechText(resources.getString(R.string.tv_product_add_to_cart))
            }
        }
    }

    override fun gotoInvoice() {
        (activity as BaseActivity)?.gotoInvoiceDetail(null, false, 0.0, "")
        dismiss()
    }

    override fun showResult(
        mProducts: ArrayList<Product>,
        mSuggession: ArrayList<String>,
        speechText: String,
        title: String
    ) {
        products = mProducts
        suggessions = mSuggession
        txt_topic?.text = title
        showAssistantSpeak(true)
        presenter.speechText(speechText)
    }

    override fun speakOut(url: String, langCode: String) {
        loadAudio(url, langCode)
    }

    override fun instant(): Activity? {
        return activity
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onCompletion(mp: MediaPlayer?) {
        rvSuggest?.visibility = View.VISIBLE
        showVoiceAssistantPending()
    }

    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
        if (percent == 100) {
            showAssistantSpeak(isShowResult)
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        player!!.start()
    }

    private fun showSpeechToTextResult(result: String) {
        tv_title?.visibility = View.VISIBLE
        activity?.runOnUiThread {
            tv_title?.text = result
        }
    }

    override fun showLoading() {
        tv_title?.visibility = View.VISIBLE
        vp_suggest?.visibility = View.GONE
        ll_result?.visibility = View.GONE
        tv_title?.text = getString(R.string.text_dang_xu_ly)//"Đang xử lý ..."
        lav_main?.visibility = View.VISIBLE
        img_voice?.visibility = View.GONE

        lav_main?.apply {
            pauseAnimation()
            setAnimation("ic_loading_spinner.json")
            setRepeatCount(LottieDrawable.INFINITE)
            playAnimation()
        }
    }

    private fun showVoiceAssistantSpeaking() {
        tv_title?.visibility = View.VISIBLE
        tv_title?.text = getString(R.string.text_vui_long_doi)//"Vui lòng đợi ..."
        lav_main?.visibility = View.VISIBLE
        img_voice?.visibility = View.GONE
        lav_main?.apply {
            pauseAnimation()
            setAnimation("ic_loading_spinner.json")
            setRepeatCount(LottieDrawable.INFINITE)
            playAnimation()
        }
    }

    private fun showVoiceAssistantStartListening() {
        tv_title?.visibility = View.VISIBLE
        tv_title?.text = getString(R.string.text_hay_thu_noi)//"Hãy thử nói ..."
        lav_main?.visibility = View.VISIBLE
        img_voice?.visibility = View.GONE
        lav_main?.apply {
            pauseAnimation()
            setAnimation("ic_assistant_anmiation.json")
            setRepeatCount(LottieDrawable.INFINITE)
            playAnimation()
        }
    }

    private fun showVoiceAssistantPending() {
        tv_title?.visibility = View.GONE
        img_voice?.visibility = View.VISIBLE
        lav_main?.visibility = View.GONE
        img_voice?.requestFocus()
        lav_main?.visibility = View.GONE
        lav_main?.apply {
            pauseAnimation()
        }
    }

    fun Context.isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE)
            .any {
                Log.d("AAA", it.service.className + "/" + serviceClass.name)
                it.service.className == serviceClass.name
            }
    }
}

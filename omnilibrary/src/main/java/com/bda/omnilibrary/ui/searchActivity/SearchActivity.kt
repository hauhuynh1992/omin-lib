package com.bda.omnilibrary.ui.searchActivity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.search.SearchAdapter
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.BrandShop
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_SEARCH_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search.*
import me.toptas.fancyshowcase.FancyShowCaseView
import java.lang.ref.WeakReference


class SearchActivity : BaseActivity(), SearchContact.View {

    private lateinit var presenter: SearchContact.Presenter
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var adapter: SearchAdapter
    private var isCallingApi = false

    companion object {
        private var weakActivity: WeakReference<SearchActivity>? = null
        fun getMaiActivity(): SearchActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.SEARCH.name
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_SEARCH_v2,
            data
        )
        presenter = SearchPresenter(this, this)
        weakActivity = WeakReference(this@SearchActivity)
        initial()
    }


    private fun initial() {
        preferencesHelper = PreferencesHelper(this)
        adapter = SearchAdapter(
            this, arrayListOf(
            )
        )
        val list = getDatabaseHandler()!!.getLProductList()
        adapter.count = list.size
        adapter.setOnCallbackListener(object : SearchAdapter.OnCallBackListener {
            override fun onClickVoice() {
                if (ContextCompat.checkSelfPermission(
                        this@SearchActivity,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.SEARCH.name
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_VOICE_SEARCH_BUTTON_v2,
                        data
                    )
                    gotoDiscoveryVoice(REQUEST_VOICE_SEARCH_CODE)
                } else {
                    ActivityCompat.requestPermissions(
                        this@SearchActivity,
                        arrayOf(
                            Manifest.permission.RECORD_AUDIO
                        ),
                        0
                    )
                }


            }

            override fun onClickSearch(query: String, isClickSearch: Boolean) {
                if (isClickSearch) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.SEARCH.name
                    dataObject.keyword = query
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_SUBMIT_KEYWORD_v2,
                        data
                    )

                    if (!isCallingApi)
                        presenter.callSearchProduct(query)

                    setCallingApiState()

                    adapter.setDataSuggestionProducts(arrayListOf())
                } else {

                    presenter.callSuggestKeywords(query)
                }

                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.SEARCH.name
                dataObject.keyword = query
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_KEYBOARD_SEARCH_v2,
                    data
                )
            }

            override fun onClickProduct(product: Product, position: Int) {
                gotoProductDetail(product, position, Config.SCREEN_ID.SEARCH.name)
            }

            override fun onClickStore(product: BrandShop, position: Int) {
                gotoBrandShopDetail(product.uid)
            }
        })

        if (GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS
        ) {
            adapter.isVoice = true
            Handler().postDelayed({
                adapter.myKeyboard?.requestFocus()

            }, 100)
        } else {
            adapter.isVoice = false
        }
        rv_search.adapter = adapter
        if (LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()) {
            Handler().postDelayed({
                showToolTip()
            }, 100)
        }
    }

    private fun loadSearch(text: String?) {
        text.let {
            if (it!!.isNotBlank()) {
                presenter.callSearchProduct(it)
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        when (event!!.keyCode) {
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (FancyShowCaseView.isVisible(this)) {
                        FancyShowCaseView.hideCurrent(this)
                        preferencesHelper.setShowCase(true)
                    }
                }
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (this::adapter.isInitialized && adapter.isSuggestionListFocus) {
                        Handler().postDelayed({
                            adapter.myKeyboard?.searchButton?.requestFocus()
                        }, 50)
                    }
                }
            }

        }
        return super.dispatchKeyEvent(event)
    }

    override fun sendFail(erroMessage: Int) {

    }

    override fun sendSuccess(list: ArrayList<Product>, stores: ArrayList<BrandShop>) {
        setFinishedCallApiState()
        hideQuickPay()

        adapter.setDataFoundProduct(list, stores)
        Handler().postDelayed({

            if (list.size > 0) {
                rv_search.smoothScrollToPosition(1)
            } else if (stores.size > 0) {
                rv_search.smoothScrollToPosition(2)
            }
        }, 150)
    }

    override fun sendKeywordSuggest(list: ArrayList<String>) {
        adapter.setSuggestKeywordData(list)
    }


    override fun sendHighlightFail(erroMessage: Int) {
        hideQuickPay()
    }

    override fun sendHightlightSuccess(list: ArrayList<Product>) {
        setFinishedCallApiState()
        hideQuickPay()
        adapter.setDataSuggestionProducts(list)
    }

    override fun onResume() {
        super.onResume()
        if (adapter.header != null) {
            reloadTotalMoney(adapter.header!!)
        }
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VOICE_SEARCH_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotBlank()) {
                    adapter.setDataVoice(result)
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.SEARCH.name
                    dataObject.result = result.trim()
                    logTrackingVersion2(
                        QuickstartPreferences.VOICE_SEARCH_RESULT_v2,
                        Gson().toJson(dataObject).toString()
                    )
                    loadSearch(result)
                }
            }
        }
    }

    private fun setCallingApiState() {
        progressBar.visibility = View.VISIBLE
        isCallingApi = true
    }

    private fun setFinishedCallApiState() {
        progressBar.visibility = View.GONE
        isCallingApi = false
    }

    private fun showToolTip() {

        //Functions.faceIn(dim)
        //Functions.bounceInLeft(ln_product)

        // Prepare the View for the animation
        tip.visibility = View.VISIBLE
        tip.alpha = 0.0f
        tip.translationY = tip.height.toFloat()

        // Start the animation
        tip.animate()
            .translationY(0f)
            .alpha(1.0f)
            .setDuration(800)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                }
            })
    }

    private fun hideQuickPay() {

        tip.animate().translationY(tip.height.toFloat())
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    tip.visibility = View.GONE
                }
            })
    }
}

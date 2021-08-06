package com.bda.omnilibrary.ui.splashActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.HistoryModel
import com.bda.omnilibrary.model.HomeModel
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_splash.*


open class SplashActivity : BaseActivity(), SplashContact.View {
    private lateinit var presenter: SplashContact.Presenter
    private lateinit var preferencesHelper: PreferencesHelper
    private var productId: String? = null
    private var utmSource: String? = null
    private var utmCampaign: String? = null
    private var utmContent: String? = null
    private var utmMedium: String? = null
    private var utmTargetType: String? = null
    private var utmLandingImage: String? = null
    private var utmTargetID: String? = null
    private var isPopup = false
    private var popupType = ""
    private var isNotification = false
    private lateinit var checkInternetConnection: CheckInternetConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("BBBHAU", "OnCreate:")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setLanguage(Config.LANGUAGE.vi.toString())
        val dataObject = LogDataRequest()
        dataObject.source = utmSource
        dataObject.campaign = utmCampaign
        dataObject.medium = utmMedium
        dataObject.targetUid = utmTargetID
        dataObject.targetType = utmTargetType
        dataObject.content = utmContent
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.APP_OPEN_v2,
            data
        )

        preferencesHelper = PreferencesHelper(this)
        Config.developer_mode = preferencesHelper.developerMode
        this.presenter = SplashPresenter(this, this)
        ImageUtils.loadImageNoCache(
            this,
            background_splash,
            "https://tvcommerce-st.fptplay.net/prod/images/splashscreen_images/splash_screen.jpeg?mode=scale&w=1920&h=1080&v=" + System.currentTimeMillis()
        )
        initial()

    }

    private fun initial() {
        Log.d("BBBHAU", "initial:")
        if (Functions.checkInternet(this)) {
            presenter.loadSplash(LibConfig.token, getHistory())
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
            checkInternetConnection = CheckInternetConnection()
            checkInternetConnection.addConnectionChangeListener(object : ConnectionChangeListener {
                override fun onConnectionChanged(isConnectionAvailable: Boolean) {
                    if (isConnectionAvailable) {
                        checkInternetConnection.removeConnectionChangeListener()
                        initial()
                    }
                }
            })
        }
    }

    private fun gotoMain(
        model: HomeModel,
        product_id: String?,
        isPopup: Boolean,
        isNotification: Boolean,
        popup_type: String,
        landingUrlImage: String?
    ) {
        System.gc()
        Config.homeData = model
        val dataObject = LogDataRequest()
        dataObject.source = utmSource
        dataObject.campaign = utmCampaign
        dataObject.medium = utmMedium
        dataObject.targetUid = utmTargetID
        dataObject.targetType = utmTargetType
        dataObject.content = utmContent
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_SPLASH_v2,
            data
        )

        gotoMainActivity(product_id, isPopup, isNotification, popup_type, landingUrlImage)
        finish()
    }

    private fun getHistory(): HistoryModel {
        var history = HistoryModel(ArrayList(), "")
        if (preferencesHelper.history != "") {
            history = Gson().fromJson(
                preferencesHelper.history,
                object : TypeToken<HistoryModel>() {}.type
            )
        }
        return history
    }

    private fun requestNewToken(bundle: Bundle) {
        Log.d("BBBHAU", "requestNewToken:")
        val launchIntent =
            this.packageManager.getLaunchIntentForPackage("net.fptplay.ottbox".takeIf { Config.platform == "box2021" }
                ?: "net.androidboxfptplay")
        if (launchIntent != null) {
            launchIntent.putExtra("TV_SHOPPING", true)
            launchIntent.putExtra("DATA", bundle)
            startActivity(launchIntent)
        } else {
            Functions.showMessage(this, "Package not found")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getConfig(Config.PARTNER.FPT)
        getTokenFromIntent(intent)
    }

    private fun getTokenFromIntent(intent: Intent?) {
        Log.d("BBBHAU", "getTokenFromIntent:")
        ImageUtils.loadImageNoCache(
            this,
            background_splash,
            "https://tvcommerce-st.fptplay.net/prod/images/splashscreen_images/splash_screen.jpeg?mode=scale&w=1920&h=1080&v=" + System.currentTimeMillis()
        )
        val bundle = intent?.extras
//        var token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpYXQiOjE1OTI0NTgyMzk2NjAsImp0aSI6ImI2NjY2ZWJkLWVhOGItNGEzYy05OTFlLWM4Y2RmZDc2YTVjMSIsInN1YiI6Ijk3MTkxMjYifQ.Qu2uel0LvdoDZcG-pydfx0N1o9Rtg82q-RbGOLuMDNs"
//        presenter.loadSplash(token, getHistory())
        if (bundle != null && !bundle.isEmpty) {
            var token: String? = bundle.getString("ACCESS_TOKEN")
            val data = bundle.getBundle("DATA")
            val popupLink = bundle.getString("APP_LINK")
            var launcherLink = bundle.getString("LAUNCHER_LINK")
            ////check token box2018
            if (Config.platform == "box2018") {
                if (token == null) {
                    if (data != null) {
                        token = data.getString("ACCESS_TOKEN")
                    }
                    if (token == null) {
                        finish()
                    }
                }
            }

            ///////////

            var link = popupLink.takeIf { popupLink != null }
                ?: launcherLink.takeIf { launcherLink != null }
            if (link != null) {
                ///check pop up
                productId = Functions.getProductIdFromAppLink(link)
                setUTM(link)
                if (!productId.isNullOrBlank()) {
                    isPopup = true
                    popupType = utmTargetType.takeIf { utmTargetType != null } ?: ""
                }
            } else {
                productId = bundle.getString("uid")
            }

            if (data != null) {
                productId = data.getString("uid")
                isPopup = data.getBoolean("popup")
                link = data.getString("LAUNCHER_LINK")
                if (link != null && link.isNotEmpty()) {
                    setUTM(link)
                }

                data.getString("popup_type")?.let {
                    popupType = it
                }

                isNotification = data.getBoolean("notification")
                for (i in data.keySet()) {
                    if (i.equals("type", true)) {
                        isNotification = true
                    }
                }
            }

            if (token != null) {
                presenter.loadSplash(token, getHistory())
            } else {
                requestToken(
                    productId,
                    isPopup,
                    isNotification,
                    popupType, link
                )
            }

        } else {
            requestToken(
                "", false,
                isNotification = false,
                popup_type = "",
                link = null
            )
        }
    }

    private fun requestToken(
        product_id: String?,
        isPopup: Boolean,
        isNotification: Boolean,
        popup_type: String,
        link: String?,
    ) {
        requestNewToken(
            bundleData(
                product_id, isPopup,
                isNotification,
                popup_type,
                link
            )
        )
    }

    private fun setUTM(link: String) {
        utmSource = Functions.getValueFromAppLink(link, "utm_source")
        utmMedium = Functions.getValueFromAppLink(link, "utm_medium")
        utmContent = Functions.getValueFromAppLink(link, "utm_content")
        utmCampaign = Functions.getValueFromAppLink(link, "utm_campaign")
        utmTargetID = Functions.getValueFromAppLink(link, "product_id")
        utmTargetType = Functions.getValueFromAppLink(link, "type")
        utmLandingImage = Functions.getValueFromAppLink(link, "image")
    }

    private fun bundleData(
        product_id: String?,
        isPopup: Boolean,
        isNotification: Boolean,
        popup_type: String,
        link: String?,
    ): Bundle {
        val bundleData = Bundle()
        bundleData.putString("uid", product_id)
        bundleData.putBoolean("popup", isPopup)
        bundleData.putBoolean("notification", isNotification)
        bundleData.putString("popup_type", popup_type)
        bundleData.putString("LAUNCHER_LINK", link)
        return bundleData
    }


    override fun sendSplashUISussess(model: HomeModel) {
        if (preferencesHelper.firstOpen) {
            gotoMain(model, productId, isPopup, isNotification, popupType, utmLandingImage)
            preferencesHelper.setFirstOpen(false)
        } else {
            loadAnimation(model)
        }

    }

    override fun sendSplashUIFail(erroMessage: String) {
        Log.d("SplashActivity", erroMessage)
    }

    override fun sendBoxDataFail(erroMessage: String) {
        Functions.showMessage(this, erroMessage)
    }

    private fun loadAnimation(model: HomeModel) {
        gotoMain(model, productId, isPopup, isNotification, popupType, utmLandingImage)
    }
}
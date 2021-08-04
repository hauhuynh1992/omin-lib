package com.bda.omnilibrary.ui.mainActivity

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.homev2.HomeAdapterV2
import com.bda.omnilibrary.dialog.ExitAppDialog
import com.bda.omnilibrary.dialog.PopUpDialog
import com.bda.omnilibrary.dialog.VoucherPopupDialog
import com.bda.omnilibrary.dialog.assitant.AssistantDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.service.BackgroundSoundService
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.REQUEST_CUSTOMER_RESULT_CODE
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.RESPONSE_CUSTOMER_RESULT
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import kotlin.random.Random


class MainActivity : BaseActivity(), MainActivityContact.View {

    private lateinit var presenter: MainActivityContact.Presenter
    private lateinit var homeModels: HomeModel
    private var homeAdapter: HomeAdapterV2? = null
    private var productId: String? = null
    private var landingImageUrl: String? = null
    private var isPopup = false
    private var popupType = "product"
    private var isNotification = false
    private var addCartList = ArrayList<Product>()
    private var skyMusicResponse: SkyMusicResponse? = null
    private var clickfrom = ""
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    companion object {
        var firstView = true
        private var weakActivity: WeakReference<MainActivity>? = null
        const val REQUEST_VOICE_PERMISSIONS_REQUEST_CODE = 1
        fun getMaiActivity(): MainActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    private fun intialFirebaseConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig!!.reset()
        mFirebaseRemoteConfig!!.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                if (LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()) {
                    Config.oncart = mFirebaseRemoteConfig!!.getString("ONCART")
                    Config.oncartdetail = mFirebaseRemoteConfig!!.getString("ONCARTDETAIL")
                } else {
                    Config.oncart = mFirebaseRemoteConfig!!.getString("ONCARTEU")
                    Config.oncartdetail = mFirebaseRemoteConfig!!.getString("ONCARTDETAILEU")
                }

            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainActivityPresenter(this, this)
        loadhome(intent)
        intialFirebaseConfig()
    }

    private fun loadhome(intent: Intent?) {
        if (intent != null && intent.getBooleanExtra(QuickstartPreferences.RESTART, false)) {
            val history = getHistory()
            history.customer_id = ""
            progressBar8.visibility = View.VISIBLE
            presenter.loadHomeData(history)
        } else {
            weakActivity = WeakReference(this@MainActivity)
            if (Config.homeData != null) {
                homeModels = Config.homeData!!
                if (intent != null) {
                    val bundle = intent.extras
                    if (bundle != null && !bundle.isEmpty) {
                        productId = bundle.getString(QuickstartPreferences.PRODUCT_ID)
                        isPopup = bundle.getBoolean(QuickstartPreferences.POPUP)
                        popupType = bundle.getString(QuickstartPreferences.POPUP_TYPE) ?: ""
                        isNotification = bundle.getBoolean(QuickstartPreferences.NOTIFICATION)
                        landingImageUrl = bundle.getString(QuickstartPreferences.LANDING_PAGE)
                    } else {
                        finish()
                    }
                }
                initial()
            } else {
                finish()
            }
            clickfrom = QuickstartPreferences.CLICK_FROM_POPUP.takeIf { isPopup }
                ?: QuickstartPreferences.CLICK_FROM_NOTIFICATION
        }
    }

    override fun onNewIntent(intent: Intent?) {
        loadhome(intent)
        super.onNewIntent(intent)

    }

    private fun initial() {

        rv_home.removeAllViews()
        homeAdapter = null
        rv_home.extraLayoutSpace = 800
        rv_home.setItemViewCacheSize(20)
        rv_home.clearFindViewByIdCache()
        rv_home.setHasFixedSize(true)
        if (getDatabaseHandler()!!.getCountLogItem() > 0) {
            sendListLog()
        }
        setData()

        Handler().postDelayed({
            if (getCheckCustomerResponse() != null)
                presenter.loadOrderMegaMenu(getCheckCustomerResponse()!!.data.uid)
        }, 500)

    }

    private fun loadOnlineCart() {
        if (Functions.checkInternet(this)) {
            if (getCheckCustomerResponse() != null) {
                presenter.loadOnlineCart(getCheckCustomerResponse()!!.data.uid)
            }
        }

    }

    private fun setData() {
        if (LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()) {
            presenter.loadListMusic()
        }

        if (!hasHybridLayout()) {
            ImageUtils.loadImage(
                this,
                main_cover,
                homeModels.data.image_cover,
                ImageUtils.TYPE_PRIVIEW_LAGE
            )
        }
        loadOnlineCart()
        homeAdapter = HomeAdapterV2(
            this,
            getAvailableSection()
        )

        homeAdapter?.setOnCallbackListener(object : HomeAdapterV2.OnCallBackListener {
            override fun onLiveStreamClick(
                product: LiveStream,
                position: Int,
                clickFrom: String
            ) {
                gotoLiveStreamProduct(product.uid)
            }

            override fun onPromotionClick(promotion: Promotion, position: Int, clickFrom: String) {
                gotoDetailPromotion(promotion)
            }

            override fun onClickVoiceAssitance() {
                if (arrayListOf(
                        "box2019",
                        "box2020",
                        "omnishopeu",
                        "box2021"
                    ).contains(Config.platform)
                ) {
                    requestVoicePermission()
                } else {
                    if (getDatabaseHandler()!!.getCountCartItem() > 0) {
                        val dataObject = LogDataRequest()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.CLICK_CART_BUTTON_v2,
                            data
                        )
                        gotoCart()
                    }
                }
            }

            override fun onShopItemClick(
                product: BrandShop,
                position: Int,
                sectionName: String,
                sectionIndex: String,
                sectionType: String
            ) {
                val dataObject = LogDataRequest()
                dataObject.layoutId = homeModels.data.name
                dataObject.screen = Config.SCREEN_ID.HOME.name
                dataObject.brandshopUid = product.uid
                dataObject.brandshopName = product.name
                dataObject.categoryIndex = position.toString()
                dataObject.sectionName = sectionName
                dataObject.sectionIndex = sectionIndex
                dataObject.sectionType = sectionType


                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_BRANDSHOP_v2,
                    data
                )
                gotoBrandShopDetail(product.uid)
            }

            override fun onFocusHybridSection(section: HybridSection) {
                ImageUtils.loadImageHybrid(
                    this@MainActivity,
                    main_cover,
                    section.image,
                    ImageUtils.TYPE_PRIVIEW_LAGE
                )
            }

            override fun onHybridSection(product: HybridSection, position: Int) {
                gotoScreen(product.sectionValue, product.sectionRef, product.image_background)
            }

            override fun onItemClick(
                product: Product,
                position: Int,
                clickFrom: String,
                sectionName: String,
                sectionIndex: String,
                sectionType: String
            ) {
                loadProduct(
                    product,
                    position,
                    clickFrom,
                    sectionName = sectionName,
                    sectionType = sectionType,
                    sectionIndex = sectionIndex
                )
            }

            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun onCollectionClick(
                collection: SimpleCollection,
                position: Int,
                sectionName: String,
                sectionIndex: String,
                selectType: String
            ) {
                if (Functions.checkInternet(this@MainActivity)) {
                    val dataObject = LogDataRequest()
                    dataObject.layoutId = homeModels.data.name
                    dataObject.screen = Config.SCREEN_ID.HOME.name
                    dataObject.categoryId = collection.uid
                    dataObject.category = collection.collection_name
                    dataObject.categoryIndex = position.toString()
                    dataObject.sectionName = sectionName
                    dataObject.sectionIndex = sectionIndex
                    dataObject.sectionType = selectType


                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_CATEGORY_v2,
                        data
                    )
                    gotoCategory(
                        collection.uid,
                        collection.collection_name,
                        sectionName,
                        sectionIndex,
                        selectType
                    )
                } else {
                    Functions.showMessage(
                        this@MainActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onHaveProductOutsite(product: Product, position: Int) {

            }

            override fun onMoreClick(
                subCollectionName: String,
                subcCollectionId: String,
                sectionName: String,
                sectionIndex: String,
                sectionType: String
            ) {
                if (Functions.checkInternet(this@MainActivity)) {
                    gotoCollection(
                        subcCollectionId,
                        subCollectionName,
                        sectionName,
                        sectionIndex,
                        sectionType
                    )
                } else {
                    Functions.showMessage(
                        this@MainActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onVoucherClick(voucher: Voucher, position: Int) {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.HOME.name
                dataObject.voucherCode = voucher.voucher_code
                dataObject.voucherId = voucher.uid
                dataObject.voucherValue = voucher.voucher_value.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_VOUCHER_v2,
                    data
                )

                VoucherPopupDialog(this@MainActivity)
            }

            override fun onOnClickFooter() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.HOME.name
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_BACK_TO_TOP_BUTTON_v2,
                    data
                )

                Functions.alphaAnimation(layout_media, 1f) {}

                rv_home.scrollToPosition(0)
            }
        })
        rv_home.adapter = homeAdapter
        if ((isPopup || isNotification) && productId != null) {
            gotoScreen(popupType, productId!!, landingImageUrl)
        } else if ((!isPopup && !isNotification) && productId != null) {
            clickfrom = QuickstartPreferences.CLICK_PRODUCT_OUTSITE_FROM_HOME
            if (!TextUtils.isDigitsOnly(productId!!.trim())) {
                presenter.callApiProduct(productId!!.trim())
            }
        } else {
            if (getCheckCustomerResponse() != null) {
                Handler().postDelayed({
                    val popUpRequest =
                        PopUpRequest(
                            getCheckCustomerResponse()!!.data.uid,
                            Config.popup_boxid,
                            Config.SCREEN_ID.HOME.toString()
                        )
                    presenter.loadPopup(popUpRequest)
                }, 1000)
            }
        }
    }

    private fun gotoScreen(type: String, id: String, url: String? = "") {
        when (type) {
            "product" -> {
                presenter.callApiProduct(id)
            }
            "collection_temp" -> {
                gotoCollection(id, getString(R.string.app_name))
            }
            "brandShop", "brandshop", "brand_shop" -> {
                gotoBrandShopDetail(id)
            }
            "collection" -> {
                gotoCategory(id, getString(R.string.app_name))
            }
            "livestream" -> {
                gotoLiveStreamProduct(id)
            }
            "landing_page" -> {
                presenter.loadLandingPage(id)
            }
            "promotion" -> {
                gotoDetailPromotion(id)
            }
        }

    }

    fun loadCart() {
        if (getDatabaseHandler() != null) {
            addCartList = getDatabaseHandler()!!.getLProductList()
            homeAdapter?.setQuantity(addCartList.size.toString())
            homeAdapter?.setPrice(getTotalMoney(addCartList))
            if (getCheckCustomerResponse() != null && getCheckCustomerResponse()!!.data.customer_phone.isNotEmpty()) {
                homeAdapter?.setPhone(getCheckCustomerResponse()!!.data.customer_phone)
            }
            if (getCheckCustomerResponse() != null) {
                presenter.editOnlineCart(
                    CartRequest(
                        getCheckCustomerResponse()!!.data.uid,
                        addCartList
                    )
                )
            }
        }
    }

    var mLastKeyDownTime = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        val current = System.currentTimeMillis()
        return if (current - mLastKeyDownTime < Config.lastKeyDownTime) {
            true
        } else {
            mLastKeyDownTime = current

            when (keyCode) {

                KeyEvent.KEYCODE_HOME -> {
                    finish()
                }
                KeyEvent.KEYCODE_MENU -> {
                    finish()
                }
                KeyEvent.KEYCODE_DPAD_CENTER -> {

                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (rv_home.hasFocus()) {
                        if (homeAdapter?.getCurrentPositon() == 1) {
                            Functions.alphaAnimation(layout_media, 0f) {}
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (event!!.action == KeyEvent.ACTION_DOWN) {

                        if (rv_home.hasFocus()) {
                            Handler().postDelayed({
                                if (homeAdapter?.getLiveCurrentPositon() == 1 &&
                                    (homeAdapter!!.hasFocusHeader())
                                ) {
                                    Functions.alphaAnimation(layout_media, 1f) {}
                                }
                            }, 100)
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {

                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                }

                KeyEvent.KEYCODE_PROG_RED -> {
                    if (arrayListOf(
                            "box2019",
                            "box2020",
                            "omnishopeu",
                            "box2021"
                        ).contains(Config.platform)
                    ) {
                        requestVoicePermission()
                    } else {
                        if (getDatabaseHandler()!!.getCountCartItem() > 0) {
                            val dataObject = LogDataRequest()
                            val data = Gson().toJson(dataObject).toString()
                            logTrackingVersion2(
                                QuickstartPreferences.CLICK_CART_BUTTON_v2,
                                data
                            )
                            gotoCart()
                        }
                    }
                }

                KeyEvent.KEYCODE_F4 -> {
                    if (arrayListOf(
                            "box2019",
                            "box2020",
                            "omnishopeu",
                            "box2021"
                        ).contains(Config.platform)
                    ) {
                        requestVoicePermission()
                    } else {
                        if (getDatabaseHandler()!!.getCountCartItem() > 0) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.HOME.name
                            val data = Gson().toJson(dataObject).toString()
                            logTrackingVersion2(
                                QuickstartPreferences.CLICK_CART_BUTTON_v2,
                                data
                            )
                            gotoCart()
                        }
                    }
                }

                else -> return super.onKeyDown(keyCode, event)
            }
            return super.onKeyDown(keyCode, event)
        }


    }

    private fun hasHybridLayout(): Boolean {
        return homeModels.data.section.size > 0 && homeModels.data.section[0].hybrid_section.size > 0
    }

    override fun onResume() {
        super.onResume()
        loadCart()
        if (assistantDialog == null) {
            playMusicBackground()
        }
        val dataObject = LogDataRequest()
        dataObject.layoutId = Config.homeData?.data?.name
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.HOME_LOADED_v2,
            data
        )

    }

    override fun onBackPressed() {
        ExitAppDialog(this, R.string.exit_app, {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.HOME.name
            dataObject.popUpName = "Pop-up exit app"
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                data
            )
            super.onBackPressed()

        }, {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.HOME.name
            dataObject.popUpName = "Pop-up exit app"
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                data
            )
        })
    }

    override fun sendCartOnlineSuccess(cart: CartModel) {
        getDatabaseHandler()!!.deleteCart()
        addListItemToCart(cart.data)
        loadCart()
    }

    override fun sendCartOnlineFail(error: String) {
        loadCart()
    }

    override fun sendProductSuccess(product: Product) {
        loadProduct(product, 0, clickFrom = clickfrom)
    }

    override fun sendSkyFail(erroMessage: String) {

    }

    override fun sendSkySussess(model: SkyMusicResponse) {
        skyMusicResponse = model
        val intent = Intent(this, BackgroundSoundService::class.java)
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
    }

    override fun sendListMusicFail(erroMessage: String) {

    }

    override fun sendListMusicSussess(model: MusicResponse) {
        val sb = StringBuilder()
        for (code in model.result) {
            sb.append(code.song_id)
            sb.append(",")
        }
        presenter.loadSkyMusic(sb.toString())
    }

    override fun sendListPopUpFail(erroMessage: String) {

    }

    override fun sendListPopUpSussess(model: PopUpResponse) {
        val random = Random.nextInt(0, model.result.size)
        val data = model.result[random]
        if (getCheckCustomerResponse() != null) {
            val activePopUpRequest =
                ActivePopUpRequest(getCheckCustomerResponse()!!.data.uid, data.uid)
            presenter.sendActivePopUp(activePopUpRequest)
        }

        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.HOME.name
        dataObject.popUpUid = data.uid
        dataObject.targetType = data.popup_type
        when (data.popup_type) {
            Config.POPUP_TYPE.PRODUCT.toString() -> {
                dataObject.targetUid = data.popup_product.uid
            }
            Config.POPUP_TYPE.COLLECTION.toString() -> {
                dataObject.targetUid = data.popup_collection.uid
            }
            Config.POPUP_TYPE.BRANDSHOP.toString() -> {
                dataObject.targetUid = data.popup_brandshop.uid
            }
            Config.POPUP_TYPE.COLLECTION_TEMP.toString() -> {
                dataObject.targetUid = data.popup_collection_temp.uid
            }
            Config.POPUP_TYPE.LANDINGPAGE.toString() -> {
                dataObject.targetUid = data.landing_page.landing_name
            }
        }
        val dataTracking = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_POPUP_v2,
            dataTracking
        )

        PopUpDialog(this, data, {
            loadDestinationPopUp(data)
        }, {
            val dataObject1 = LogDataRequest()
            dataObject1.screen = Config.SCREEN_ID.HOME.name
            dataObject1.popUpUid = data.uid
            dataObject1.targetType = data.popup_type
            when (data.popup_type) {
                Config.POPUP_TYPE.PRODUCT.toString() -> {
                    dataObject1.targetUid = data.popup_product.uid
                }
                Config.POPUP_TYPE.COLLECTION.toString() -> {
                    dataObject1.targetUid = data.popup_collection.uid
                }
                Config.POPUP_TYPE.BRANDSHOP.toString() -> {
                    dataObject1.targetUid = data.popup_brandshop.uid
                }
                Config.POPUP_TYPE.COLLECTION_TEMP.toString() -> {
                    dataObject1.targetUid = data.popup_collection_temp.uid
                }
                Config.POPUP_TYPE.LANDINGPAGE.toString() -> {
                    dataObject1.targetUid = data.landing_page.landing_name
                }
            }
            val dataTracking1 = Gson().toJson(dataObject1).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLOSE_POPUP_v2,
                dataTracking1
            )
        })

    }

    private fun loadDestinationPopUp(data: PopUpResponse.ResultPopup) {
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.HOME.name
        dataObject.popUpUid = data.uid
        dataObject.targetType = data.popup_type

        when (data.popup_type) {
            Config.POPUP_TYPE.PRODUCT.toString() -> {
                dataObject.targetUid = data.popup_product.uid
                clickfrom = QuickstartPreferences.CLICK_FROM_PRODUCT_APP_POP_UP_HOME
                presenter.callApiProduct(data.popup_product.uid)
            }
            Config.POPUP_TYPE.COLLECTION_TEMP.toString() -> {
                dataObject.targetUid = data.popup_collection_temp.uid
                gotoCollection(
                    data.popup_collection.uid,
                    data.popup_collection.name
                )
            }
            Config.POPUP_TYPE.COLLECTION.toString() -> {
                dataObject.targetUid = data.popup_collection.uid
                gotoCategory(
                    data.popup_collection.uid,
                    data.popup_collection.name
                )
            }
            Config.POPUP_TYPE.BRANDSHOP.toString() -> {
                dataObject.targetUid = data.popup_brandshop.uid
                gotoBrandShopDetail(data.popup_brandshop.uid)
            }
            Config.POPUP_TYPE.LANDINGPAGE.toString() -> {
                dataObject.targetUid = data.landing_page.uid
                presenter.loadLandingPage(data.landing_page.uid)
            }
        }
        val dataTracking = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.CLICK_POPUP_v2,
            dataTracking
        )
    }

    override fun sendActivePopUpFail(erroMessage: String) {

    }

    override fun sendActivePopUpSussess(model: ActivePopUpResponse) {

    }

    override fun sendSplashUISussess(model: HomeModel) {
        progressBar8.visibility = View.GONE
        Config.homeData = model
        loadhome(null)
    }

    override fun sendSplashUIFail(erroMessage: String) {
        progressBar8.visibility = View.GONE
        Functions.showMessage(this, erroMessage)
    }

    override fun sendLandingPageSuccess(cart: LandingPageModel) {
        gotoLandingPage(cart.data?.image_cover)
    }

    override fun sendOrderMegaMenu(quantity: Int, supplier: String, date: Int) {
        homeAdapter!!.updateOrderDateData(quantity, supplier, date)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openShoppingAssistant()
        }
    }

    private fun loadProduct(
        product: Product,
        position: Int,
        clickFrom: String,
        sectionName: String? = null,
        sectionIndex: String? = null,
        sectionType: String? = null
    ) {
        gotoProductDetail(
            product,
            position,
            clickFrom,
            sectionIndex = sectionIndex,
            sectionType = sectionType,
            sectionName = sectionName
        )
        presenter.loadAddHistory(product.uid)
    }

    var myService: BackgroundSoundService? = null
    var isBound = false
    private val myConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            val binder = service as BackgroundSoundService.MyLocalBinder
            myService = binder.getService()
            if (skyMusicResponse != null && skyMusicResponse!!.data.size > 0) {
                myService!!.setMusic(skyMusicResponse!!.data)
                isBound = true
            } else {
                myService?.stopSelf()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    fun playMusicBackground() {
        if (myService != null && isBound) {
            myService!!.loadMusic()
        }
    }

    fun pauseMusicToPlayVideo() {
        if (myService != null && isBound) {
            myService!!.pauseToPlayVideo()
        }
    }

    fun checkIsPlayingBackground(): Boolean {
        if (myService != null && isBound) {
            return myService!!.checkPlaying()
        }
        return false
    }

    fun pauseMusicBackground() {
        if (myService != null && isBound) {
            myService!!.stop()
        }
    }


    private fun requestVoicePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            REQUEST_VOICE_PERMISSIONS_REQUEST_CODE
        )
    }

    var assistantDialog: DialogFragment? = null
    private fun openShoppingAssistant() {
        if (getCheckCustomerResponse() != null) {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.HOME.name
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.OPEN_SHOPPING_ASSISTANT_v2,
                data
            )
            assistantDialog = AssistantDialog()
            assistantDialog?.show(getFManager(), assistantDialog?.tag)
            getFManager().executePendingTransactions()
        } else {
            gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
        }

    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(myConnection)
        }
        firstView = true
        super.onDestroy()
    }

    private fun getAvailableSection(): List<HomeModel.LayoutSection> {
        return homeModels.data.section.filterIndexed { _, layoutSection ->
            layoutSection.livestream.size > 0 /*|| layoutSection.endStream.size > 0*/
                    || layoutSection.collections.size > 0
                    || layoutSection.collectionTemp.items.size > 0
                    || layoutSection.viewed_prod.size > 0
                    || layoutSection.favourite.size > 0
                    || layoutSection.buylater.size > 0
                    || layoutSection.brand_shop.size > 0
                    || layoutSection.hybrid_section.size > 0
                    || layoutSection.promotions.size > 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CUSTOMER_RESULT_CODE -> {
                val result = data?.getStringExtra(RESPONSE_CUSTOMER_RESULT)
                if (result != null && result.isNotBlank()) {
                    val history = getHistory()
                    history.customer_id = getCheckCustomerResponse()!!.data.uid
                    progressBar8.visibility = View.VISIBLE
                    presenter.loadHomeData(
                        history
                    )
                }
            }
            DiscoveryVoiceActivity.REQUEST_VOICE_SEARCH_CODE -> {
                assistantDialog?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun getHistory(): HistoryModel {
        var history = HistoryModel(ArrayList(), "")
        if (getPreferenceHelper() != null && getPreferenceHelper()!!.history != "") {
            history = Gson().fromJson(
                getPreferenceHelper()!!.history,
                object : TypeToken<HistoryModel>() {}.type
            )
        }
        return history
    }
}
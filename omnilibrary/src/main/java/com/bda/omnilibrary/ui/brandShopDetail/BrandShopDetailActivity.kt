package com.bda.omnilibrary.ui.brandShopDetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.brandShopDetail.BrandShopDetailAdapter
import com.bda.omnilibrary.dialog.PopUpDialog
import com.bda.omnilibrary.dialog.ProductDescriptionDialog
import com.bda.omnilibrary.dialog.VoucherPopupDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_brand_shop_detail.*
import kotlinx.android.synthetic.main.activity_brand_shop_detail.rv_home
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference
import kotlin.random.Random

class BrandShopDetailActivity : BaseActivity(), BrandShopDetailContract.View {
    private lateinit var adapter: BrandShopDetailAdapter
    private lateinit var brandShopModels: BrandShopModel
    private lateinit var brandShop: BrandShop
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var uid: String

    private lateinit var presenter: BrandShopDetailContract.Presenter

    companion object {
        private var weakActivity: WeakReference<BrandShopDetailActivity>? = null

        fun getActivity(): BrandShopDetailActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brand_shop_detail)

        weakActivity = WeakReference(this@BrandShopDetailActivity)

        uid = intent.getStringExtra(QuickstartPreferences.PRODUCT_BRAND_SHOP_ID).toString()
        if (uid.isNotEmpty()) {
            presenter = BrandShopDetailPresenter(this, this)
            presenter.loadPresenter(uid)
            progressBar4.visibility = View.VISIBLE
        } else {
            finish()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initial() {
        preferencesHelper = PreferencesHelper(this)

        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
        dataObject.brandshopName = brandShop.name
        dataObject.brandshopUid = brandShop.uid
        dataObject.layoutId = Config.homeData?.data?.name
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_BRANDSHOP_DETAILS_v2,
            data
        )

        adapter = BrandShopDetailAdapter(this, filterValidSection(), brandShop)
        adapter.setOnCallbackListener(object : BrandShopDetailAdapter.OnCallBackListener {
            override fun onClickIntroShop() {
                ProductDescriptionDialog(
                    this@BrandShopDetailActivity,
                    brandShop.description_html
                )
            }

            override fun onItemClick(
                product: Product,
                position: Int,
                clickFrom: String,
                sectionName: String,
                sectionIndex: String,
                sectionType: String,
            ) {
                loadProduct(
                    product,
                    position,
                    clickFrom,
                    sectionName = sectionName,
                    sectionIndex = sectionIndex,
                    sectionType = sectionType
                )
            }

            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun onCollectionClick(
                collections: SimpleCollection, position: Int,
                sectionName: String,
                sectionIndex: String,
                selectType: String,
            ) {
                if (Functions.checkInternet(this@BrandShopDetailActivity)) {
                    val dataObject1 = LogDataRequest()
                    dataObject1.layoutId = Config.homeData?.data?.name
                    dataObject1.screen = Config.SCREEN_ID.BRANDSHOP.name
                    dataObject1.categoryId = collections.uid
                    dataObject1.category = collections.collection_name
                    dataObject1.categoryIndex = position.toString()
                    dataObject1.sectionName = sectionName
                    dataObject1.sectionIndex = sectionIndex
                    dataObject1.sectionType = selectType

                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_CATEGORY_v2,
                        Gson().toJson(dataObject1).toString()
                    )

                    gotoCategory(
                        collections.uid, collections.collection_name,
                        brandShopID = brandShop.uid,
                        brandShopName = brandShop.name
                    )
                } else {
                    Functions.showMessage(
                        this@BrandShopDetailActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onBrandShopChildCollectionClick(
                collections: SimpleCollection,
                position: Int,
                sectionName: String,
                sectionIndex: String,
                sectionType: String,
            ) {
                if (Functions.checkInternet(this@BrandShopDetailActivity)) {
                    val dataObject1 = LogDataRequest()
                    dataObject1.layoutId = Config.homeData?.data?.name
                    dataObject1.screen = Config.SCREEN_ID.BRANDSHOP.name
                    dataObject1.categoryId = collections.uid
                    dataObject1.category = collections.collection_name
                    dataObject1.categoryIndex = position.toString()
                    dataObject1.sectionName = sectionName
                    dataObject1.sectionIndex = sectionIndex
                    dataObject1.sectionType = sectionType

                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_BRANDSHOP_CATEGORY_v2,
                        Gson().toJson(dataObject1).toString()
                    )
                    gotoCollection(
                        collections.uid, collections.collection_name,
                        brandShopID = brandShop.uid,
                        brandShopName = brandShop.name
                    )
                } else {
                    Functions.showMessage(
                        this@BrandShopDetailActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onHaveProductOutsite(product: Product, position: Int) {
            }

            override fun onMoreClick(subCollectionName: String, subcCollectionId: String) {
                if (Functions.checkInternet(this@BrandShopDetailActivity)) {
                    gotoCollection(
                        subcCollectionId,
                        subCollectionName,
                        brandShopID = brandShop.uid,
                        brandShopName = brandShop.name
                    )
                } else {
                    Functions.showMessage(
                        this@BrandShopDetailActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onVoucherClick(voucher: Voucher, position: Int) {
                VoucherPopupDialog(this@BrandShopDetailActivity)

            }

            override fun onOnClickFooter() {
                rv_home.scrollToPosition(0)

                if (rv_home.hasFocus()) {
                    Handler().postDelayed({
                        adapter.buttonIntro?.requestFocus()
                    }, 50)
                }
            }

            override fun onLiveStreamClick(product: LiveStream, position: Int, clickFrom: String) {
                gotoLiveStreamProduct(product.uid)
            }

            override fun onHybridSection(product: HybridSection, position: Int) {
                gotoScreen(product.sectionValue, product.sectionRef, product.image_background)
            }

            override fun onPromotionClick(promotion: Promotion, position: Int, clickFrom: String) {
                gotoDetailPromotion(promotion)
            }
        })
        rv_home.extraLayoutSpace = 800
        rv_home.setItemViewCacheSize(20)
        rv_home.setHasFixedSize(true)
        rv_home.adapter = adapter

        Handler().postDelayed({
            adapter.buttonSearch?.requestFocus()
        }, 100)
        if (getCheckCustomerResponse() != null) {
            Handler().postDelayed({
                val popUpRequest =
                    PopUpRequest(
                        getCheckCustomerResponse()!!.data.uid,
                        Config.popup_boxid,
                        Config.SCREEN_ID.BRANDSHOP.toString(),
                        uid
                    )
                presenter.loadPopup(popUpRequest)
            }, 1000)
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
                gotoLandingPage(url)
            }
        }

    }

    override fun getSuccess(brandShop: BrandShop, model: BrandShopModel) {
        brandShopModels = model
        this.brandShop = brandShop
        progressBar4.visibility = View.GONE
        initial()
    }

    private fun filterValidSection(): List<BrandShopModel.LayoutSection> {
        val i = getBrandShop(brandShopModels.data.section)

        if (i != null) {
            brandShopModels.data.section.removeAt(i)
        }

        return brandShopModels.data.section.filterIndexed { _, layoutSection ->
            layoutSection.livestream.size > 0 /*|| layoutSection.endStream.size > 0*/
                    || layoutSection.collections.size > 0
                    || layoutSection.collectionTemp.items.size > 0
                    || layoutSection.viewed_prod.size > 0
                    || layoutSection.favourite.size > 0
                    || layoutSection.buylater.size > 0
                    || layoutSection.brandshop_collections.size > 0
                    || layoutSection.hybrid_section.size > 0
                    || layoutSection.promotions.size > 0
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

                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (event!!.action == KeyEvent.ACTION_DOWN) {

                        if (rv_home.hasFocus()) {
                            Handler().postDelayed({
                                if (this::adapter.isInitialized && adapter.getLiveCurrentPositon() == 1) {
                                    adapter.buttonIntro?.requestFocus()
                                }
                            }, 100)
                        }
                    }
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {

                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {

                }

                else -> return super.onKeyDown(keyCode, event)
            }
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun getHomeFailed(e: Int) {
        Functions.showMessage(this, getString(e))
    }

    override fun getDetailFailed(e: Int) {
        Functions.showMessage(this, getString(e))
    }

    override fun sendListPopUpFail(erroMessage: String) {

    }

    @Suppress("SENSELESS_COMPARISON")
    private fun loadProduct(
        product: Product,
        position: Int,
        clickFrom: String,
        sectionName: String? = null,
        sectionIndex: String? = null,
        sectionType: String? = null,
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

    private fun getBrandShop(list: ArrayList<BrandShopModel.LayoutSection>): Int? {
        if (Config.homeData != null) {
            for (i in 0 until list.size) {
                if (list[i].sectionValue == "brand_shop" && list[i].brand_shop.size > 0)
                    return i
            }
        }

        return null
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
                dataObject.targetUid = data.landing_page.uid
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
                    dataObject1.targetUid = data.landing_page.uid
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
                gotoLandingPage(data.landing_page.image_cover)
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

    override fun sendProductSuccess(product: Product) {
        loadProduct(product, 0, QuickstartPreferences.CLICK_FROM_PRODUCT_APP_POP_UP_BRANDSHOP)
    }

    private fun loadProduct(
        product: Product,
        position: Int,
        clickFrom: String,
    ) {
        gotoProductDetail(
            product,
            position,
            clickFrom
        )
        presenter.loadAddHistory(product.uid)
    }
}

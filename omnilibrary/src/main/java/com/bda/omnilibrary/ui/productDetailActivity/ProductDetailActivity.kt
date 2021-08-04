package com.bda.omnilibrary.ui.productDetailActivity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.DetailProductAdapter.DetailAdapter
import com.bda.omnilibrary.adapter.checkout.ProductCheckoutAdapter
import com.bda.omnilibrary.dialog.*
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.REQUEST_CUSTOMER_RESULT_CODE
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher.ChooseVoucherFragment
import com.bda.omnilibrary.ui.liveStreamActivity.paymentMethod.PaymentMethodFragment
import com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream.QuickPayLiveStreamFragment
import com.bda.omnilibrary.ui.liveStreamActivity.successOrder.SuccessFragment
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlin.random.Random


class ProductDetailActivity : BaseActivity(),
    ProductDetailContact.View {

    private var product: Product? = null
    private var productId: String? = null
    private var voucher: Voucher? = null
    private var listRelativeProduct = ArrayList<Product>()
    private lateinit var presenter: ProductDetailContact.Presenter
    private lateinit var preferencesHelper: PreferencesHelper
    private var productPosition = 0
    private var onGotoCartClick = false
    private var onKeyDown: OnKeyDownListener? = null
    private var isFavourite = false
    private var clickFrom: String? = null
    private var sectionName: String? = null
    private var sectionType: String? = null
    private var sectionIndex: String? = null
    private var quickPayInfo: CheckCustomerResponse? = null
    private lateinit var qAdapter: ProductCheckoutAdapter
    private var voucherCode = ""
    private var voucherUid = ""
    private var voucherValue = 0.0

    companion object {
        var productDetailActivity: ProductDetailActivity? = null
    }

    private var detailAdapter: DetailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        setLayoutId(R.id.ln_product)
        productDetailActivity = this
        preferencesHelper = PreferencesHelper(this)

        dummy_view.setOnClickListener {
            // todo nothing
        }

        this.presenter =
            ProductDetailPresenter(
                this,
                this
            )
        loadProduct(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        loadProduct(intent)
    }

    private fun loadProduct(intent: Intent?) {
        val bundle = intent?.extras
        if (!bundle!!.isEmpty) {
            productId =
                bundle.getString(QuickstartPreferences.PRODUCT_ID)
            productPosition = bundle.getInt("PRODUCT_POSITION")
            bundle.getString("CLICK_FROM")?.let {
                clickFrom = it
            }

            bundle.getString("SECTION_NAME")?.let {
                sectionName = it
            }

            bundle.getString("SECTION_INDEX")?.let {
                sectionIndex = it
            }

            bundle.getString("SECTION_TYPE")?.let {
                sectionType = it
            }


            if (productId == null) {
                product = Gson().fromJson(
                    bundle.getString(QuickstartPreferences.PRODUCT_MODEL),
                    Product::class.java
                )
                loadDataDetail()
            } else {
                if (productId != null) {
                    presenter.callApiProduct(productId!!)
                }
            }
        } else {
            Functions.showMessage(this, getString(R.string.error))
            finish()
        }
    }


    private fun loadDataDetail() {
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
        dataObject.itemName = product?.name.toString()
        product?.let {
            if (it.collection.size >= 1) {
                dataObject.itemCategoryId = it.collection[0].uid
                dataObject.itemCategoryName = it.collection[0].collection_name
            }
        }
        dataObject.clickFrom = clickFrom
        dataObject.sectionName = sectionName
        dataObject.sectionType = sectionType
        dataObject.sectionIndex = sectionIndex
        dataObject.itemIndex = productPosition.toString()
        dataObject.itemId = product?.uid.toString()
        dataObject.itemBrand = product?.brand?.name
        dataObject.itemListPriceVat = product?.price.toString()
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_PRODUCT_DETAIL_v2,
            data
        )
        if (getCheckCustomerResponse() != null) {
            presenter.callVoucher(product!!.uid, getCheckCustomerResponse()?.data?.uid)
            presenter.loadFavourite(getCheckCustomerResponse()?.data?.uid!!, product!!.uid)
        }
        presenter.callApiRelative(product!!.uid)

    }

    private fun initial() {
        setData()
    }

    override fun sendProductSuccess(product: Product) {
        this.product = product
        loadDataDetail()
    }

    private fun setData() {
        detailAdapter =
            DetailAdapter(
                this,
                product!!,
                listRelativeProduct
            )
        onKeyDown = detailAdapter
        detailAdapter?.setOnItemClickListener(object : DetailAdapter.OnItemClickListener {
            override fun onOtherItemClick(
                @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") productRelative: Product,
                position: Int
            ) {
                this@ProductDetailActivity.product = productRelative
                loadMusic()
                if (Functions.checkInternet(this@ProductDetailActivity)) {
                    presenter.callApiRelative(product!!.uid)
                    productPosition = position

                    val dataObject = LogDataRequest()
                    dataObject.itemName = product?.name.toString()
                    dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.sectionName = QuickstartPreferences.CLICK_RELATED_PRODUCT
                    dataObject.itemIndex = productPosition.toString()
                    dataObject.itemId = product?.uid.toString()
                    dataObject.itemBrand = product?.brand?.name
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.LOAD_PRODUCT_DETAIL_v2,
                        data
                    )

                } else {
                    Functions.showMessage(
                        this@ProductDetailActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onQuickBuyClick() {
                if (product != null && !product!!.is_disabled_cod) {
                    if (getCheckCustomerResponse() != null) {
                        onGotoCartClick = true
                        val dataObject = LogDataRequest()
                        dataObject.itemName = product?.name.toString()
                        product?.let {
                            if (it.collection.size >= 1) {
                                dataObject.itemCategoryId = it.collection[0].uid
                                dataObject.itemCategoryName = it.collection[0].collection_name
                            }
                        }
                        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                        dataObject.itemIndex = productPosition.toString()
                        dataObject.itemId = product?.uid.toString()
                        dataObject.itemBrand = product?.brand?.name
                        dataObject.itemListPriceVat = product?.price.toString()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.CLICK_QUICKPAY_BUTTON_v2,
                            data
                        )

                        //gotoQuickPayActivity(product, CLICK_QUICK_PAY_FROM_PRODUCT_DETAIL)
                        showQuickPay()
                    } else {
                        gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                    }
                } else {
                    Functions.showMessage(
                        this@ProductDetailActivity,
                        String.format(
                            getString(R.string.only_online),
                            product!!.display_name_detail
                        )
                    )
                }
            }

            override fun onVideoCoverClick(video: Product.MediaType, position: Int) {
                val dataObject = LogDataRequest()
                dataObject.attachmentType = video.mediaType
                dataObject.attachmentUrl = video.url
                dataObject.itemName = product?.name.toString()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                product?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.itemId = product?.uid.toString()
                dataObject.itemBrand = product?.brand?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.PLAY_VIDEO_COVER_v2,
                    data
                )
                gotoMediaProduct(product!!, 0)
            }

            override fun onFavouriteClick() {
                if (getCheckCustomerResponse() != null) {
                    val dataObject = LogDataRequest()
                    if (isFavourite) {
                        dataObject.value = "Remove wishlist"
                    } else {
                        dataObject.value = "Add wishlist"
                    }

                    dataObject.itemName = product?.name.toString()
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.itemId = product?.uid.toString()
                    dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                    dataObject.itemBrand = product?.brand?.name
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_WISHLIST_BUTTON_v2,
                        data
                    )
                    if (isFavourite) {
                        if (detailAdapter != null) {
                            if (getCheckCustomerResponse() != null) {
                                detailAdapter?.setTextFavourite(getString(R.string.wish_list))
                                isFavourite = false
                                presenter.postDeleteFavourite(
                                    FavouriteResquest(
                                        getCheckCustomerResponse()?.data?.uid!!,
                                        product!!.uid
                                    )
                                )
                            }
                        }
                    } else {
                        if (detailAdapter != null && getCheckCustomerResponse() != null) {
                            if (getCheckCustomerResponse() != null) {
                                detailAdapter?.setTextFavourite(
                                    getString(R.string.aldready_favourite)
                                )
                                isFavourite = true
                                presenter.postAddFavourite(
                                    FavouriteResquest(
                                        getCheckCustomerResponse()?.data?.uid!!,
                                        product!!.uid
                                    )
                                )
                            }
                        }
                    }
                } else {
                    gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                }
            }

            override fun onBackToHeadClick() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                dataObject.itemName = product?.name
                product?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.itemId = product?.uid
                dataObject.itemBrand = product?.brand?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_BACK_TO_TOP_BUTTON_v2,
                    data
                )
                rv_detail.scrollToPosition(0)
            }

            override fun onPolicyClick() {
                gotoCodWarrantyActivity()
            }

            override fun onStoreClick() {
                StoreDialog(this@ProductDetailActivity, product!!)
            }

            override fun onReviewClick() {

            }

            override fun onAddToCartClick() {
                if (getCheckCustomerResponse() != null) {
                    onGotoCartClick = false
                    loadGoToCart(onGotoCartClick)

                    val dataObject = LogDataRequest()
                    val dbProduct = getDatabaseHandler()?.getLProductList()
                    var mTotalMoney = 0.0
                    dbProduct?.let { list ->
                        for (i in 0 until list.size) {
                            mTotalMoney += (list[i].price * list[i].order_quantity)
                        }
                    }
                    dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                    dataObject.itemId = product?.uid
                    dataObject.itemName = product?.name
                    dataObject.cartValue = mTotalMoney.toString()
                    dataObject.cartTotalItem = dbProduct?.size.toString()
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_ADD_TO_CART_BUTTON_v2,
                        data
                    )
                } else {
                    gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                }
            }

            override fun onBackClick() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                dataObject.itemName = product?.name
                product?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.itemId = product?.uid
                dataObject.itemBrand = product?.brand?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_BACK_TO_HOME_BUTTON_v2,
                    data
                )
                onBackPressed()
            }

            override fun onSearchClick() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEARCH_MENU_v2,
                    data
                )
                gotoSearchActivity()
            }

            override fun onNotAvailableClick() {
                DetailProductNotAvailableDialog(this@ProductDetailActivity)
            }

            override fun onVideoClick(video: Product.MediaType, position: Int) {
                if (Functions.checkInternet(this@ProductDetailActivity)) {
                    //gotoVideo(video.url)
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                    dataObject.attachmentType = video.mediaType
                    dataObject.attachmentUrl = video.url
                    dataObject.itemName = product?.name.toString()
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.itemId = product?.uid.toString()
                    dataObject.itemBrand = product?.brand?.name
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_RELATED_ATTACHMENT_v2,
                        data
                    )
                    gotoMediaProduct(product!!, position)
                } else {
                    Functions.showMessage(
                        this@ProductDetailActivity,
                        getString(R.string.no_internet)
                    )
                }
            }

            override fun onImageClick(
                @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") listMediaType: ArrayList<Product.MediaType>,
                position: Int
            ) {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                dataObject.attachmentType = listMediaType[0].mediaType
                dataObject.attachmentUrl = listMediaType[0].url
                dataObject.itemName = product?.name.toString()
                product?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.itemId = product?.uid.toString()
                dataObject.itemBrand = product?.brand?.name
                dataObject.itemListPriceVat = product?.price.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_RELATED_ATTACHMENT_v2,
                    data
                )
//                ImageViewDialog(this@ProductDetailActivity, listMediaType, position)
            }

            override fun onSpecificationClick(spec: Product.Detail) {

            }
        })

        setTextFavourite(isFavourite)
        if (getCheckCustomerResponse() != null) {
            Handler().postDelayed({
                val popUpRequest = PopUpRequest(
                    getCheckCustomerResponse()!!.data.uid,
                    Config.popup_boxid,
                    Config.SCREEN_ID.PRODUCT_DETAIL.toString()
                )
                presenter.loadPopup(popUpRequest)
            }, 1000)
        }
        rv_detail.extraLayoutSpace = 800
        rv_detail.setItemViewCacheSize(30)
        rv_detail.adapter = detailAdapter

    }

    private fun loadGoToCart(onGotoCartClick: Boolean) {
        addItemToCart(product!!)

        if (onGotoCartClick) {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
            dataObject.itemName = product?.name.toString()
            product?.let {
                if (it.collection.size >= 1) {
                    dataObject.itemCategoryId = it.collection[0].uid
                    dataObject.itemCategoryName = it.collection[0].collection_name
                }
            }
            dataObject.itemId = product?.uid.toString()
            dataObject.itemBrand = product?.brand?.name
            dataObject.itemListPriceVat = product?.price.toString()
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CART_BUTTON_v2,
                data
            )
            gotoCart()
        } else {
            DetailConfirmDialog(this@ProductDetailActivity, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                dataObject.popUpName = "Add to cart succeed pop-up"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_CART_BUTTON_v2,
                    data
                )
                gotoCart()
            }, {
                val dataObject = LogDataRequest()
                dataObject.itemName = product?.name.toString()
                product?.let {
                    if (it.collection.size >= 1) {
                        dataObject.itemCategoryId = it.collection[0].uid
                        dataObject.itemCategoryName = it.collection[0].collection_name
                    }
                }
                dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                dataObject.itemIndex = productPosition.toString()
                dataObject.itemId = product?.uid.toString()
                dataObject.itemBrand = product?.brand?.name
                dataObject.itemListPriceVat = product?.price.toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_CONTINUE_SHOPPING_BUTTON_v2,
                    Gson().toJson(dataObject).toString()
                )
                finish()
            })
        }
    }

    var mLastKeyDownTime = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val current = System.currentTimeMillis()
        return if (current - mLastKeyDownTime < Config.lastKeyDownTime) {
            true
        } else {
            val f = getCurrentFragment()

            if (f != null && ln_product.visibility == View.VISIBLE
                && (f is ChooseVoucherFragment)
            ) {
                return if (f.myOnkeyDown(keyCode, event))
                    true
                else
                    return super.onKeyDown(keyCode, event)
            }

            mLastKeyDownTime = current
            if (onKeyDown != null) {
                onKeyDown?.onKeyDown(keyCode, event)
            }
            super.onKeyDown(keyCode, event)
        }
    }

    private fun getCurrentFragment() = getFManager().findFragmentById(layoutToLoadId())

    override fun sendSplashUIFail(erroMessage: String) {
        Functions.showMessage(this, erroMessage)
    }

    override fun sendSplashRelativeFail(erroMessage: String) {
        initial()
    }

    override fun sendSplashRelativeSuccess(list: ArrayList<Product>) {
        listRelativeProduct = list
        initial()
    }

    override fun sendProductFail() {
        finish()
    }

    override fun sendVoucher(voucher: Voucher) {
        this.voucher = voucher

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
        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
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
            dataObject1.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
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
            logTrackingVersion2(
                QuickstartPreferences.CLOSE_POPUP_v2,
                Gson().toJson(dataObject1).toString()
            )
        })
    }

    private fun loadDestinationPopUp(data: PopUpResponse.ResultPopup) {
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
        dataObject.popUpUid = data.uid
        dataObject.targetType = data.popup_type

        when (data.popup_type) {
            Config.POPUP_TYPE.PRODUCT.toString() -> {
                dataObject.targetUid = data.popup_product.uid
            }
            Config.POPUP_TYPE.COLLECTION_TEMP.toString() -> {
                dataObject.targetUid = data.popup_collection_temp.uid
                gotoCollection(
                    data.popup_collection_temp.uid,
                    data.popup_collection_temp.name
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

    override fun sendLoadFavouriteSuccess(response: FavouriteResponse) {
        isFavourite = response.checkStatus
        setTextFavourite(isFavourite)
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {
        quickPayInfo = data
    }

    private var isShowQuickPay = false
    private fun showQuickPay() {
        isShowQuickPay = true

        //Functions.faceIn(dim)
        //Functions.bounceInLeft(ln_product)

        // Prepare the View for the animation
        loadFragment(
            QuickPayLiveStreamFragment.newInstance(product!!),
            layoutToLoadId(),
            true
        )

        ln_product.visibility = View.VISIBLE
        ln_product.translationX = resources.getDimension(R.dimen._153sdp)

        ln_product.alpha = 0.0f

        // Start the animation
        ln_product.animate()
            .translationX(0f)
            .alpha(1.0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    //edt_phone.requestFocus()

                    dim.visibility = View.VISIBLE
                    Functions.faceIn(dim)

                    val f = getCurrentFragment()
                    if (f != null)
                        (getCurrentFragment() as LiveStreamBaseFragment).requestFocus()
                }
            })
    }

    override fun focusDummyView() {
        Handler().postDelayed({
            dummy_view.requestFocus()
        }, 0)
    }

    /**
     * hide quick pay
     */
    override fun hideDetailProduct() {
        isShowQuickPay = false
        getFManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        ln_product.animate().translationX(ln_product.width.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    ln_product.visibility = View.INVISIBLE

                    //detailAdapter?._bn_buy_now
                    //detailAdapter?._rl_text_bn_buy_now
                    dim.visibility = View.GONE
                    detailAdapter?._bn_buy_now?.requestFocus()
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

    private fun setTextFavourite(isFav: Boolean) {
        if (isFav) {
            if (detailAdapter != null) {
                detailAdapter?.setTextFavourite(getString(R.string.aldready_favourite))
            }
        } else {
            if (detailAdapter != null) {
                detailAdapter?.setTextFavourite(getString(R.string.wish_list))
            }
        }
    }

    interface OnKeyDownListener {
        fun onKeyDown(keyCode: Int, event: KeyEvent)
    }

    override fun onResume() {
        loadMusic()
        detailAdapter?.notifyItemChanged(0)
        super.onResume()
    }

    private fun loadMusic() {
        if (product != null && product!!.videos.isNotEmpty()) {
            MainActivity.getMaiActivity()?.pauseMusicBackground()
        } else {
            if (MainActivity.getMaiActivity() != null && !MainActivity.getMaiActivity()!!
                    .checkIsPlayingBackground()
            ) {
                MainActivity.getMaiActivity()?.playMusicBackground()
            }
        }
    }

    override fun onBackPressed() {

        if (isShowQuickPay) {
            focusDummyView()
            val f = getCurrentFragment()

            if (f is SuccessFragment)
                hideDetailProduct()

            if (getFManager().backStackEntryCount > 0 || getCurrentFragment() is PaymentMethodFragment) {

                getFManager().popBackStack()

            } else {
                hideDetailProduct()
            }

        } else {
            if (detailAdapter != null) {
                detailAdapter?.releaseVideo()
            }
            Handler().postDelayed({
                super.onBackPressed()
            }, 100)
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in getFManager().fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}

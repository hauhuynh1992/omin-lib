package com.bda.omnilibrary.ui.categoryDetail

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.collection.CollectionHeaderNameAdapter
import com.bda.omnilibrary.adapter.homev2.ProductAdapterV2
import com.bda.omnilibrary.dialog.PopUpDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_category_detail.*
import kotlinx.android.synthetic.main.item_activity_header.view.*
import kotlinx.android.synthetic.main.item_header_screen.view.*
import kotlinx.android.synthetic.main.item_header_screen.view.bn_account
import kotlinx.android.synthetic.main.item_header_screen.view.bn_cart
import kotlinx.android.synthetic.main.item_header_screen.view.bn_search
import java.lang.ref.WeakReference
import kotlin.random.Random

class CategoryDetailActivity : BaseActivity(), CategoryDetailContract.View {
    private var collectionNameAdapter: CollectionHeaderNameAdapter? = null
    private var productAdapter: ProductAdapterV2? = null
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var presenter: CategoryDetailContract.Presenter
    private lateinit var parent_uid: String
    private lateinit var parent_name: String
    private var sectionName: String? = null
    private var sectionIndex: String? = null
    private var sectionType: String? = null
    private var brandShopId = ""
    private var brandShopName = ""

    private var listProduct = ArrayList<Product>()

    private var length = 30
    private var page = 0
    private val THRESHOLD = 6
    private var isLoading = false
    private var collection_uid = ""
    private var isLoadMore = false
    private var msHandler: Handler = Handler()
    private var hasNewArrival = false

    companion object {
        private var weakActivity: WeakReference<CategoryDetailActivity>? = null

        fun getActivity(): CategoryDetailActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_detail)

        parent_uid = intent.getStringExtra("COLLECTION_PARENT_CODE").toString()
        parent_name = intent.getStringExtra("COLLECTION_PARENT_NAME").toString()
        sectionIndex = intent.getStringExtra("SECTION_INDEX")
        sectionName = intent.getStringExtra("SECTION_NAME")
        sectionType = intent.getStringExtra("SECTION_TYPE")
        brandShopId = intent.getStringExtra("BRAND_SHOP_ID").toString()
        brandShopName = intent.getStringExtra("BRAND_SHOP_NAME").toString()

        weakActivity = WeakReference(this@CategoryDetailActivity)

        initChildHeader(image_header)
        image_header.bn_header_back.nextFocusRightId = image_header.bn_search.id

        initial()
    }

    private fun initial() {
        preferencesHelper = PreferencesHelper(this)
        tv_breadcrumb.text = parent_name
        presenter = CategoryDetailPresenter(this, this)
        if (brandShopId != "") {
            presenter.getSubCollectionBrandShop(brandShopId, parent_uid)
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.BRAND_SHOP_CATEGORY_DETAIL.name
            dataObject.brandshopUid = brandShopId
            dataObject.brandshopName = brandShopId
            dataObject.categoryId = parent_uid
            dataObject.categoryId = parent_uid
            dataObject.category = parent_name
            dataObject.sectionIndex = sectionIndex
            dataObject.sectionName = sectionName
            dataObject.sectionType = sectionType
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.LOAD_CATEGORY_DETAILS_v2,
                data
            )
        } else {
            presenter.getSubCollectionHome(parent_uid)
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CATEGORY_DETAIL.name
            dataObject.categoryId = parent_uid
            dataObject.categoryId = parent_uid
            dataObject.category = parent_name
            dataObject.sectionIndex = sectionIndex
            dataObject.sectionName = sectionName
            dataObject.sectionType = sectionType
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.LOAD_CATEGORY_DETAILS_v2,
                data
            )
        }
        if (getCheckCustomerResponse() != null) {
            Handler().postDelayed({
                val popUpRequest =
                    PopUpRequest(
                        getCheckCustomerResponse()!!.data.uid,
                        Config.popup_boxid,
                        Config.SCREEN_ID.COLLECTION.toString()
                    )
                presenter.loadPopup(popUpRequest)
            }, 1000)
        }
    }

    override fun sendCollectionsHomeSuccess(subCollection: SubCollection) {
        tv_breadcrumb.text = subCollection.collectionName
        if (subCollection.data.size > 0) {
            progressBar2.visibility = View.GONE
            loadSubCollectionHome(subCollection)
        }
    }

    override fun sendProductsSuccess(model: ProductMoreModel) {
        progressBar2.visibility = View.GONE
        rv_product.visibility = View.VISIBLE
        collectionNameAdapter?.isCallThisCallBack = true
        loadDataIntoList(model)
    }

    override fun sendProductSuccess(product: Product) {
        gotoProductDetail(
            product,
            0,
            QuickstartPreferences.CLICK_FROM_PRODUCT_APP_POP_UP_COLLECTION
        )
        presenter.loadAddHistory(product.uid)
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
        dataObject.screen =
            Config.SCREEN_ID.BRAND_SHOP_CATEGORY_DETAIL.name.takeIf { brandShopId != "" }
                ?: Config.SCREEN_ID.CATEGORY_DETAIL.name
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
            dataObject1.screen =
                Config.SCREEN_ID.BRAND_SHOP_CATEGORY_DETAIL.name.takeIf { brandShopId != "" }
                    ?: Config.SCREEN_ID.CATEGORY_DETAIL.name
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
        dataObject.screen =
            Config.SCREEN_ID.BRAND_SHOP_CATEGORY_DETAIL.name.takeIf { brandShopId != "" }
                ?: Config.SCREEN_ID.CATEGORY_DETAIL.name
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

    override fun sendFalsed(message: String) {
        progressBar2.visibility = View.GONE
        isLoading = false
        Functions.showMessage(this, message)
    }

    private fun loadDataIntoList(cart: ProductMoreModel) {

        listProduct.addAll(cart.data)

        if (productAdapter == null) {
            isLoadMore = true
            productAdapter = ProductAdapterV2(this, listProduct, 0, false)
            productAdapter?.setOnItemClickListener(object : ProductAdapterV2.OnItemClickListener {
                override fun onItemClick(product: Product, position: Int) {
                    gotoProductDetail(
                        product,
                        position,
                        parent_name
                    )

                    if (brandShopId != "") {
                        presenter.loadAddHistoryBrandShop(product.uid, brandShopId)
                    } else {
                        presenter.loadAddHistory(product.uid)
                    }

                }

                override fun onPositionListFocus(
                    product: Product?,
                    positionEnterList: Int,
                    position: Int,
                ) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CATEGORY_CHILD_TAB.name
                    @Suppress("SENSELESS_COMPARISON")
                    if (brandShopId != null && brandShopId.isNotEmpty()) {
                        dataObject.brandshopUid = brandShopId
                        dataObject.brandshopName = brandShopName
                    }
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.collectionTempName = parent_name
                    dataObject.collectionTempUid = parent_uid
                    dataObject.itemName = product?.name
                    dataObject.itemIndex = position.toString()
                    dataObject.itemId = product?.uid.toString()
                    dataObject.itemBrand = product?.brand?.name
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.HOVER_PRODUCT_v2,
                        data
                    )
                }

                override fun onPositionLiveListFocus(
                    product: Product?,
                    positionLiveList: Int,
                    position: Int,
                ) {
                }

                override fun onClickWatchMore(uid: String, name: String) {
                }

            })

            rv_product.addOnChildViewHolderSelectedListener(object :
                OnChildViewHolderSelectedListener() {
                override fun onChildViewHolderSelected(
                    parent: RecyclerView,
                    child: RecyclerView.ViewHolder,
                    position: Int,
                    subposition: Int,
                ) {
                    super.onChildViewHolderSelected(parent, child, position, subposition)
                    if (position != RecyclerView.NO_POSITION) {
                        @Suppress("SENSELESS_COMPARISON")
                        if (parent != null) {
                            parent.layoutManager?.let { layoutManager ->
                                if (layoutManager != null && isLoadMore) {
                                    if (position > layoutManager.itemCount - 1 - THRESHOLD) {
                                        if (!isLoading) {
                                            page += 1
                                            callApiLoadMoreProduct(page)
                                            isLoading = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })

            rv_product.setNumColumns(3)
            rv_product.adapter = productAdapter
        } else {
            if (cart.data.size < length) {
                productAdapter?.notifyDataSetChanged()

            } else {
                productAdapter?.notifyDataSetChanged()
                isLoading = false
            }
        }
    }

    private fun loadSubCollectionHome(subCollection: SubCollection) {
        val listRemove = ArrayList<Collections>()
        if (subCollection.data.size > 0) {
            for (i in 0 until subCollection.data.size) {
                if (subCollection.data[i].count_items == 0) {
                    listRemove.add(subCollection.data[i])
                }
            }

            for (i in 0 until listRemove.size) {
                subCollection.data.remove(listRemove[i])
            }

            hasNewArrival = brandShopId == ""
            collectionNameAdapter =
                CollectionHeaderNameAdapter(
                    this,
                    createDataSet(subCollection.data, hasNewArrival, subCollection.hotdeal)
                )

            list_collection.setItemViewCacheSize(20)
            list_collection.hasFixedSize()
            list_collection.adapter = collectionNameAdapter
            collectionNameAdapter?.setOnItemClickListener(object :
                CollectionHeaderNameAdapter.OnItemClickListener {

                override fun onItemFocus(
                    collection: Collections,
                    position: Int,
                ) {
                    msHandler.removeCallbacksAndMessages(null)

                    progressBar2.visibility = View.VISIBLE
                    rv_product.visibility = View.GONE

                    msHandler.postDelayed({
                        collectionNameAdapter?.isCallThisCallBack = false
                        collectionNameAdapter?.currentSelectedPosition = position
                        collectionNameAdapter?.notifyDataSetChanged()

                        if (collection.type != null && collection.type == CollectionType.HOT_DEAL) {
                            val dataObject = LogDataRequest()
                            dataObject.categoryId = parent_uid
                            dataObject.category = parent_name

                            @Suppress("SENSELESS_COMPARISON")
                            if (brandShopId != null && brandShopId.isNotEmpty()) {
                                dataObject.brandshopUid = brandShopId
                                dataObject.brandshopName = brandShopName
                            }
                            val data = Gson().toJson(dataObject).toString()
                            logTrackingVersion2(
                                QuickstartPreferences.LOAD_DEAL_CATEGORY_v2,
                                data
                            )
                            resetData()
                            showProductWithoutLoadMore(collection.products)
                        } else if (collection.type != null && collection.type == CollectionType.NEW_ARRIVAL) {
                            resetData()
                            this@CategoryDetailActivity.hasNewArrival = true
                            callApiLoadMoreProduct(page)
                        } else {
                            resetData()
                            collection_uid = collection.uid
                            callApiLoadMoreProduct(page)

                            val dataObject = LogDataRequest()

                            @Suppress("SENSELESS_COMPARISON")
                            if (brandShopId != null && brandShopId.isNotEmpty()) {
                                dataObject.brandshopUid = brandShopId
                                dataObject.brandshopName = brandShopName
                            }
                            dataObject.categoryId = parent_uid
                            dataObject.category = parent_name
                            dataObject.childCategory = collection.collection_name
                            dataObject.childCategoryId = collection.uid
                            dataObject.childCategoryIndex = position.toString()
                            val data = Gson().toJson(dataObject).toString()
                            logTrackingVersion2(
                                QuickstartPreferences.LOAD_CHILD_CATEGORY_DETAILS_v2,
                                data
                            )
                        }
                    }, 500)
                }
            })

            Handler().postDelayed({
                list_collection.requestFocus()
            }, 100)
        }

        if (subCollection.hotdeal.products.size > 0 && !hasNewArrival) {
            showProductWithoutLoadMore(subCollection.hotdeal.products)

            isLoadMore = false
        }
    }

    private fun showProductWithoutLoadMore(list: ArrayList<Product>) {
        Handler().postDelayed({
            collectionNameAdapter?.isCallThisCallBack = true
        }, 100)

        if (list.size > 0) {
            progressBar2.visibility = View.GONE
            rv_product.visibility = View.VISIBLE

            productAdapter = ProductAdapterV2(this, list, 0, false)

            productAdapter!!.setOnItemClickListener(object :
                ProductAdapterV2.OnItemClickListener {
                override fun onItemClick(product: Product, position: Int) {
                    gotoProductDetail(
                        product,
                        position,
                        parent_name,
                        isPopup = false
                    )
                }

                override fun onPositionListFocus(
                    product: Product?,
                    positionEnterList: Int,
                    position: Int,
                ) {
                    val dataObject = LogDataRequest()
                    @Suppress("SENSELESS_COMPARISON")
                    if (brandShopId != null && brandShopId.isNotEmpty()) {
                        dataObject.brandshopUid = brandShopId
                        dataObject.brandshopName = brandShopName
                    }
                    dataObject.screen = Config.SCREEN_ID.CATEGORY_DEAL_TAB.name
                    dataObject.collectionTempName = parent_name
                    dataObject.collectionTempUid = parent_uid
                    dataObject.itemName = product?.name
                    product?.let {
                        if (it.collection.size >= 1) {
                            dataObject.itemCategoryId = it.collection[0].uid
                            dataObject.itemCategoryName = it.collection[0].collection_name
                        }
                    }
                    dataObject.itemIndex = position.toString()
                    dataObject.itemId = product?.uid.toString()
                    dataObject.itemBrand = product?.brand?.name
                    dataObject.itemListPriceVat = product?.price.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.HOVER_PRODUCT_v2,
                        data
                    )
                }

                override fun onPositionLiveListFocus(
                    product: Product?,
                    positionLiveList: Int,
                    position: Int,
                ) {
                }

                override fun onClickWatchMore(uid: String, name: String) {
                }
            })

            rv_product.setNumColumns(3)
            rv_product.adapter = productAdapter
        }
    }

    private fun callApiLoadMoreProduct(currentpage: Int) {
        @Suppress("SENSELESS_COMPARISON")
        if (hasNewArrival) {
            presenter.getNewArrivalProducts(
                parent_uid,
                currentpage,
                length
            )
        } else if (collection_uid != null && collection_uid.isNotEmpty()) {
            if (brandShopId != "") {
                presenter.loadProductMoreBrandShop(
                    ProductBrandShopMoreRequestModel(
                        collection_uid,
                        brandShopId,
                        length,
                        currentpage
                    )
                )
            } else {
                presenter.loadProductMore(
                    ProductMoreRequestModel(
                        collection_uid,
                        length,
                        currentpage
                    )
                )
            }
        }
    }

    private fun createDataSet(
        list: ArrayList<Collections>,
        hasNewArrival: Boolean,
        hotdeal: Collections,
    ): ArrayList<Collections> {
        if (hasNewArrival) {
            val newArrival = Collections()
            newArrival.collection_name = getString(R.string.new_arrival)
            newArrival.type = CollectionType.NEW_ARRIVAL
            list.add(0, newArrival)
        }

        if (hotdeal.products.size > 0) {
            hotdeal.type = CollectionType.HOT_DEAL
            hotdeal.collection_name = getString(R.string.hot)
            list.add(0, hotdeal)
        }

        return list
    }

    private fun resetData() {
        productAdapter = null
        page = 0
        listProduct.clear()
        isLoading = false
        hasNewArrival = false
    }

    var mLastKeyDownTime = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val current = System.currentTimeMillis()
        if (current - mLastKeyDownTime < Config.lastKeyDownTime) {
            return true
        } else {
            mLastKeyDownTime = current

            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    if (rv_product.childCount > 0 && rv_product.hasFocus()) {
                        when (rv_product.childCount) {
                            1 -> {
                                if (rv_product[0].hasFocus()) {
                                    Handler().postDelayed({
                                        list_collection.requestFocus()
                                        changeFocusView(true)
                                    }, 0)
                                }
                            }

                            2 -> {
                                if (rv_product[0].hasFocus() || rv_product[1].hasFocus()) {
                                    Handler().postDelayed({
                                        list_collection.requestFocus()
                                        changeFocusView(true)
                                    }, 0)
                                }
                            }

                            else -> {
                                if (productAdapter != null && productAdapter!!.getCurrentFocusPosition() <= 3
                                    && productAdapter!!.getCurrentFocusPosition() >= 0
                                ) {
                                    if (rv_product[0].hasFocus() || rv_product[1].hasFocus()
                                        || rv_product[2].hasFocus()
                                    ) {
                                        Handler().postDelayed({
                                            list_collection.requestFocus()
                                            changeFocusView(true)
                                        }, 0)
                                    }
                                }
                            }
                        }
                    }

                    if (list_collection.hasFocus()) {
                        changeFocusView(false)
                    }
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (image_header.bn_header_back.hasFocus() || image_header.bn_search.hasFocus()
                        || image_header.bn_account.hasFocus() || image_header.bn_cart.hasFocus()
                    ) {
                        Handler().postDelayed({
                            list_collection.requestFocus()
                            changeFocusView(true)
                        }, 0)
                    } else {
                        changeFocusView(false)
                    }
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (rv_product.size > 0 && rv_product[rv_product.childCount - 1].hasFocus()) {
                        return true
                    }
                }
            }
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun changeFocusView(bool: Boolean) {
        collectionNameAdapter?.isFocusName = bool
    }

    override fun onResume() {
        super.onResume()
        reloadTotalMoney(image_header)
        if (MainActivity.getMaiActivity() != null && !MainActivity.getMaiActivity()!!
                .checkIsPlayingBackground()
        ) {
            MainActivity.getMaiActivity()?.playMusicBackground()
        }
    }

    override fun onBackPressed() {
        rv_product?.let {
            if (it.hasFocus()) {
                Handler().postDelayed({
                    list_collection.requestFocus()
                    changeFocusView(true)
                }, 0)
            } else {
                super.onBackPressed()
            }
        }
    }
}
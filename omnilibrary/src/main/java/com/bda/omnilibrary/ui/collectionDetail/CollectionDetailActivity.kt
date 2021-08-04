package com.bda.omnilibrary.ui.collectionDetail

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
import com.bda.omnilibrary.adapter.collection.CollectionTempHeaderNameAdapter
import com.bda.omnilibrary.adapter.homev2.ProductAdapterV2
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.ChildDetails
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_category_detail.*
import kotlinx.android.synthetic.main.activity_collection_detail.*
import kotlinx.android.synthetic.main.activity_collection_detail.image_header
import kotlinx.android.synthetic.main.activity_collection_detail.list_collection
import kotlinx.android.synthetic.main.activity_collection_detail.progressBar2
import kotlinx.android.synthetic.main.activity_collection_detail.rv_product
import kotlinx.android.synthetic.main.activity_collection_detail.tv_breadcrumb
import kotlinx.android.synthetic.main.item_header_screen.view.*
import java.lang.ref.WeakReference

class CollectionDetailActivity : BaseActivity(), CollectionDetailContract.View {
    private lateinit var loadMreNameAdapter: CollectionTempHeaderNameAdapter
    private var productAdapter: ProductAdapterV2? = null
    private lateinit var presenter: CollectionDetailContract.Presenter
    private lateinit var parentUid: String
    private lateinit var parentName: String
    private var sectionName: String? = null
    private var sectionIndex: String? = null
    private var sectionType: String? = null
    private var brandShopId = ""
    private var brandShopName = ""

    private var listProduct = ArrayList<Product>()

    private val keys =
        arrayListOf("best_seller", "all", "new_arrived", "price_inc", "price_desc")
    private lateinit var names: ArrayList<String>

    private var key = keys[0]
    private var length = 30
    private var page = 0
    private val THRESHOLD = 6
    private var isLoading = false
    private var isLoadMore = false
    private var msHandler: Handler = Handler()

    companion object {
        private var weakActivity: WeakReference<CollectionDetailActivity>? = null

        fun getActivity(): CollectionDetailActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        names = arrayListOf(getString(R.string.text_ban_chay), getString(R.string.text_tat_ca),
            getString(R.string.text_hang_moi), getString(R.string.text_gia_tang_dan),
            getString(R.string.text_gia_giam_dan))

        weakActivity = WeakReference(this@CollectionDetailActivity)

        parentUid = intent.getStringExtra("COLLECTION_PARENT_CODE").toString()
        parentName = intent.getStringExtra("COLLECTION_PARENT_NAME").toString()
        sectionIndex = intent.getStringExtra("SECTION_INDEX")
        sectionName = intent.getStringExtra("SECTION_NAME")
        sectionType = intent.getStringExtra("SECTION_TYPE")
        brandShopId = intent.getStringExtra("BRAND_SHOP_ID").toString()
        brandShopName = intent.getStringExtra("BRAND_SHOP_NAME").toString()
        initChildHeader(image_header)
        image_header.bn_header_back.nextFocusRightId = image_header.bn_search.id

        initial()
    }

    private fun initial() {
        tv_breadcrumb.text = parentName
        presenter = CollectionDetailPresenter(this, this)

        if (brandShopId != "") {
            presenter.getBrandShopChildCollection(parentUid, key, length, page)
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.BRAND_SHOP_COLLECTION_DETAIL.name
            dataObject.brandshopUid = brandShopId
            dataObject.brandshopName = brandShopName
            dataObject.collectionId = parentUid
            dataObject.collectionName = parentName
            dataObject.sectionIndex = sectionIndex
            dataObject.sectionName = sectionName
            dataObject.sectionType = sectionType
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.LOAD_COLLECTION_DETAILS_v2,
                data
            )

        } else {
            presenter.getCollectionProduct(parentUid, key, length, page)
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.COLLECTION_DETAIL.name
            dataObject.collectionId = parentUid
            dataObject.collectionName = parentName
            dataObject.sectionIndex = sectionIndex
            dataObject.sectionName = sectionName
            dataObject.sectionType = sectionType
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.LOAD_COLLECTION_DETAILS_v2,
                data
            )

        }
        loadMreNameAdapter =
            CollectionTempHeaderNameAdapter(this, names)
        list_collection.setItemViewCacheSize(20)
        list_collection.hasFixedSize()
        list_collection.adapter = loadMreNameAdapter
        loadMreNameAdapter.setOnItemClickListener(object :
            CollectionTempHeaderNameAdapter.OnItemClickListener {

            override fun onItemFocus(index: Int) {
                msHandler.removeCallbacksAndMessages(null)
                progressBar2.visibility = View.VISIBLE
                rv_product.visibility = View.GONE
                msHandler.postDelayed({
                    resetData()
                    this@CollectionDetailActivity.key = keys[index]
                    if (brandShopId != "") {
                        presenter.getBrandShopChildCollection(parentUid, key, length, page)
                        val dataObject = LogDataRequest()
                        dataObject.categoryId = parentUid
                        dataObject.brandshopUid = brandShopId
                        dataObject.brandshopName = brandShopName
                        dataObject.category = parentName
                        dataObject.tabName = names[index]
                        dataObject.tabIndex = index.toString()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.LOAD_TAB_ORDERBY_COLLECTION_DETAILS_v2,
                            data
                        )

                    } else {
                        presenter.getCollectionProduct(
                            parentUid,
                            this@CollectionDetailActivity.key,
                            length,
                            page
                        )
                        val dataObject = LogDataRequest()
                        dataObject.categoryId = parentUid
                        dataObject.category = parentName
                        dataObject.tabName = names[index]
                        dataObject.tabIndex = index.toString()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.LOAD_TAB_ORDERBY_COLLECTION_DETAILS_v2,
                            data
                        )
                    }

                    loadMreNameAdapter.isCallThisCallBack = false
                    loadMreNameAdapter.currentSelectedPosition = index
                    loadMreNameAdapter.notifyDataSetChanged()

                }, 500)
            }
        })
    }

    override fun sendFalsed(message: String) {
        progressBar2.visibility = View.GONE
        isLoading = false
        Functions.showMessage(this, message)
    }

    override fun sendSuccessProducts(model: ChildDetails) {
        tv_breadcrumb.text = model.data.collection_name
        loadMreNameAdapter.isCallThisCallBack = true
        listProduct.addAll(model.data.products)

        if (productAdapter == null) {
            if (model.data.products.size > 0) {
                isLoadMore = true
                progressBar2.visibility = View.GONE
                rv_product.visibility = View.VISIBLE

                productAdapter = ProductAdapterV2(this, listProduct, 0, false)

                productAdapter!!.setOnItemClickListener(object :
                    ProductAdapterV2.OnItemClickListener {
                    override fun onItemClick(product: Product, position: Int) {
                        gotoProductDetail(
                            product,
                            position,
                            parentName,
                            isPopup = false
                        )
                    }

                    override fun onPositionListFocus(
                        product: Product?,
                        positionEnterList: Int,
                        position: Int,
                    ) {
                        Log.d("AAA", "onPositionListFocus 2")
                        val dataObject = LogDataRequest()
                        if (brandShopId.isNotBlank()) {
                            dataObject.brandshopUid = brandShopId
                            dataObject.brandshopName = brandShopName
                        }
                        dataObject.screen = Config.SCREEN_ID.CATEGORY_DEAL_TAB.name
                        dataObject.collectionTempName = parentName
                        dataObject.collectionTempUid = parentUid
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
                                                //loadProduct(page)
                                                if (brandShopId != "") {
                                                    presenter.getBrandShopChildCollection(
                                                        parentUid,
                                                        key,
                                                        length,
                                                        page
                                                    )

                                                } else {
                                                    presenter.getCollectionProduct(
                                                        parentUid,
                                                        key,
                                                        length,
                                                        page
                                                    )

                                                }

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
            }
        } else {
            if (model.data.products.size < length) {
                productAdapter?.notifyDataSetChanged()

            } else {
                productAdapter?.notifyDataSetChanged()
                isLoading = false
            }
        }
    }

    private fun resetData() {
        productAdapter = null
        page = 0
        listProduct.clear()
        isLoading = false
    }

    private var mLastKeyDownTime = 0L
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
        loadMreNameAdapter.isFocusName = bool
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
}
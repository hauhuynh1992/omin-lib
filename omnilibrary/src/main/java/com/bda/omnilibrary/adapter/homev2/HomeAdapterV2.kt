package com.bda.omnilibrary.adapter.homev2

import android.annotation.SuppressLint
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.adapter.DotsAdapter
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.REQUEST_CUSTOMER_RESULT_CODE
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.DotsIndicator
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_item_v2.view.*
import kotlinx.android.synthetic.main.item_activity_header.view.*
import kotlinx.android.synthetic.main.item_footer.view.*
import java.util.*
import kotlin.collections.ArrayList

class HomeAdapterV2(
    activity: BaseActivity,
    list: List<HomeModel.LayoutSection>
) : BaseAdapter(activity) {

    private var mList: List<HomeModel.LayoutSection> = list
    private lateinit var productAdapter: ProductAdapterV2

    private lateinit var collectionAdapterV2: CollectionAdapterV2

    private lateinit var liveAdapter: LiveAdapter
    private lateinit var promotionAdapter: PromotionAdapterV2
    private lateinit var hotStoreAdapter: HotStoreAdapter

    private lateinit var bestSellerAdapter: BestSellerAdapter

    private lateinit var singleProductAdapter: SingleProductAdapter
    private lateinit var hybridAdapter: HybridSliderAdapter

    private lateinit var clickListener: OnCallBackListener

    private var tvQuantity: SfTextView? = null
    private var price: SfTextView? = null
    private var phone: SfTextView? = null
    private var focusPosition = 1
    private var liveFocusPosition = -1
    private var tv_order_date: SfTextView? = null

    private var hybrid: ArrayList<HybridSection>? = null

    var currentPage = 0
    private lateinit var timer: Timer
    val DELAY_MS = 500L//delay in milliseconds before task is to be executed
    val PERIOD_MS = 7000L

    init {
        if (list.isNotEmpty() && list[0].sectionType == "hybrid_section" && list[0].hybrid_section.size > 0) {
            hybrid = list[0].hybrid_section
            mList = list.subList(1, list.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return when (viewType) {
            R.layout.item_footer -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_footer, parent, false)
                FooterItemViewHolder(v)
            }
            R.layout.item_activity_header -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_activity_header, parent, false)
                HeaderItemViewHolder(v)
            }
            else -> {
                val sharedPool = RecyclerView.RecycledViewPool()
                v = getLayoutInflater()
                    .inflate(R.layout.home_item_v2, parent, false)
                v.rv_sub_home.setRecycledViewPool(sharedPool)
                ItemViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_activity_header
            (itemCount - 1) -> R.layout.item_footer
            else -> R.layout.home_item_v2
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        if (mholder is ItemViewHolder && mList.isNotEmpty()) {
            val holder: ItemViewHolder = mholder
            holder.sub_dots.visibility = View.GONE
            when (mList[position - 1].sectionValue) {
                "promotions" -> {
                    if (mList[position - 1].promotions.size > 0
                    ) {
                        holder.tv_sub_home.visibility = View.VISIBLE

                        if (mList[position - 1].sectionName != "") {
                            holder.tv_sub_home.text = mList[position - 1].sectionName
                        }

                        this.promotionAdapter =
                            PromotionAdapterV2(
                                mActivity,
                                mList[position - 1].promotions,
                                position
                            )

                        this.promotionAdapter.setOnItemClickListener(object :
                            PromotionAdapterV2.OnItemClickListener {
                            override fun onItemClick(
                                promotion: Promotion,
                                position: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.HOME.name
                                dataObject.promotionId = promotion.uid
                                dataObject.promotionName = promotion.display_name
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.CLICK_PROMOTION,
                                    data
                                )

                                clickListener.onPromotionClick(
                                    promotion,
                                    position,
                                    QuickstartPreferences.CLICK_PROMOTION
                                )
                            }

                            override fun onPositionListFocus(
                                promotion: Promotion,
                                positionOnList: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.HOME.name
                                dataObject.promotionId = promotion.uid
                                dataObject.promotionName = promotion.display_name
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_PROMOTION,
                                    data
                                )
                                if (positionOnList != focusPosition) {
                                    focusPosition = positionOnList
                                }
                            }

                            override fun onPositionLiveListFocus(
                                product: Promotion,
                                positionLiveList: Int
                            ) {
                                if (positionLiveList != liveFocusPosition) {
                                    liveFocusPosition = positionLiveList
                                }
                            }
                        })
                        holder.rv_sub_home.setPadding(
                            mActivity.resources.getDimension(R.dimen._20sdp).toInt(),
                            0,
                            mActivity.resources.getDimension(R.dimen._20sdp).toInt(),
                            0
                        )
                        holder.rv_sub_home.adapter = promotionAdapter
                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                    }
                }
                "livestream" -> {
                    if (mList[position - 1].livestream.size > 0
                    ) {
                        holder.tv_sub_home.visibility = View.VISIBLE
                        if (mList[position - 1].sectionName != "") {
                            holder.tv_sub_home.text = mList[position - 1].sectionName
                        }
                        var dotAdapter: DotsAdapter? = null
                        if (mList[position - 1].layoutType == LiveAdapter.LIVESTREAM_LAYOUT_SINGLE) {
                            val listDot = mList[position - 1].livestream.map {
                                "none"
                            }
                            dotAdapter = DotsAdapter(mActivity, listDot as ArrayList<String>)
                            holder.sub_dots.visibility = View.VISIBLE
                            holder.sub_dots.layoutManager = LinearLayoutManager(
                                mActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            holder.sub_dots.adapter = dotAdapter
                        }
                        this.liveAdapter =
                            LiveAdapter(
                                mActivity,
                                //todo change
                                mList[position - 1].livestream,
                                position,
                                mList[position - 1].layoutType
                            )

                        this.liveAdapter.setOnItemClickListener(object :
                            LiveAdapter.OnItemClickListener {
                            override fun onItemClick(
                                product: LiveStream,
                                position: Int
                            ) {
                                if (mActivity.getCheckCustomerResponse() != null) {
                                    val dataObject = LogDataRequest()
                                    dataObject.screen = Config.SCREEN_ID.HOME.name
                                    dataObject.liveStreamId = product.uid
                                    dataObject.liveStreamName = product.name
                                    val data = Gson().toJson(dataObject).toString()
                                    mActivity.logTrackingVersion2(
                                        QuickstartPreferences.CLICK_LIVESTREAM,
                                        data
                                    )

                                    clickListener.onLiveStreamClick(
                                        product,
                                        position,
                                        QuickstartPreferences.CLICK_LIVESTREAM
                                    )
                                } else {
                                    mActivity.gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                                }

                            }

                            override fun onPositionListFocus(
                                product: LiveStream,
                                mPosition: Int,
                                positionOnList: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.HOME.name
                                dataObject.liveStreamId = product.uid
                                dataObject.liveStreamName = product.name
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_LIVESTREAM,
                                    data
                                )
                                if (mList[position - 1].layoutType == LiveAdapter.LIVESTREAM_LAYOUT_SINGLE) {
                                    dotAdapter?.update(mPosition)
                                }
                                if (positionOnList != focusPosition) {
                                    focusPosition = positionOnList
                                }
                            }

                            override fun onPositionLiveListFocus(positionLiveList: Int) {
                                if (positionLiveList != liveFocusPosition) {
                                    liveFocusPosition = positionLiveList
                                }
                            }
                        })
                        holder.rv_sub_home.setPadding(
                            mActivity.resources.getDimension(R.dimen._20sdp).toInt(),
                            0,
                            mActivity.resources.getDimension(R.dimen._20sdp).toInt(),
                            0
                        )
                        holder.rv_sub_home.adapter = liveAdapter
                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                    }
                }
                "collection" -> {
                    if (mList[position - 1].collections.size > 0) {
                        holder.tv_sub_home.visibility = View.VISIBLE
                        holder.tv_sub_home.text = mList[position - 1].sectionName

                        this.collectionAdapterV2 =
                            CollectionAdapterV2(
                                mActivity,
                                mList[position - 1].collections,
                                position
                            )

                        this.collectionAdapterV2.setOnItemClickListener(object :
                            CollectionAdapterV2.OnItemClickListener {
                            override fun onItemClick(
                                collection: SimpleCollection, mPosition: Int
                            ) {
                                clickListener.onCollectionClick(
                                    collection,
                                    mPosition,
                                    sectionName = mList[position - 1].sectionName,
                                    sectionIndex = (position - 1).toString(),
                                    sectionValue = mList[position - 1].sectionValue
                                )
                            }

                            override fun onPositionListFocus(
                                collections: SimpleCollection,
                                positionOnList: Int,
                                mPosition: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.HOME.name
                                dataObject.layoutId = Config.homeData?.data?.name
                                dataObject.categoryId = collections.uid
                                dataObject.category = collections.collection_name
                                dataObject.categoryIndex = mPosition.toString()
                                dataObject.sectionName = mList[position - 1].sectionName
                                dataObject.sectionType = mList[position - 1].sectionType
                                dataObject.sectionIndex = (position - 1).toString()
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_CATEGORY_v2,
                                    data
                                )

                                if (positionOnList != focusPosition) {
                                    focusPosition = positionOnList
                                }
                            }

                            override fun onPositionLiveListFocus(positionLiveList: Int) {
                                if (positionLiveList != liveFocusPosition) {
                                    liveFocusPosition = positionLiveList
                                }
                            }
                        })
                        holder.rv_sub_home.adapter = collectionAdapterV2

                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                    }
                }
                "collection_temp" -> {
                    if (mList[position - 1].collectionTemp.items.size > 0) {
                        holder.tv_sub_home.visibility = View.VISIBLE

                        holder.tv_sub_home.text =
                            mList[position - 1].collectionTemp.collection_name
                        when (mList[position - 1].collectionTemp.layout_type) {
                            "0" -> {
                                this.singleProductAdapter =
                                    SingleProductAdapter(
                                        mActivity,
                                        mList[position - 1].collectionTemp.items,
                                        position
                                    )
                                val listDot = mList[position - 1].collectionTemp.items.map {
                                    "none"
                                }
                                val dotAdapter =
                                    DotsAdapter(mActivity, listDot as ArrayList<String>)
                                holder.sub_dots.visibility = View.VISIBLE
                                holder.sub_dots.layoutManager = LinearLayoutManager(
                                    mActivity,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                holder.sub_dots.adapter = dotAdapter
                                this.singleProductAdapter.setOnItemClickListener(object :
                                    SingleProductAdapter.OnItemClickListener {
                                    override fun onItemClick(
                                        product: Product,
                                        mPosition: Int
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionType = mList[position - 1].sectionType,
                                            sectionIndex = (position - 1).toString()
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        product: Product,
                                        positionEnterList: Int,
                                        mPosition: Int
                                    ) {
                                        dotAdapter.update(mPosition)
                                        if (positionEnterList != focusPosition) {
                                            focusPosition = positionEnterList

                                        }
                                    }

                                    override fun onPositionLiveListFocus(positionLiveList: Int) {
                                        if (positionLiveList != liveFocusPosition) {
                                            liveFocusPosition = positionLiveList
                                        }
                                    }
                                })

                                holder.rv_sub_home.adapter = singleProductAdapter
                            }
                            "2" -> {
                                this.bestSellerAdapter =
                                    BestSellerAdapter(
                                        mActivity,
                                        mList[position - 1].collectionTemp.items,
                                        position
                                    )

                                this.bestSellerAdapter.setOnItemClickListener(object :
                                    BestSellerAdapter.OnItemClickListener {
                                    override fun onItemClick(
                                        product: Product,
                                        mPosition: Int
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionType = mList[position - 1].sectionType,
                                            sectionIndex = (position - 1).toString()
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        product: Product,
                                        positionEnterList: Int,
                                        position: Int
                                    ) {
                                        val dataObject = LogDataRequest()
                                        dataObject.screen = Config.SCREEN_ID.HOME.name
                                        if (product.collection.size >= 1) {
                                            dataObject.category =
                                                product.collection[0].collection_name
                                            dataObject.categoryId = product.collection[0].uid
                                            dataObject.itemCategoryId = product.collection[0].uid
                                            dataObject.itemCategoryName =
                                                product.collection[0].collection_name
                                        }

                                        dataObject.collectionTempName =
                                            mList[position - 1].collectionTemp.collection_name
                                        dataObject.collectionTempUid =
                                            mList[position - 1].collectionTemp.uid
                                        dataObject.sectionName = mList[position - 1].sectionName
                                        dataObject.sectionType = mList[position - 1].sectionType
                                        dataObject.sectionIndex = (position - 1).toString()
                                        dataObject.itemName = product.name
                                        dataObject.itemIndex = positionEnterList.toString()
                                        dataObject.itemId = product.uid
                                        dataObject.itemBrand = product.brand.name
                                        dataObject.itemListPriceVat = product.price.toString()
                                        val data = Gson().toJson(dataObject).toString()
                                        mActivity.logTrackingVersion2(
                                            QuickstartPreferences.HOVER_PRODUCT_v2,
                                            data
                                        )
                                        if (positionEnterList != focusPosition) {
                                            focusPosition = positionEnterList
                                        }
                                    }


                                    override fun onPositionLiveListFocus(positionLiveList: Int) {
                                        if (positionLiveList != liveFocusPosition) {
                                            liveFocusPosition = positionLiveList

                                            //                                        val dataObject = LogDataRequest()
                                            //                                        dataObject.screen = Config.SCREEN_ID.HOME.name
                                            //                                        dataObject.categoryId =
                                            //                                            mList[position - 1].collectionTemp.uid
                                            //                                        dataObject.category =
                                            //                                            mList[position - 1].collectionTemp.collection_name
                                            //                                        dataObject.categoryIndex = positionLiveList.toString()
                                            //                                        val data = Gson().toJson(dataObject).toString()
                                            //                                        (mActivity as MainActivity)?.logTrackingVersion2(
                                            //                                            QuickstartPreferences.HOVER_PRODUCT_v2,
                                            //                                            data
                                            //                                        )
                                        }
                                    }
                                })

                                holder.rv_sub_home.adapter = bestSellerAdapter
                            }
                            else -> {
                                this.productAdapter =
                                    ProductAdapterV2(
                                        mActivity,
                                        mList[position - 1].collectionTemp.items,
                                        position,
                                        true,
                                        mList[position - 1].collectionTemp.uid,
                                        mList[position - 1].collectionTemp.collection_name
                                    )

                                this.productAdapter.setOnItemClickListener(object :
                                    ProductAdapterV2.OnItemClickListener {
                                    override fun onItemClick(
                                        product: Product,
                                        mPosition: Int
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionType = mList[position - 1].sectionType,
                                            sectionIndex = (position - 1).toString()
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        product: Product?,
                                        positionOnList: Int,
                                        mFocusPosition: Int
                                    ) {
                                        val dataObject = LogDataRequest()
                                        dataObject.screen = Config.SCREEN_ID.HOME.name
                                        product?.let {
                                            if (it.collection.size >= 1) {
                                                dataObject.category =
                                                    it.collection[0].collection_name
                                                dataObject.categoryId = it.collection[0].uid
                                                dataObject.itemCategoryId = it.collection[0].uid
                                                dataObject.itemCategoryName =
                                                    product.collection[0].collection_name
                                            }
                                        }
                                        dataObject.collectionTempName =
                                            mList[position - 1].collectionTemp.collection_name
                                        dataObject.collectionTempUid =
                                            mList[position - 1].collectionTemp.uid
                                        dataObject.sectionName = mList[position - 1].sectionName
                                        dataObject.sectionType = mList[position - 1].sectionType
                                        dataObject.sectionIndex = (position - 1).toString()
                                        dataObject.itemName = product?.name
                                        dataObject.itemIndex = mFocusPosition.toString()
                                        dataObject.itemId = product?.uid.toString()
                                        dataObject.itemBrand = product?.brand?.name
                                        dataObject.itemListPriceVat = product?.price.toString()
                                        val data = Gson().toJson(dataObject).toString()
                                        (mActivity as MainActivity).logTrackingVersion2(
                                            QuickstartPreferences.HOVER_PRODUCT_v2,
                                            data
                                        )
                                        if (positionOnList != focusPosition) {
                                            focusPosition = positionOnList
                                        }
                                    }

                                    override fun onPositionLiveListFocus(
                                        product: Product?,
                                        positionLiveList: Int,
                                        mFocusPosition: Int
                                    ) {
                                        if (positionLiveList != liveFocusPosition) {
                                            liveFocusPosition = positionLiveList
                                        }
                                    }

                                    override fun onClickWatchMore(uid: String, name: String) {
                                        clickListener.onMoreClick(
                                            name,
                                            uid,
                                            mList[position - 1].sectionName,
                                            (position - 1).toString(),
                                            mList[position - 1].sectionType
                                        )
                                    }
                                })

                                holder.rv_sub_home.adapter = productAdapter
                            }
                        }
                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                    }
                }
                "viewed_prod", "favourite", "buylater" -> {
                    var list = ArrayList<Product>()
                    when (mList[position - 1].sectionValue) {
                        "viewed_prod" -> {
                            list = mList[position - 1].viewed_prod
                        }
                        "favourite" -> {
                            list = mList[position - 1].favourite
                        }
                        "buylater" -> {
                            list = mList[position - 1].buylater
                        }
                    }
                    if (list.size > 0) {
                        holder.tv_sub_home.visibility = View.VISIBLE
                        holder.tv_sub_home.text = mList[position - 1].sectionName
                        this.productAdapter =
                            ProductAdapterV2(
                                mActivity,
                                list,
                                position,
                                false
                            )

                        this.productAdapter.setOnItemClickListener(object :
                            ProductAdapterV2.OnItemClickListener {
                            override fun onItemClick(
                                product: Product,
                                mPosition: Int
                            ) {
                                clickListener.onItemClick(
                                    product,
                                    mPosition,
                                    mList[position - 1].sectionName,
                                    sectionName = mList[position - 1].sectionName,
                                    sectionType = mList[position - 1].sectionType,
                                    sectionIndex = (position - 1).toString()
                                )
                            }

                            override fun onPositionListFocus(
                                product: Product?,
                                positionOnList: Int,
                                mFocusPosition: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.HOME.name
                                product?.let {
                                    if (it.collection.size >= 1) {
                                        dataObject.category = it.collection[0].collection_name
                                        dataObject.categoryId = it.collection[0].uid
                                        dataObject.itemCategoryId = it.collection[0].uid
                                        dataObject.itemCategoryName =
                                            it.collection[0].collection_name
                                    }
                                }

                                dataObject.collectionTempName =
                                    mList[position - 1].collectionTemp.collection_name
                                dataObject.collectionTempUid =
                                    mList[position - 1].collectionTemp.uid
                                dataObject.sectionName = mList[position - 1].sectionName
                                dataObject.sectionType = mList[position - 1].sectionType
                                dataObject.sectionIndex = (position - 1).toString()
                                dataObject.itemName = product?.name

                                dataObject.itemIndex = mFocusPosition.toString()
                                dataObject.itemId = product?.uid.toString()
                                dataObject.itemBrand = product?.brand?.name
                                dataObject.itemListPriceVat = product?.price.toString()
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_PRODUCT_v2,
                                    data
                                )

                                if (positionOnList != focusPosition) {
                                    focusPosition = positionOnList
                                }
                            }

                            override fun onPositionLiveListFocus(
                                product: Product?,
                                positionLiveList: Int,
                                mFocusPosition: Int
                            ) {
                                if (positionLiveList != liveFocusPosition) {
                                    liveFocusPosition = positionLiveList
                                }
                            }

                            override fun onClickWatchMore(uid: String, name: String) {
                            }
                        })
                        holder.rv_sub_home.adapter = productAdapter
                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                        holder.itemView.background = null
                    }
                }
                "brand_shop" -> {
                    holder.tv_sub_home.visibility = View.VISIBLE
                    holder.tv_sub_home.text = mList[position - 1].sectionName

                    this.hotStoreAdapter =
                        HotStoreAdapter(
                            mActivity,
                            mList[position - 1].brand_shop,
                            position
                        )

                    this.hotStoreAdapter.setOnItemClickListener(object :
                        HotStoreAdapter.OnItemClickListener {
                        override fun onItemClick(product: BrandShop, mPosition: Int) {
                            clickListener.onShopItemClick(
                                product,
                                mPosition,
                                sectionName = mList[position - 1].sectionName,
                                sectionIndex = (position - 1).toString(),
                                sectionType = mList[position - 1].sectionType
                            )
                        }

                        override fun onPositionListFocus(
                            product: BrandShop,
                            mPosition: Int,
                            positionEnterList: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.HOME.name
                            dataObject.layoutId = Config.homeData?.data?.name
                            dataObject.brandshopUid = product.uid
                            dataObject.brandshopName = product.name
                            dataObject.sectionName = mList[position - 1].sectionName
                            dataObject.sectionType = mList[position - 1].sectionType
                            dataObject.sectionIndex = (position - 1).toString()
                            val data = Gson().toJson(dataObject).toString()
                            mActivity.logTrackingVersion2(
                                QuickstartPreferences.HOVER_BRANDSHOP_v2,
                                data
                            )

                            if (positionEnterList != focusPosition) {
                                focusPosition = positionEnterList
                            }
                        }

                        override fun onPositionLiveListFocus(positionLiveList: Int) {
                            if (positionLiveList != liveFocusPosition) {
                                liveFocusPosition = positionLiveList
                            }
                        }

                        override fun onFocusItem(brandShop: BrandShop, index: Int) {
                            //clickListener.onFocusItem(brandShop, index)
                        }

                        override fun onLostFocusItem(brandShop: BrandShop) {
                            //clickListener.onLostFocusItem(brandShop)
                        }

                    })

                    holder.rv_sub_home.adapter = hotStoreAdapter
                }

                else -> {

                }
            }

        } else if (mholder is FooterItemViewHolder) {
            mholder.itemView.setOnClickListener {
                clickListener.onOnClickFooter()
            }

            mholder.itemView.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    mholder.ic_arrow.setImageDrawable(
                        ContextCompat.getDrawable(
                            mActivity,
                            R.mipmap.ic_mui_ten_white
                        )
                    )
                    mholder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                    mholder.rl_con.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.background_button_active)

                    focusPosition = position

                } else {
                    mholder.ic_arrow.setImageDrawable(
                        ContextCompat.getDrawable(
                            mActivity,
                            R.mipmap.ic_mui_ten
                        )
                    )
                    mholder.name.setTextColor(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.start_color
                        )
                    )
                    mholder.rl_con.background = ContextCompat.getDrawable(
                        mActivity,
                        R.drawable.background_collection_v2_default
                    )

                    liveFocusPosition = position
                }
            }
        } else if (mholder is HeaderItemViewHolder) {
            if (hybrid != null && hybrid!!.size > 0) {
                hybridAdapter = HybridSliderAdapter(mActivity, hybrid!!)
                mholder.homeSlider.offscreenPageLimit = hybridAdapter.count
                mholder.homeSlider.adapter = hybridAdapter

                if (hybrid!!.size == 1) {
                    mholder.dots.visibility = View.GONE
                } else {
                    mholder.dots.attachViewPager(mholder.homeSlider)
                    mholder.dots.setDotDrawable(
                        R.drawable.ic_indicator_next,
                        R.drawable.ic_indicator_forward,
                        R.drawable.ic_unselected_indicator
                    )
                }

                mholder.homeSlider.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        Functions.setStrokeFocus(mholder.rl_imageSlider, mActivity)
                        createTimer(mholder.homeSlider, mholder.dots)

                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.HOME.name
                        dataObject.layoutId = Config.homeData?.data?.name
                        dataObject.hybridUid = hybrid!![mholder.homeSlider.currentItem].uid
                        dataObject.hybridUidSectionName =
                            hybrid!![mholder.homeSlider.currentItem].sectionName
                        dataObject.hybridUidSectionValue =
                            hybrid!![mholder.homeSlider.currentItem].sectionValue
                        dataObject.hybridUidSectionRef =
                            hybrid!![mholder.homeSlider.currentItem].sectionRef
                        dataObject.hybridUidSectionType =
                            hybrid!![mholder.homeSlider.currentItem].sectionType
                        val data = Gson().toJson(dataObject).toString()
                        mActivity.logTrackingVersion2(
                            QuickstartPreferences.HOVER_HYBRID_v2,
                            data
                        )

                    } else {
                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()
                        }
                        Functions.setLostFocus(mholder.rl_imageSlider, mActivity)
                    }
                }

                mholder.homeSlider.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                        mholder.dots.changedByTimer = false

                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()

                            if (mholder.homeSlider.currentItem == 0) {
                                mholder.dots.changedByTimer = false
                                smoothScrollTo(mholder.homeSlider, mholder.dots, hybrid!!.size - 1)
                                return@OnKeyListener true
                            }

                            createTimer(mholder.homeSlider, mholder.dots)
                        }
                        Handler().postDelayed({ currentPage = mholder.homeSlider.currentItem }, 10)
                        return@OnKeyListener false
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                        mholder.dots.changedByTimer = true

                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()

                            if (mholder.homeSlider.currentItem == hybrid!!.size - 1) {
                                mholder.dots.changedByTimer = true
                                smoothScrollTo(mholder.homeSlider, mholder.dots, 0)
                                return@OnKeyListener true
                            }

                            createTimer(mholder.homeSlider, mholder.dots)
                        }
                        Handler().postDelayed({ currentPage = mholder.homeSlider.currentItem }, 10)
                        return@OnKeyListener false
                    }
                    false
                })

                mholder.homeSlider.setOnClickListener {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.HOME.name
                    dataObject.layoutId = Config.homeData?.data?.name
                    dataObject.hybridUid = hybrid!![mholder.homeSlider.currentItem].uid
                    dataObject.hybridUidSectionName =
                        hybrid!![mholder.homeSlider.currentItem].sectionName
                    dataObject.hybridUidSectionValue =
                        hybrid!![mholder.homeSlider.currentItem].sectionValue
                    dataObject.hybridUidSectionRef =
                        hybrid!![mholder.homeSlider.currentItem].sectionRef
                    dataObject.hybridUidSectionType =
                        hybrid!![mholder.homeSlider.currentItem].sectionType
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_HYBRID_v2,
                        data
                    )
                    clickListener.onHybridSection(
                        hybrid!![mholder.homeSlider.currentItem],
                        position
                    )
                }


            } else {
                mholder.rl_imageSlider.visibility = View.GONE

                mholder.image_top.post {
                    val params = mholder.image_top.layoutParams
                    params.height = mActivity.resources.getDimensionPixelSize(R.dimen._225sdp)
                    mholder.image_top.layoutParams = params
                }
            }

            tvQuantity = mholder.txt_quantity
            price = mholder.txt_header_total
            phone = mholder.txt_phone
            tv_order_date = mholder.text_order
            if (arrayListOf(
                    "box2019",
                    "box2020",
                    "omnishopeu",
                    "box2021"
                ).contains(Config.platform)
            ) {
                mholder.bn_assistant.visibility = View.VISIBLE
                val l = mholder.text_order.layoutParams
                (l as RelativeLayout.LayoutParams).addRule(
                    RelativeLayout.START_OF,
                    mholder.bn_assistant.id
                )
                mholder.text_order.layoutParams = l
            }

            if (mActivity.getCheckCustomerResponse() != null) {
                if (mActivity.getCheckCustomerResponse()!!.data.phone.isNotBlank())
                    mholder.txt_phone.text = mActivity.getCheckCustomerResponse()!!.data.phone
                else {
                    mholder.txt_phone.visibility = View.GONE
                }
            } else {
                mholder.txt_phone.text = mActivity.getString(R.string.dang_nhap)
            }

            val d = mActivity.getDatabaseHandler()!!.getLProductList()
            mholder.txt_quantity.text = d.size.toString()
            mholder.txt_header_total.text = mActivity.getTotalMoney(d)

            mholder.ic_account_top.setColorFilter(
                ContextCompat.getColor(
                    mActivity,
                    R.color.title_gray_C4C4C4
                )
            )

            cartBn = mholder.bn_cart
            mholder.bn_cart.setOnClickListener {
                if (mActivity.getCheckCustomerResponse() != null) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.HOME.name
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_CART_BUTTON_v2,
                        data
                    )
                    mActivity.gotoCart()
                } else {
                    mActivity.gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                }
            }


            accountBn = mholder.bn_account
            mholder.bn_account.setOnClickListener {
                if (mActivity.getCheckCustomerResponse() != null) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.HOME.name
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_ACCOUNT_BUTTON_v2,
                        data
                    )
                    mActivity.gotoAccount(false)
                } else {
                    mActivity.gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
                }
            }

            assistantBn = mholder.bn_assistant
            mholder.bn_assistant.setOnClickListener {
                clickListener.onClickVoiceAssitance()
            }

            searchBn = mholder.bn_search
            mholder.bn_search.setOnClickListener {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.HOME.name
                val data = Gson().toJson(dataObject).toString()
                mActivity.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEARCH_MENU_v2,
                    data
                )
                mActivity.gotoSearchActivity()
            }

            /////////////////////// setOnFocusChangeListener ////////////////////

            mholder.bn_account.setOnFocusChangeListener { _, hasFocus ->
                mholder.txt_phone.isSelected = hasFocus
                mholder.txt_account.isSelected = hasFocus
                if (hasFocus) {
                    mholder.ic_account_top.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.title_white
                        )
                    )
                    setFocusPositionToHideImage()
                } else {
                    mholder.ic_account_top.setColorFilter(
                        ContextCompat.getColor(
                            mActivity,
                            R.color.title_gray_C4C4C4
                        )
                    )
                }
            }

            mholder.bn_cart.setOnFocusChangeListener { _, hasFocus ->
                mholder.txt_header_total.isSelected = hasFocus
                mholder.txt_cart.isSelected = hasFocus
                mholder.txt_quantity.isSelected = hasFocus

                if (hasFocus) {
                    mholder.image_cart.setImageResource(R.mipmap.ic_style_1_cart_white)
                    setFocusPositionToHideImage()
                } else {
                    mholder.image_cart.setImageResource(R.mipmap.ic_style_1_cart_gray)
                }
            }

            mholder.bn_assistant.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setFocusPositionToHideImage()
                } else {
                }

                mholder.txt_assistant.isSelected = hasFocus
                mholder.txt_assistant_name.isSelected = hasFocus
            }

            mholder.bn_search.setOnFocusChangeListener { _, hasFocus ->
                mholder.txt_search.isSelected = hasFocus
                mholder.txt_search_product.isSelected = hasFocus

                if (hasFocus) {
                    setFocusPositionToHideImage()
                    mholder.ic_bn_search.setImageResource(R.mipmap.ic_search_white)
                } else {
                    mholder.ic_bn_search.setImageResource(R.mipmap.ic_search_gray)
                }
            }
        }
    }

    private fun createTimer(homeSlider: ViewPager, dotsIndicator: DotsIndicator) {
        val handler = Handler()
        val runnable = Runnable {
            dotsIndicator.changedByTimer = true
            if (currentPage == hybrid!!.size) {
                currentPage = 0
            }
            homeSlider.setCurrentItem(currentPage++, true)
        }

        timer = Timer() // This will create a new Thread
        timer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, DELAY_MS, PERIOD_MS)
    }

    private fun smoothScrollTo(slider: ViewPager, dots: DotsIndicator, position: Int) {
        currentPage = position
        slider.setCurrentItem(currentPage, true)
        createTimer(slider, dots)
        Handler().postDelayed({ currentPage = slider.currentItem }, 10)
    }

    private fun setFocusPositionToHideImage() {
        focusPosition = 1
    }

    fun getCurrentPositon(): Int {
        return focusPosition
    }


    fun updateOrderDateData(quantity: Int, supplier: String, date: Int) {
        if (quantity > 0) {
            tv_order_date?.let {
                it.text =
                    mActivity.getString(
                        R.string.text_order_date,
                        quantity.toString(),
                        supplier,
                        date.toString()
                    )
            }
            tv_order_date!!.text =
                mActivity.getString(
                    R.string.text_order_date,
                    quantity.toString(),
                    supplier,
                    date.toString()
                )
        } else {
            tv_order_date?.let {
                it.visibility = View.GONE
            }
        }
    }

    fun getLiveCurrentPositon(): Int {
        return liveFocusPosition
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_sub_home: HorizontalGridView = view.rv_sub_home
        var sub_dots: RecyclerView = view.sub_dots
        var tv_sub_home: SfTextView = view.tv_sub_home
        var layout_content_home: LinearLayout = view.layout_content_home
    }

    @Suppress("PropertyName")
    inner class FooterItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rl_con: RelativeLayout = view.rl_con
        var ic_arrow: AppCompatImageView = view.ic_arrow
        var name: SfTextView = view.name
    }

    private var assistantBn: LinearLayout? = null
    private var searchBn: LinearLayout? = null
    private var accountBn: LinearLayout? = null
    private var cartBn: LinearLayout? = null

    @Suppress("PropertyName")
    inner class HeaderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_assistant: LinearLayout = view.bn_assistant
        val txt_assistant: SfTextView = view.txt_assistant
        val txt_assistant_name: SfTextView = view.txt_assistant_name

        val bn_search: LinearLayout = view.bn_search
        val ic_bn_search: ImageView = view.ic_bn_search
        val txt_search: SfTextView = view.txt_search
        val txt_search_product: SfTextView = view.txt_search_product

        val bn_account: LinearLayout = view.bn_account
        val ic_account_top: ImageView = view.ic_account_top
        val txt_account: SfTextView = view.txt_account
        val txt_phone: SfTextView = view.txt_phone

        val bn_cart: LinearLayout = view.bn_cart
        val image_cart: ImageView = view.image_cart
        val txt_cart: SfTextView = view.txt_cart
        val txt_quantity: SfTextView = view.txt_quantity
        val txt_header_total: SfTextView = view.txt_header_total

        //val rv_sub_home_hybrid: HorizontalGridView = view.rv_sub_home_hybrid
        //val imageSlider: SliderView = view.imageSlider
        val homeSlider: ViewPager = view.homeSlider
        val dots: DotsIndicator = view.dots
        val rl_imageSlider: MaterialCardView = view.rl_imageSlider

        val image_top: RelativeLayout = view.image_top

        val text_order: SfTextView = view.text_order
    }

    fun hasFocusHeader(): Boolean {
        return (assistantBn != null && assistantBn!!.hasFocus())
                || (searchBn != null && searchBn!!.hasFocus())
                || (accountBn != null && accountBn!!.hasFocus())
                || (cartBn != null && cartBn!!.hasFocus())
    }

    fun setQuantity(text: String) {
        if (tvQuantity != null) {
            tvQuantity!!.text = text
        }
    }

    fun setPrice(text: String) {
        if (price != null) {
            price!!.text = text
        }
    }

    fun setPhone(text: String) {
        phone?.visibility = View.VISIBLE
        if (phone != null) {
            phone?.text = text
        } else {
            phone?.text = mActivity.getString(R.string.dang_nhap)
        }
    }

    override fun getItemCount(): Int {
        return mList.size + 2
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onItemClick(
            product: Product,
            position: Int,
            clickFrom: String,
            sectionName: String,
            sectionIndex: String,
            sectionType: String
        )

        fun onCollectionClick(
            collection: SimpleCollection,
            position: Int,
            sectionName: String,
            sectionIndex: String,
            sectionValue: String
        )

        fun onHaveProductOutsite(product: Product, position: Int)
        fun onMoreClick(
            subCollectionName: String,
            subcCollectionId: String,
            sectionName: String,
            sectionIndex: String,
            sectionType: String
        )

        fun onVoucherClick(voucher: Voucher, position: Int)
        fun onOnClickFooter()
        fun onLiveStreamClick(product: LiveStream, position: Int, clickFrom: String)
        fun onPromotionClick(promotion: Promotion, position: Int, clickFrom: String)
        fun onClickVoiceAssitance()
        fun onShopItemClick(
            product: BrandShop,
            position: Int,
            sectionName: String,
            sectionIndex: String,
            sectionType: String
        )

        fun onFocusHybridSection(section: HybridSection)

        fun onHybridSection(product: HybridSection, position: Int)
    }
}
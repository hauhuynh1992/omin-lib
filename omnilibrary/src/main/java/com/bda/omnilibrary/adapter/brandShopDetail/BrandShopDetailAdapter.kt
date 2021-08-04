package com.bda.omnilibrary.adapter.brandShopDetail

import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
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
import com.bda.omnilibrary.adapter.homev2.*
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.brandShopDetail.BrandShopDetailActivity
import com.bda.omnilibrary.ui.brandShopDetail.ProductBrandShopAdapterV2
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.DotsIndicator
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.home_item_v2.view.*
import kotlinx.android.synthetic.main.item_brand_shop_detail_header.view.*
import kotlinx.android.synthetic.main.item_footer.view.*
import kotlinx.android.synthetic.main.item_footer.view.name
import kotlinx.android.synthetic.main.item_header_screen.view.*
import java.util.*
import kotlin.collections.ArrayList

@Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
class BrandShopDetailAdapter(
    activity: BaseActivity,
    list: List<BrandShopModel.LayoutSection>,
    brandShop: BrandShop,
) : BaseAdapter(activity) {

    private var mList: List<BrandShopModel.LayoutSection> = list
    private var mBrandShop: BrandShop = brandShop
    private lateinit var productAdapter: ProductBrandShopAdapterV2

    private lateinit var collectionAdapterV2: CollectionAdapterV2

    private lateinit var liveAdapter: LiveAdapter
    private lateinit var promotionAdapter: PromotionAdapterV2

    private lateinit var bestSellerAdapter: BestSellerAdapter

    private lateinit var singleProductAdapter: SingleProductAdapter
    private lateinit var clickListener: OnCallBackListener

    private lateinit var hybridAdapter: HybridSliderAdapter
    private lateinit var imageAdapter: ImageSliderAdapter
    private var hybrid: ArrayList<HybridSection>? = null
    var currentPage = 0
    private lateinit var timer: Timer
    val DELAY_MS = 500L//delay in milliseconds before task is to be executed
    val PERIOD_MS = 7000L

    init {
        if (list[0].sectionType == "hybrid_section" && list[0].hybrid_section.size > 0) {
            hybrid = list[0].hybrid_section
            mList = list.subList(1, list.size)
        }
    }

    private var focusPosition = -1
    private var liveFocusPosition = -1

    var buttonIntro: LinearLayout? = null
    var buttonSearch: LinearLayout? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        when (viewType) {
            R.layout.item_footer -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_footer, parent, false)
                return FooterItemViewHolder(v)
            }
            R.layout.item_brand_shop_detail_header -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_brand_shop_detail_header, parent, false)
                return HeaderItemViewHolder(v)
            }
            else -> {
                val sharedPool = RecyclerView.RecycledViewPool()
                v = getLayoutInflater()
                    .inflate(R.layout.home_item_v2, parent, false)
                v.rv_sub_home.setRecycledViewPool(sharedPool)
                return ItemViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            (itemCount - 1) -> R.layout.item_footer
            0 -> return R.layout.item_brand_shop_detail_header
            else -> R.layout.home_item_v2
        }
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        if (mholder is ItemViewHolder && mList.isNotEmpty()) {
            val holder: ItemViewHolder = mholder
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
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                        holder.sub_dots.visibility = View.GONE
                        if (mList[position - 1].sectionName != "") {
                            holder.tv_sub_home.text = mList[position - 1].sectionName
                        }
                        var dotAdapter: DotsAdapter? = null

                        if (mList[position - 1].layoutType == LiveAdapter.LIVESTREAM_LAYOUT_SINGLE) {
                            val listDot = mList[position - 1].livestream.map {
                                "none"
                            }
                            dotAdapter = DotsAdapter(mActivity, listDot as ArrayList<String>)
                            holder.sub_dots.layoutManager = LinearLayoutManager(
                                mActivity,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            holder.sub_dots.visibility = View.VISIBLE
                            holder.sub_dots.adapter = dotAdapter
                        }

                        this.liveAdapter =
                            LiveAdapter(
                                mActivity,
                                mList[position - 1].livestream,
                                position,
                                mList[position - 1].layoutType
                            )
                        this.liveAdapter.setOnItemClickListener(object :
                            LiveAdapter.OnItemClickListener {
                            override fun onItemClick(
                                product: LiveStream,
                                position: Int,
                            ) {
                                if (mActivity.getCheckCustomerResponse() != null) {
                                    val dataObject = LogDataRequest()
                                    dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                                    mActivity.gotoAuthentication(AuthenticationActivity.REQUEST_CUSTOMER_RESULT_CODE)
                                }

                            }

                            override fun onPositionListFocus(
                                product: LiveStream,
                                mPoistion: Int,
                                positionEnterList: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
                                dataObject.liveStreamId = product.uid
                                dataObject.liveStreamName = product.name
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_LIVESTREAM,
                                    data
                                )
                                if (mList[position - 1].layoutType == LiveAdapter.LIVESTREAM_LAYOUT_SINGLE) {
                                    dotAdapter?.update(mPoistion)
                                }
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
                                collections: SimpleCollection, mPosition: Int,
                            ) {
                                clickListener.onCollectionClick(
                                    collections, mPosition,
                                    sectionName = mList[position - 1].sectionName,
                                    sectionIndex = (position - 1).toString(),
                                    sectionType = mList[position - 1].sectionType
                                )
                            }

                            override fun onPositionListFocus(
                                collections: SimpleCollection,
                                positionOnList: Int,
                                mPosition: Int,
                            ) {

                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                        holder.sub_dots.visibility = View.GONE

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

                                this.singleProductAdapter.setOnItemClickListener(object :
                                    SingleProductAdapter.OnItemClickListener {
                                    override fun onItemClick(
                                        product: Product,
                                        mPosition: Int,
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionIndex = (position - 1).toString(),
                                            sectionType = mList[position - 1].sectionType
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        product: Product,
                                        positionEnterList: Int,
                                        mPosition: Int,
                                    ) {
                                        val dataObject = LogDataRequest()
                                        dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                                        dataObject.itemIndex = mPosition.toString()
                                        dataObject.itemId = product.uid
                                        dataObject.itemBrand = product.brand.name
                                        dataObject.itemListPriceVat = product.price.toString()
                                        val data = Gson().toJson(dataObject).toString()
                                        (mActivity as BrandShopDetailActivity).logTrackingVersion2(
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
                                        }
                                    }
                                })
                                holder.sub_dots.visibility = View.VISIBLE

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
                                        mPosition: Int,
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionIndex = (position - 1).toString(),
                                            sectionType = mList[position - 1].sectionType
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        product: Product,
                                        positionEnterList: Int,
                                        mPosition: Int,
                                    ) {
                                        val dataObject = LogDataRequest()
                                        dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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


                                        dataObject.itemIndex = mPosition.toString()
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
                                        }
                                    }
                                })
                                holder.sub_dots.visibility = View.GONE

                                holder.rv_sub_home.adapter = bestSellerAdapter
                            }
                            else -> {
                                this.productAdapter =
                                    ProductBrandShopAdapterV2(
                                        mActivity,
                                        mList[position - 1].collectionTemp.items,
                                        position,
                                        true,
                                        mList[position - 1].collectionTemp.uid,
                                        mList[position - 1].collectionTemp.collection_name,
                                    )

                                this.productAdapter.setOnItemClickListener(object :
                                    ProductAdapterV2.OnItemClickListener {
                                    override fun onItemClick(
                                        product: Product,
                                        mPosition: Int,
                                    ) {
                                        clickListener.onItemClick(
                                            product,
                                            mPosition,
                                            mList[position - 1].collectionTemp.collection_name,
                                            sectionName = mList[position - 1].sectionName,
                                            sectionIndex = (position - 1).toString(),
                                            sectionType = mList[position - 1].sectionType
                                        )
                                    }

                                    override fun onPositionListFocus(
                                        mProduct: Product?,
                                        positionOnList: Int,
                                        mFocusPosition: Int,
                                    ) {
                                        mProduct?.let { product ->
                                            val dataObject = LogDataRequest()
                                            dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
                                            if (product.collection.size >= 1) {
                                                dataObject.category =
                                                    product.collection[0].collection_name
                                                dataObject.categoryId = product.collection[0].uid
                                                dataObject.itemCategoryId =
                                                    product.collection[0].uid
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
                                            dataObject.itemIndex = mFocusPosition.toString()
                                            dataObject.itemId = product.uid
                                            dataObject.itemBrand = product.brand.name
                                            dataObject.itemListPriceVat = product.price.toString()
                                            val data = Gson().toJson(dataObject).toString()
                                            mActivity.logTrackingVersion2(
                                                QuickstartPreferences.HOVER_PRODUCT_v2,
                                                data
                                            )
                                        }

                                        if (positionOnList != focusPosition) {
                                            focusPosition = positionOnList
                                        }
                                    }

                                    override fun onPositionLiveListFocus(
                                        product: Product?,
                                        positionLiveList: Int,
                                        mFocusPosition: Int,
                                    ) {
                                        if (positionLiveList != liveFocusPosition) {
                                            liveFocusPosition = positionLiveList
                                        }
                                    }

                                    override fun onClickWatchMore(uid: String, name: String) {
                                        clickListener.onMoreClick(name, uid)
                                    }
                                })
                                holder.sub_dots.visibility = View.GONE

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
                            ProductBrandShopAdapterV2(
                                mActivity,
                                list,
                                position,
                                false
                            )

                        this.productAdapter.setOnItemClickListener(object :
                            ProductAdapterV2.OnItemClickListener {
                            override fun onItemClick(
                                product: Product,
                                mPosition: Int,
                            ) {
                                clickListener.onItemClick(
                                    product,
                                    mPosition,
                                    mList[position - 1].sectionName,
                                    sectionName = mList[position - 1].sectionName,
                                    sectionIndex = (position - 1).toString(),
                                    sectionType = mList[position - 1].sectionType
                                )
                            }

                            override fun onPositionListFocus(
                                product: Product?,
                                positionOnList: Int,
                                mFocusPosition: Int,
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
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
                                mFocusPosition: Int,
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

                "brandshop_collection" -> {
                    if (mList[position - 1].brandshop_collections.size > 0) {
                        holder.tv_sub_home.visibility = View.VISIBLE
                        holder.tv_sub_home.text = mList[position - 1].sectionName

                        this.collectionAdapterV2 =
                            CollectionAdapterV2(
                                mActivity,
                                mList[position - 1].brandshop_collections,
                                position
                            )

                        this.collectionAdapterV2.setOnItemClickListener(object :
                            CollectionAdapterV2.OnItemClickListener {
                            override fun onItemClick(
                                collection: SimpleCollection, mPosition: Int,
                            ) {
                                clickListener.onBrandShopChildCollectionClick(
                                    collection,
                                    mPosition,
                                    sectionName = mList[position - 1].sectionName,
                                    sectionIndex = (position - 1).toString(),
                                    sectionType = mList[position - 1].sectionType
                                )
                            }

                            override fun onPositionListFocus(
                                collections: SimpleCollection,
                                positionOnList: Int,
                                mPosition: Int,
                            ) {

                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.BRANDSHOP.name
                                dataObject.layoutId = Config.homeData?.data?.name
                                dataObject.categoryId = collections.uid
                                dataObject.category = collections.collection_name
                                dataObject.categoryIndex = mPosition.toString()
                                dataObject.sectionName = mList[position - 1].sectionName
                                dataObject.sectionType = mList[position - 1].sectionType
                                dataObject.sectionIndex = (position - 1).toString()
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_BRANDSHOP_CATEGORY_v2,
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
            mActivity.initChildHeader(mholder.image_header)
            buttonSearch = mholder.image_header.bn_search
            if (hybrid != null && hybrid!!.size > 0) {
                hybridAdapter = HybridSliderAdapter(mActivity, hybrid!!)

                mholder.imageSlider.adapter = hybridAdapter

                if (hybrid!!.size == 1) {
                    mholder.dots.visibility = View.GONE

                } else {
                    mholder.dots.attachViewPager(mholder.imageSlider)
                    mholder.dots.setDotDrawable(
                        R.drawable.ic_indicator_next,
                        R.drawable.ic_indicator_forward,
                        R.drawable.ic_unselected_indicator
                    )

                }

                mholder.imageSlider.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        Functions.setStrokeFocusHybridBrandShop(mholder.rl_image_cover, mActivity)
                        createTimer(mholder.imageSlider, mholder.dots)
                    } else {
                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()
                        }

                        Functions.setLostFocusHybridBrandShop(mholder.rl_image_cover, mActivity)
                    }
                }

                mholder.imageSlider.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                        mholder.dots.changedByTimer = false

                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()

                            if (mholder.imageSlider.currentItem == 0) {
                                mholder.dots.changedByTimer = false
                                smoothScrollTo(mholder.imageSlider, mholder.dots, hybrid!!.size - 1)
                                return@OnKeyListener true
                            }

                            createTimer(mholder.imageSlider, mholder.dots)
                        }
                        Handler().postDelayed({ currentPage = mholder.imageSlider.currentItem }, 10)
                        return@OnKeyListener false
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                        mholder.dots.changedByTimer = true

                        if (::timer.isInitialized && timer != null) {
                            timer.purge()
                            timer.cancel()

                            if (mholder.imageSlider.currentItem == hybrid!!.size - 1) {
                                mholder.dots.changedByTimer = true
                                smoothScrollTo(mholder.imageSlider, mholder.dots, 0)
                                return@OnKeyListener true
                            }


                            createTimer(mholder.imageSlider, mholder.dots)
                        }
                        Handler().postDelayed({ currentPage = mholder.imageSlider.currentItem }, 10)
                        return@OnKeyListener false
                    }
                    false
                })

                mholder.imageSlider.setOnClickListener {
                    clickListener.onHybridSection(
                        hybrid!![mholder.imageSlider.currentItem],
                        position
                    )
                }

                mholder.bn_intro.nextFocusUpId = mholder.imageSlider.id

            } else {
                //navigate
                mholder.bn_intro.nextFocusUpId = mholder.image_header.bn_cart.id
                mholder.image_header.bn_cart.nextFocusDownId = mholder.bn_intro.id
                mholder.image_header.bn_search.nextFocusDownId = mholder.bn_intro.id
                mholder.image_header.bn_account.nextFocusDownId = mholder.bn_intro.id
                mholder.image_header.bn_header_back.nextFocusDownId = mholder.bn_intro.id

                //view
                mholder.dots.visibility = View.GONE

                imageAdapter = ImageSliderAdapter(mActivity, mBrandShop.images)

                mholder.imageSlider.adapter = imageAdapter

                //todo slide
            }

            ImageUtils.loadImageCache(
                mActivity,
                mholder.img_avatar,
                mBrandShop.image_logo,
                ImageUtils.TYPE_BRAND_SHOP_AVATAR
            )

            @Suppress("SENSELESS_COMPARISON")
            if (mBrandShop.hotline != null && mBrandShop.hotline.isNotBlank()) {
                mholder.hotline.text = mBrandShop.hotline
            } else {
                mholder.hotline.visibility = View.GONE
                mholder.text_hotline.visibility = View.GONE
            }

            mholder.name.text = mBrandShop.display_name_detail
            mholder.bn_intro.setOnFocusChangeListener { _, hasFocus ->
                mholder.text_bn_intro.isSelected = hasFocus
                if (hasFocus)
                    liveFocusPosition = 0
            }

            this.buttonIntro = mholder.bn_intro

            mholder.text_bn_intro.text = mBrandShop.introButtonText

            mholder.bn_intro.setOnClickListener {
                clickListener.onClickIntroShop()
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

    fun getLiveCurrentPositon(): Int {
        return liveFocusPosition
    }

    private fun smoothScrollTo(slider: ViewPager, dots: DotsIndicator, position: Int) {
        currentPage = position
        slider.setCurrentItem(currentPage, true)
        createTimer(slider, dots)
        Handler().postDelayed({ currentPage = slider.currentItem }, 10)
    }

    inner class HeaderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val image_cover: AppCompatImageView = view.image_cover
        val imageSlider: ViewPager = view.imageSlider
        val rl_image_cover: MaterialCardView = view.rl_image_cover
        val dots: DotsIndicator = view.dots

        val img_avatar: CircleImageView = view.img_avatar

        //val img_certificated: AppCompatImageView = view.img_certificated
        val name: SfTextView = view.name

        // val rate: SfTextView = view.rate
        val hotline: SfTextView = view.hotline
        val bn_intro: LinearLayout = view.bn_intro
        val text_bn_intro: SfTextView = view.text_bn_intro
        val image_header: View = view.image_header

        val text_hotline: View = view.text_hotline

        init {
            //todo change
            /*val displayMetrics = DisplayMetrics()
            mActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            val height = (width / 1920) * 460
            rl_image_cover.layoutParams = RelativeLayout.LayoutParams(width, height)*/

        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_sub_home: HorizontalGridView = view.rv_sub_home
        var tv_sub_home: SfTextView = view.tv_sub_home
        var layout_content_home: LinearLayout = view.layout_content_home
        val sub_dots: RecyclerView = view.sub_dots
    }

    inner class FooterItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rl_con: RelativeLayout = view.rl_con
        var ic_arrow: AppCompatImageView = view.ic_arrow
        var name: SfTextView = view.name
    }

    override fun getItemCount(): Int {
        return mList.size + 2
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onClickIntroShop()
        fun onItemClick(
            product: Product,
            position: Int,
            clickFrom: String,
            sectionName: String,
            sectionIndex: String,
            sectionType: String,
        )

        fun onCollectionClick(
            collections: SimpleCollection,
            position: Int,
            sectionName: String,
            sectionIndex: String,
            sectionType: String,
        )

        fun onBrandShopChildCollectionClick(
            collections: SimpleCollection,
            position: Int,
            sectionName: String,
            sectionIndex: String,
            sectionType: String,
        )

        fun onHaveProductOutsite(product: Product, position: Int)
        fun onMoreClick(subCollectionName: String, subcCollectionId: String)
        fun onVoucherClick(voucher: Voucher, position: Int)
        fun onOnClickFooter()
        fun onLiveStreamClick(product: LiveStream, position: Int, clickFrom: String)
        fun onHybridSection(product: HybridSection, position: Int)
        fun onPromotionClick(promotion: Promotion, position: Int, clickFrom: String)
    }
}
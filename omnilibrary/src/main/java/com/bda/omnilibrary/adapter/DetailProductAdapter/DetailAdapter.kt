package com.bda.omnilibrary.adapter.DetailProductAdapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.text.Html
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.custome.VideoPlayer
import com.bda.omnilibrary.dialog.ProductDescriptionDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.productDetailActivity.ProductDetailActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfStrikeTextView
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.detail_product_order_product.view.*
import kotlinx.android.synthetic.main.detail_product_preview.view.*
import kotlinx.android.synthetic.main.detail_product_preview_image.view.*
import kotlinx.android.synthetic.main.detail_product_product.view.*
import kotlinx.android.synthetic.main.item_footer.view.*
import kotlinx.android.synthetic.main.item_header_screen.view.*
import java.util.*

@Suppress("PropertyName")
class DetailAdapter(
    activity: BaseActivity,
    product: Product,
    listRelativeProduct: ArrayList<Product>,
) :
    BaseAdapter(activity),
    ProductDetailActivity.OnKeyDownListener {

    private var mProduct: Product = product
    private var mListRelativeProduct = listRelativeProduct
    private lateinit var clickListener: OnItemClickListener
    private lateinit var detailPreviewImageAdapter: DetailPreviewImageAdapter
    private lateinit var detailPreviewReviewAdapter: DetailPreviewReviewAdapter
    private lateinit var detailOrderProductAdapter: DetailOrderProductAdapter
    private var currentPositionFocus = 0
    private var listVideo = ArrayList<Product.MediaType>()

    private var checkForUserPauseAndSpeak = Handler()

    private var isFirstCreate = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.detail_product_product -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_product_product, parent, false)
                return ItemProductViewHolder(v)
            }

            R.layout.detail_product_preview_image -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_product_preview_image, parent, false)
                return ItemPreviewImageViewHolder(v)
            }

            R.layout.detail_product_description -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_product_description, parent, false)
                return ItemDescriptionViewHolder(v)
            }

            R.layout.detail_product_preview -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_product_preview, parent, false)
                return ItemPreviewViewHolder(v)
            }

            R.layout.detail_product_order_product -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.detail_product_order_product, parent, false)
                return ItemOrderProductViewHolder(v)
            }

            else -> {
                val v = getLayoutInflater()
                    .inflate(R.layout.item_footer, parent, false)
                return FooterItemViewHolder(v)
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemProductViewHolder -> {
                holder.detail_title.text = mProduct.display_name_detail

                mActivity.initChildHeader(holder.image_header)

                //export
                _bn_buy_now = holder.rl_text_bn_buy_now
                _bn_add_to_cart = holder.rl_text_bn_add_to_cart
                _header = holder.image_header

                listVideo.clear()

                listVideo.addAll(mProduct.videos)
                listVideo.addAll(mProduct.images)
                holder.detail_title.text = mProduct.display_name_detail
                holder.detail_price.text = Functions.formatMoney(mProduct.price)

                if (mProduct.listedPrice > 0 && mProduct.listedPrice > mProduct.price) {
                    holder.list_price.text = Functions.formatMoney(mProduct.listedPrice)
                } else {
                    holder.list_price.visibility = View.GONE
                }

                if (mProduct.supplier.required_order_value > mProduct.price) {
                    holder.rl_text_bn_buy_now.visibility = View.GONE
                    holder.rl_text_bn_add_to_cart.nextFocusLeftId = holder.rl_text_bn_add_to_cart.id

                    val p = holder.rl_text_bn_add_to_cart.layoutParams as LinearLayout.LayoutParams
                    p.setMargins(0, 0, 0, 0)
                    holder.rl_text_bn_add_to_cart.layoutParams = p
                } else {
                    holder.rl_text_bn_buy_now.visibility = View.VISIBLE
                }

                if (mProduct.videos.isNotEmpty()) {
                    ImageUtils.loadImage(
                        mActivity,
                        holder.image_video,
                        mProduct.videos[0].icon,
                        ImageUtils.TYPE_VIDEO
                    )
                    holder.ic_img_product.visibility = View.GONE
                    holder.ln_video.visibility = View.VISIBLE
                    holder.ln_video.setOnClickListener {
                        checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
                        videoDetailCover?.pause()
                        Handler().postDelayed({
                            System.gc()
                            videoDetailCover?.release()
                        }, 3000)

                        clickListener.onVideoCoverClick(listVideo[0], 0)
                    }

                    startVideo(holder)

                    holder.ln_video.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            Functions.setStrokeFocus(holder.rl_video, mActivity)
                            playVideo(holder.ic_play)
                        } else {
                            Functions.setLostFocus(holder.rl_video, mActivity)
                        }
                    }
                } else {
                    holder.ln_video.visibility = View.GONE
                    if (mProduct.imageCover.isNotBlank()) {
                        holder.ln_img_product.visibility = View.VISIBLE

                        ImageUtils.loadImage(
                            mActivity,
                            holder.ic_img_product,
                            mProduct.imageCover, ImageUtils.TYPE_PRODUCT
                        )

                    } else {
                        holder.ln_img_product.visibility = View.GONE
                    }
                }

                holder.tv_number_reviews.text = mProduct.brand.name

                if (mProduct.sku_id != null
                    && mProduct.sku_id.isNotEmpty()
                    && mProduct.sku_id.isNotBlank()
                ) {
                    holder.tv_sku.text = mActivity.getString(
                        R.string.text_SKU,
                        mProduct.sku_id
                    )//"SKU: " + mProduct.sku_id

                } else {
                    holder.tv_sku.visibility = View.GONE

                }

                holder.rl_review.setOnFocusChangeListener { _, b ->
                    if (b) {
                        holder.rl_review.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.background_button_active
                        )

                        holder.tv_number_reviews.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )

                        holder.rb_review.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )

                    } else {
                        holder.rl_review.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.border_shape_gray
                        )
                        holder.tv_number_reviews.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )
                        holder.rb_review.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )

                    }
                }

                if (mProduct.brand_shop != null && mProduct.brand_shop!!.size > 0) {
                    holder.tv_store.text = mProduct.brand_shop!![0].display_name_in_product
                    holder.store.text = mActivity.getString(R.string.text_doi_tac_cao_cap)
                    holder.rl_store.setOnClickListener {
                        mActivity.gotoBrandShopDetail(mProduct.brand_shop!![0].uid)
                    }

                } else {
                    holder.tv_store.text =
                        Functions.firstLetterUppercasedAndFilterAcronym(mProduct.supplier.supplier_name)
                    holder.store.text = mActivity.getString(R.string.text_nha_cung_cap)
                }

                holder.rl_store.setOnFocusChangeListener { _, b ->
                    if (b) {
                        holder.rl_store.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.background_button_active
                        )

                        holder.tv_store.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )
                        holder.store.setTextColor(ContextCompat.getColor(mActivity, R.color.white))

                        playVideo(holder.ic_play)
                    } else {
                        holder.rl_store.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.border_shape_gray
                        )

                        holder.tv_store.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )
                        holder.store.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )
                    }
                }

                holder.rl_policy.setOnFocusChangeListener { _, b ->
                    if (b) {
                        holder.rl_policy.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.background_button_active
                        )

                        holder.tv_policy.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )
                        holder.policy.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.white
                            )
                        )

                    } else {
                        holder.rl_policy.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.border_shape_gray
                        )

                        holder.tv_policy.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )
                        holder.policy.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.text_color_dark
                            )
                        )
                    }
                }

                if (mProduct.short_descriptions.short_des_1 != null && mProduct.short_descriptions.short_des_1.isNotEmpty()) {
                    holder.short_des_1.visibility = View.VISIBLE
                    holder.short_des_1.text = mProduct.short_descriptions.short_des_1
                    try {
                        holder.short_des_1.setTextColor(Color.parseColor(mProduct.short_descriptions.short_des_1_color.takeIf { mProduct.short_descriptions.short_des_1_color != null && mProduct.short_descriptions.short_des_1_color.isNotEmpty() }
                            ?: "#000000"))
                    } catch (e: IllegalArgumentException) {

                    }
                } else {
                    holder.short_des_1.visibility = View.GONE
                }

                if (mProduct.short_descriptions.short_des_2 != null && mProduct.short_descriptions.short_des_2.isNotEmpty()) {
                    holder.short_des_2.visibility = View.VISIBLE
                    holder.short_des_2.text = mProduct.short_descriptions.short_des_2
                    try {
                        holder.short_des_2.setTextColor(Color.parseColor(mProduct.short_descriptions.short_des_2_color.takeIf { mProduct.short_descriptions.short_des_2_color != null && mProduct.short_descriptions.short_des_2_color.isNotEmpty() }
                            ?: "#000000"))
                    } catch (e: IllegalArgumentException) {

                    }
                } else {
                    holder.short_des_2.visibility = View.GONE
                }

                if (mProduct.short_descriptions.short_des_3 != null && mProduct.short_descriptions.short_des_3.isNotEmpty()) {
                    holder.short_des_3.visibility = View.VISIBLE
                    holder.short_des_3.text = mProduct.short_descriptions.short_des_3
                    try {
                        holder.short_des_3.setTextColor(Color.parseColor(mProduct.short_descriptions.short_des_3_color.takeIf { mProduct.short_descriptions.short_des_3_color != null && mProduct.short_descriptions.short_des_3_color.isNotEmpty() }
                            ?: "#000000"))
                    } catch (e: IllegalArgumentException) {

                    }
                } else {
                    holder.short_des_3.visibility = View.GONE
                }

                if (mProduct.short_descriptions.short_des_4 != null && mProduct.short_descriptions.short_des_4.isNotEmpty()) {
                    holder.short_des_4.visibility = View.VISIBLE
                    holder.short_des_4.text = mProduct.short_descriptions.short_des_4
                    try {
                        holder.short_des_4.setTextColor(Color.parseColor(mProduct.short_descriptions.short_des_4_color.takeIf { mProduct.short_descriptions.short_des_4_color != null && mProduct.short_descriptions.short_des_4_color.isNotEmpty() }
                            ?: "#000000"))
                    } catch (e: IllegalArgumentException) {

                    }
                } else {
                    holder.short_des_4.visibility = View.GONE
                }

                holder.rl_text_bn_buy_now.setOnClickListener {
                    if (mProduct.instock) {
                        clickListener.onQuickBuyClick()

                    } else {
                        clickListener.onNotAvailableClick()

                    }
                }

                holder.rl_text_bn_add_to_cart.setOnClickListener {
                    if (mProduct.instock) {
                        pauseAndReleaseVideo()
                        clickListener.onAddToCartClick()

                    } else {
                        clickListener.onNotAvailableClick()

                    }
                }

                rl_text_bn_wish_list?.setOnClickListener {
                    clickListener.onFavouriteClick()
                }
                holder.rl_text_bn_buy_now.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus)
                        playVideo(holder.ic_play)

                    holder.rl_text_bn_buy_now.isSelected = hasFocus
                }
                holder.rl_text_bn_add_to_cart.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus)
                        playVideo(holder.ic_play)

                    holder.text_bn_add_to_cart.isSelected = hasFocus
                }
                rl_text_bn_wish_list?.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus)
                        playVideo(holder.ic_play)

                    text_bn_wish_list?.isSelected = hasFocus
                }

                if (mProduct.is_disabled_quickpay) {
                    holder.rl_text_bn_buy_now.visibility = View.GONE
                    holder.rl_text_bn_add_to_cart.nextFocusLeftId = holder.rl_text_bn_add_to_cart.id

                    val p = holder.rl_text_bn_add_to_cart.layoutParams as LinearLayout.LayoutParams
                    p.setMargins(0, 0, 0, 0)

                    holder.rl_text_bn_add_to_cart.layoutParams = p
                }

                if (isFirstCreate) {
                    Handler().postDelayed({
                        if (holder.rl_text_bn_buy_now.visibility == View.VISIBLE) {
                            holder.rl_text_bn_buy_now.requestFocus()
                        } else {
                            holder.rl_text_bn_add_to_cart.requestFocus()
                        }
                    }, 50)
                    isFirstCreate = false
                }
            }

            is ItemPreviewImageViewHolder -> {
                val listMedia = ArrayList<Product.MediaType>()
                listMedia.addAll(mProduct.videos)
                listMedia.addAll(mProduct.images)

                if (listMedia.size > 0) {
                    detailPreviewImageAdapter =
                        DetailPreviewImageAdapter(mActivity, listMedia)

                    detailPreviewImageAdapter.setOnItemClickListener(object :
                        DetailPreviewImageAdapter.OnItemClickListener {
                        override fun onItemVideoClick(position: Int) {
                            pauseAndReleaseVideo()
                            clickListener.onVideoClick(listMedia[position], position)
                        }

                        override fun onItemImageClick(position: Int) {
                            clickListener.onImageClick(mProduct.images, position)
                        }

                        override fun onItemFocus(position: Int) {
                            pauseVideo()

                            if (currentPositionFocus != position) {
                                currentPositionFocus = position
                            }
                        }
                    })

                    holder.rv_detail_info.adapter = detailPreviewImageAdapter
                } else {
                    holder.layout_image_cover.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
            is ItemDescriptionViewHolder -> {
                if (mProduct.description_html != null) {
                    holder.rl_description.setOnFocusChangeListener { _, hasFocus ->
                        if (hasFocus) {
                            currentPositionFocus = 3

                            Functions.setStrokeFocus(holder.cardview_description, mActivity)
                        } else {

                            Functions.setLostFocus(holder.cardview_description, mActivity)
                        }
                    }
                    mProduct.description_html?.let {
                        holder.tv_description.text = Html.fromHtml(mProduct.description_html)
                    }

                    holder.rl_description.setOnClickListener {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.PRODUCT_DETAIL.name
                        dataObject.itemName = mProduct.name
                        mProduct.let {
                            if (it.collection.size >= 1) {
                                dataObject.itemCategoryId = it.collection[0].uid
                                dataObject.itemCategoryName = it.collection[0].collection_name
                            }
                        }
                        dataObject.itemId = mProduct.uid
                        dataObject.itemBrand = mProduct.brand.name
                        dataObject.itemListPriceVat = mProduct.price.toString()
                        val data = Gson().toJson(dataObject).toString()
                        mActivity.logTrackingVersion2(
                            QuickstartPreferences.CLICK_PRODUCT_DESCRIPTION_FIELD_v2,
                            data
                        )
                        mProduct.description_html?.let {
                            ProductDescriptionDialog(
                                mActivity,
                                mProduct.description_html!!
                            )
                        }

                    }

                } else {
                    holder.layout_main.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
            is ItemPreviewViewHolder -> {
                if (mProduct.images != null && mProduct.images.size > 0) {

                    detailPreviewReviewAdapter =
                        DetailPreviewReviewAdapter(mActivity, mProduct.images)

                    detailPreviewReviewAdapter.setOnItemClickListener(object :
                        DetailPreviewReviewAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                        }

                        override fun onItemFocus(position: Int) {
                        }

                    })
                    holder.rv_detail_review.adapter = detailPreviewReviewAdapter

                } else {
                    holder.layout_preview.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
            is ItemOrderProductViewHolder -> {
                if (mListRelativeProduct != null && mListRelativeProduct.size > 0) {

                    detailOrderProductAdapter =
                        DetailOrderProductAdapter(mActivity, mListRelativeProduct)
                    detailOrderProductAdapter.setOnItemClickListener(object :
                        DetailOrderProductAdapter.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            clickListener.onOtherItemClick(mListRelativeProduct[position], position)
                        }

                        override fun onItemFocus(product: Product) {

                        }
                    })

//                    holder.rv_detail_order_product.windowAlignmentOffsetPercent = 19f
//                    holder.rv_detail_order_product.windowAlignment = WINDOW_ALIGN_NO_EDGE
                    holder.rv_detail_order_product.adapter = detailOrderProductAdapter

                } else {
                    holder.layout_order_product.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
            is FooterItemViewHolder -> {
                holder.itemView.setOnClickListener {
                    clickListener.onBackToHeadClick()
                }

                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.ic_arrow.setImageDrawable(
                            ContextCompat.getDrawable(
                                mActivity,
                                R.mipmap.ic_mui_ten_white
                            )
                        )
                        holder.name.setTextColor(ContextCompat.getColor(mActivity, R.color.white))
                        holder.rl_con.background =
                            ContextCompat.getDrawable(
                                mActivity,
                                R.drawable.background_button_active
                            )

                    } else {
                        holder.ic_arrow.setImageDrawable(
                            ContextCompat.getDrawable(
                                mActivity,
                                R.mipmap.ic_mui_ten
                            )
                        )
                        holder.name.setTextColor(
                            ContextCompat.getColor(
                                mActivity,
                                R.color.start_color
                            )
                        )
                        holder.rl_con.background = ContextCompat.getDrawable(
                            mActivity,
                            R.drawable.background_collection_v2_default
                        )
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.detail_product_product//POSITION_CART_INFO
            1 -> R.layout.detail_product_preview_image//POSITION_PRODUCT
            2 -> R.layout.detail_product_description//POSITION_PREVIEW_IMAGE
            3 -> R.layout.detail_product_order_product//POSITION_PREVIEW
            4 -> R.layout.item_footer//POSITION_ORDER_PRODUCT
            else -> R.layout.item_footer
        }
    }

    private fun pauseAndReleaseVideo() {
        checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
        videoDetailCover?.pause()
        System.gc()
        videoDetailCover?.release()
    }

    fun pauseVideo() {
        videoDetailCover?.pause()
    }

    private fun playVideo(view: View) {
        if (videoDetailCover != null && !videoDetailCover!!.isPlaying) {
            videoDetailCover?.play()
            view.visibility = View.GONE
        }
    }

    private fun startVideo(holder: ItemProductViewHolder) {
        val video = Functions.isVideoInProduct(mProduct)
        if (video != null) {
            val url = Functions.getVideoUrl(video)
            videoDetailCover?.setMute(false)
            videoDetailCover?.setShowSpinner(false)
            videoDetailCover?.setFadeInTime(true)
            checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)
            checkForUserPauseAndSpeak.postDelayed({
                videoDetailCover?.start(url)
                holder.ic_play.visibility = View.GONE

            }, 2000)

            videoDetailCover?.setVideoTracker(object :
                VideoPlayer.VideoPlaybackTracker {
                override fun onPlaybackError(e: Exception?) {
                    //holder.ic_play.visibility = View.VISIBLE

                }

                override fun onCompleteVideo() {
                    holder.ic_play.visibility = View.VISIBLE
                }

                override fun onTimeStick(time: Int) {

                }

                override fun onReady() {

                }
            })
        }
    }

    private var videoDetailCover: VideoPlayer? = null
    fun releaseVideo() {
        checkForUserPauseAndSpeak.removeCallbacksAndMessages(null)

        if (videoDetailCover != null) {
            videoDetailCover!!.pause()
            System.gc()
            videoDetailCover!!.release()
        }
    }

    inner class FooterItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rl_con: RelativeLayout = view.rl_con
        var ic_arrow: AppCompatImageView = view.ic_arrow
        var name: SfTextView = view.name
    }

    var text_bn_wish_list: SfTextView? = null
    var rl_text_bn_wish_list: RelativeLayout? = null
    var _bn_buy_now: RelativeLayout? = null
    var _header: View? = null
    var _bn_add_to_cart: RelativeLayout? = null

    inner class ItemProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var detail_title: SfTextView = view.detail_title

        var rl_review: RelativeLayout = view.rl_review
        var rb_review: SfTextView = view.review
        var tv_number_reviews: SfTextView = view.tv_review

        var tv_store: SfTextView = view.tv_store
        var store: SfTextView = view.store
        var rl_store: RelativeLayout = view.rl_store

        var policy: SfTextView = view.policy
        var tv_policy: SfTextView = view.tv_policy
        var rl_policy: RelativeLayout = view.rl_policy

        var detail_price: SfTextView = view.detail_price

        var short_des_1: SfTextView = view.short_des_1
        var short_des_2: SfTextView = view.short_des_2
        var short_des_3: SfTextView = view.short_des_3
        var short_des_4: SfTextView = view.short_des_4

        var list_price: SfStrikeTextView = view.list_price
        val text_bn_buy_now: SfTextView = view.text_bn_buy_now
        val rl_text_bn_buy_now: RelativeLayout = view.rl_text_bn_buy_now
        val text_bn_add_to_cart: SfTextView = view.text_bn_add_to_cart
        val rl_text_bn_add_to_cart: RelativeLayout = view.rl_text_bn_add_to_cart
        var ic_img_product: ImageView = view.ic_img_product

        var ic_play: ImageView = view.ic_play
        var rl_video: MaterialCardView = view.rl_video
        val image_video: ImageView = view.image_video

        val ln_video: LinearLayout = view.ln_video
        val ln_img_product: LinearLayout = view.ln_img_product
        val cv_ic_img_product: MaterialCardView = view.findViewById(R.id.cv_ic_img_product)

        var tv_sku: SfTextView = view.findViewById(R.id.tv_sku)

        val image_header: View = view.image_header

        init {
            text_bn_wish_list = view.text_bn_wish_list
            rl_text_bn_wish_list = view.rl_text_bn_wish_list
            videoDetailCover = view.ic_video_product
        }
    }

    fun setTextFavourite(text: String) {
        Handler().postDelayed({
            text_bn_wish_list?.text = text
        }, 100)

    }

    inner class ItemPreviewImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_detail_info: HorizontalGridView = view.findViewById(R.id.rv_detail_info)
        var layout_image_cover: RelativeLayout = view.layout_image_cover
    }

    inner class ItemDescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_description: SfTextView = view.findViewById(R.id.tv_description)
        var layout_main: LinearLayout = view.findViewById(R.id.layout_main)
        var rl_description: RelativeLayout = view.findViewById(R.id.rl_description)
        var cardview_description: MaterialCardView = view.findViewById(R.id.cardview_description)
    }

    inner class ItemPreviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout_preview: LinearLayout = view.layout_preview
        var rv_detail_review: HorizontalGridView = view.rv_detail_review
    }

    inner class ItemOrderProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_detail_order_product: HorizontalGridView = view.rv_detail_order_product
        var layout_order_product: LinearLayout = view.layout_order_product
    }

    override fun getItemCount(): Int {
        return 5
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onOtherItemClick(
            product: Product, position: Int,
        )

        fun onQuickBuyClick()

        fun onFavouriteClick()
        fun onBackToHeadClick()
        fun onPolicyClick()
        fun onStoreClick()
        fun onReviewClick()
        fun onAddToCartClick()
        fun onBackClick()
        fun onSearchClick()
        fun onNotAvailableClick()
        fun onVideoClick(video: Product.MediaType, position: Int)
        fun onVideoCoverClick(video: Product.MediaType, position: Int)
        fun onImageClick(listMedia: ArrayList<Product.MediaType>, position: Int)
        fun onSpecificationClick(spec: Product.Detail)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent) {
        when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> if (event.action == KeyEvent.ACTION_DOWN) {
                if (_bn_buy_now != null && _header != null && _bn_add_to_cart != null) {
                    if (_header!!.bn_header_back.hasFocus())
                        Handler().postDelayed({
                            if (_bn_buy_now!!.visibility == View.VISIBLE)
                                _bn_buy_now!!.requestFocus()
                            else
                                _bn_add_to_cart!!.requestFocus()

                        }, 0)
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> if (event.action == KeyEvent.ACTION_DOWN) {

                if ((_bn_buy_now != null && _bn_buy_now!!.hasFocus())
                    || (_bn_add_to_cart != null && _bn_add_to_cart!!.hasFocus())
                ) {
                    _header!!.bn_header_back.requestFocus()
                }
            }
        }
    }
}
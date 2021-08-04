package com.bda.omnilibrary.adapter.search

import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.VerticalGridView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.adapter.homev2.HotStoreAdapter
import com.bda.omnilibrary.adapter.homev2.ProductAdapterV2
import com.bda.omnilibrary.custome.SearchKeyboardView
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.BrandShop
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.searchActivity.SearchActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.views.SfEditText
import com.bda.omnilibrary.views.SfTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_activity_header.view.*
import kotlinx.android.synthetic.main.item_search.view.*
import kotlinx.android.synthetic.main.item_search_header.view.*

class SearchAdapter(
    activity: BaseActivity,
    private var topKeyWords: ArrayList<String>
) : BaseAdapter(activity) {
    private lateinit var suggestKeyWordAdapter: SuggestKeyWordAdapter
    private var suggests: ArrayList<String>? = null
    private lateinit var clickListener: OnCallBackListener
    private var products: ArrayList<Product>? = null
    private var stores: ArrayList<BrandShop>? = null
    private var otherProducts: ArrayList<Product>? = null
    private lateinit var rlSuggest: RelativeLayout
    var isVoice: Boolean = true
    var count: Int = 0
    var voiceButton: ImageView? = null
    var isSuggestionListFocus = false

    fun setDataFoundProduct(products: ArrayList<Product>, stores: ArrayList<BrandShop>) {
        clearData()
        rlSuggest.visibility = View.GONE
        this.products = products
        this.stores = stores

        notifyItemRangeChanged(1, 2)
    }

    fun setDataSuggestionProducts(otherProducts: ArrayList<Product>) {
        clearData()

        rlSuggest.visibility = View.GONE

        this.otherProducts = otherProducts

        notifyItemRangeChanged(1, 2)

    }

    fun setDataVoice(text: String) {
        myEditText?.isSelected = true
        myEditText?.setText(text)
        myKeyboard?.addText(text)
    }


    fun setSuggestKeywordData(suggests: ArrayList<String>) {
        clearData()

        rlSuggest.visibility = View.VISIBLE

        this.suggests = suggests

        if (this::suggestKeyWordAdapter.isInitialized) {
            this.suggestKeyWordAdapter.notifyDataSetChanged()
        }

        notifyItemRangeChanged(1, 2)
    }

    private fun clearData() {
        products?.clear()
        stores?.clear()
        otherProducts?.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == R.layout.item_search_header) {
            v = getLayoutInflater()
                .inflate(R.layout.item_search_header, parent, false)
            HeaderItemViewHolder(v)
        } else {
            val sharedPool = RecyclerView.RecycledViewPool()
            v = getLayoutInflater()
                .inflate(R.layout.item_search, parent, false)
            v.rv_sub_home.setRecycledViewPool(sharedPool)
            ItemViewHolder(v)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_search_header
            else -> R.layout.item_search
        }
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        if (mholder is ItemViewHolder) {
            when (position) {
                1 -> {
                    if (otherProducts != null && otherProducts!!.size > 0) {
                        mholder.itemView.visibility = View.VISIBLE

                        mholder.tv_sub_home.text =
                            mActivity.getString(R.string.text_product_result, "0")

                        mholder.rv_sub_home.visibility = View.GONE
                        mholder.img_not_found.visibility = View.GONE

                        mholder.space.visibility = View.VISIBLE

                    } else if (products != null && products!!.size > 0) {
                        mholder.itemView.visibility = View.VISIBLE

                        mholder.tv_sub_home.text =
                            mActivity.getString(
                                R.string.text_product_result,
                                products!!.size.toString()
                            )

                        mholder.rv_sub_home.visibility = View.VISIBLE

                        mholder.img_not_found.visibility = View.GONE
                        mholder.space.visibility = View.GONE

                        val productAdapter = ProductAdapterV2(mActivity, products!!, 1, false)
                        productAdapter.setOnItemClickListener(object :
                            ProductAdapterV2.OnItemClickListener {
                            override fun onItemClick(product: Product, position: Int) {
                                clickListener.onClickProduct(product, position)
                            }

                            override fun onPositionListFocus(
                                product: Product?,
                                positionEnterList: Int,
                                position: Int
                            ) {
                            }

                            override fun onPositionLiveListFocus(
                                product: Product?,
                                positionLiveList: Int,
                                position: Int
                            ) {
                            }


                            override fun onClickWatchMore(uid: String, name: String) {
                            }


                        })

                        mholder.rv_sub_home.adapter = productAdapter
                    } else {
                        mholder.itemView.visibility = View.GONE
                    }

                }

                2 -> {
                    if (otherProducts != null && otherProducts!!.size > 0) {
                        mholder.itemView.visibility = View.VISIBLE

                        mholder.tv_sub_home.text =
                            mActivity.getString(R.string.text_interesting_product)

                        val productAdapter = ProductAdapterV2(mActivity, otherProducts!!, 1, false)
                        productAdapter.setOnItemClickListener(object :
                            ProductAdapterV2.OnItemClickListener {
                            override fun onItemClick(product: Product, position: Int) {
                                clickListener.onClickProduct(product, position)
                            }

                            override fun onPositionListFocus(
                                product: Product?,
                                positionEnterList: Int,
                                position: Int
                            ) {
                                if (product != null) {
                                    val dataObject = LogDataRequest()
                                    dataObject.screen = Config.SCREEN_ID.SEARCH.name
                                    if (product.collection.size > 0) {
                                        dataObject.category =
                                            product.collection[0].collection_name
                                        dataObject.categoryId = product.collection[0].uid
                                        dataObject.itemCategoryId = product.collection[0].uid
                                        dataObject.itemCategoryName =
                                            product.collection[0].collection_name
                                    }
                                    dataObject.itemName = product.name
                                    dataObject.itemIndex = position.toString()
                                    dataObject.itemId = product.uid
                                    dataObject.itemBrand = product.brand.name
                                    dataObject.itemListPriceVat = product.price.toString()
                                    val data = Gson().toJson(dataObject).toString()
                                    (mActivity as SearchActivity).logTrackingVersion2(
                                        QuickstartPreferences.HOVER_PRODUCT_v2,
                                        data
                                    )
                                }

                                isSuggestionListFocus = true
                            }

                            override fun onPositionLiveListFocus(
                                product: Product?,
                                positionLiveList: Int,
                                position: Int
                            ) {
                                isSuggestionListFocus = false
                            }


                            override fun onClickWatchMore(uid: String, name: String) {
                            }

                        })

                        mholder.rv_sub_home.adapter = productAdapter

                    } else if (stores != null && stores!!.size > 0) {
                        mholder.itemView.visibility = View.VISIBLE

                        mholder.tv_sub_home.text = mActivity.getString(
                            R.string.text_product_store_result,
                            stores!!.size.toString()
                        )

                        val productAdapter = HotStoreAdapter(mActivity, stores!!, 1)
                        productAdapter.setOnItemClickListener(object :
                            HotStoreAdapter.OnItemClickListener {
                            override fun onItemClick(product: BrandShop, position: Int) {
                                clickListener.onClickStore(product, position)
                            }

                            override fun onPositionListFocus(
                                product: BrandShop,
                                position: Int,
                                positionEnterList: Int
                            ) {
                            }

                            override fun onPositionLiveListFocus(positionLiveList: Int) {
                            }

                            override fun onFocusItem(brandShop: BrandShop, index: Int) {
                            }

                            override fun onLostFocusItem(brandShop: BrandShop) {
                            }
                        })

                        mholder.rv_sub_home.adapter = productAdapter

                    } else {
                        mholder.itemView.visibility = View.GONE
                    }
                }
            }

        } else if (mholder is HeaderItemViewHolder) {

            mholder.binding(topKeyWords)
        }

    }

    var myKeyboard: SearchKeyboardView? = null
    private var myEditText: SfEditText? = null
    var header: View? = null

    @Suppress("PropertyName")
    inner class HeaderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val keyboard = view.keyboard
        val voice: ImageView = view.voice

        val rlTextSuggest: RelativeLayout = view.rlTextSuggest
        val vgSuggestText: VerticalGridView = view.vgSuggestText
        val tv_result: SfTextView = view.tv_result

        val editText: SfEditText = view.editText
        val txt_quantity: SfTextView = view.txt_quantity

        init {
            (mActivity as SearchActivity).initChildHeader(view.header, BaseActivity.Tab.SEARCH)
            header = view.header

            this@SearchAdapter.myKeyboard = keyboard
            this@SearchAdapter.myEditText = editText
            this@SearchAdapter.voiceButton = voice

            keyboard.initKeyboardAll()
            keyboard.setOnKeyboardCallback(object : SearchKeyboardView.OnKeyboardCallback {
                override fun onDpadDownInBottom() {
                }

                override fun onDpadLeftInLeft() {
                    Handler().postDelayed({
                        voice.requestFocus()
                    }, 0)
                }

                override fun onSearchSubmit(query: String?, isClickSearch: Boolean) {
                    editText.setText(query)
                    editText.isSelected = (query != null && query.isNotEmpty())
                    editText.isCursorVisible = true
                    clickListener.onClickSearch(query!!, isClickSearch)
                }

                override fun onSearchFocusable(isFocus: Boolean) {
                }

            })



            Handler().postDelayed({
                editText.moveCursorToVisibleOffset()
            }, 100)

            voice.setOnClickListener {
                clickListener.onClickVoice()
            }

            this@SearchAdapter.rlSuggest = rlTextSuggest

            if (isVoice) {
                if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
                    voice.visibility = View.VISIBLE
                } else {
                    voice.visibility = View.GONE
                }
            } else {
                voice.visibility = View.INVISIBLE
            }

            txt_quantity.text = count.toString()

        }

        fun binding(keys: ArrayList<String>) {
            tv_result.text = mActivity.getString(R.string.text_top_keyword)

            suggestKeyWordAdapter = SuggestKeyWordAdapter(
                mActivity, keys,
                false
            ) {

            }

            vgSuggestText.adapter = suggestKeyWordAdapter
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_sub_home: HorizontalGridView = view.rv_sub_home
        var tv_sub_home: SfTextView = view.tv_sub_home
        val img_not_found: ImageView = view.img_not_found
        val space: View = view.space
    }

    override fun getItemCount(): Int {
        return 3
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onClickVoice()
        fun onClickSearch(query: String, isClickSearch: Boolean)
        fun onClickProduct(product: Product, position: Int)
        fun onClickStore(product: BrandShop, position: Int)
    }
}
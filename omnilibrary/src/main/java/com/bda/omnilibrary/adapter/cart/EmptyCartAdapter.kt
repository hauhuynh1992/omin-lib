package com.bda.omnilibrary.adapter.cart

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.adapter.homev2.ProductAdapterV2
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.views.SfTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_item_v2.view.*
import kotlinx.android.synthetic.main.item_header_cart_empty.view.*

class EmptyCartAdapter(
    activity: BaseActivity
) : BaseAdapter(activity) {
    private lateinit var mList: ArrayList<Product>
    private lateinit var productAdapter: ProductAdapterV2

    private lateinit var clickListener: OnCallBackListener

    private lateinit var continuousButton: RelativeLayout

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == R.layout.item_header_cart_empty) {
            v = getLayoutInflater()
                .inflate(R.layout.item_header_cart_empty, parent, false)
            HeaderItemViewHolder(v)
        } else {
            val sharedPool = RecyclerView.RecycledViewPool()
            v = getLayoutInflater()
                .inflate(R.layout.home_item_v2, parent, false)
            v.rv_sub_home.setRecycledViewPool(sharedPool)
            ItemViewHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_header_cart_empty
            else -> R.layout.home_item_v2
        }
    }

    fun setNewHighlight(list: ArrayList<Product>) {
        this.mList = list
        //notifyItemChanged(1)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        if (mholder is ItemViewHolder) {
            if (this::mList.isInitialized && mList.size > 0) {
                mholder.tv_sub_home.visibility = View.VISIBLE

                mholder.tv_sub_home.text = mActivity.getString(R.string.product_suggest)

                this.productAdapter =
                    ProductAdapterV2(
                        mActivity,
                        mList,
                        position,
                        false
                    )

                this.productAdapter.setOnItemClickListener(object :
                    ProductAdapterV2.OnItemClickListener {
                    override fun onItemClick(product: Product, position: Int) {
                        clickListener.onItemHighlightClick(product)
                    }

                    override fun onPositionListFocus(
                        product: Product?,
                        positionEnterList: Int,
                        position: Int
                    ) {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.CART.name
                        product?.let {
                            if (it.collection.size >= 1) {
                                dataObject.category = it.collection[0].collection_name
                                dataObject.categoryId = it.collection[0].uid
                                dataObject.itemCategoryId = it.collection[0].uid
                                dataObject.itemCategoryName =
                                    it.collection[0].collection_name
                            }
                        }
                        dataObject.sectionIndex = (position - 1).toString()
                        dataObject.itemName = product?.name
                        dataObject.itemIndex = position.toString()
                        dataObject.itemId = product?.uid.toString()
                        dataObject.itemBrand = product?.brand?.name
                        dataObject.itemListPriceVat = product?.price.toString()
                        val data = Gson().toJson(dataObject).toString()
                        mActivity.logTrackingVersion2(
                            QuickstartPreferences.HOVER_PRODUCT_v2,
                            data
                        )
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

                mholder.rv_sub_home.setPadding(
                    mActivity.resources.getDimension(R.dimen._18sdp).toInt(),
                    0,
                    mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                    0
                )

                mholder.rv_sub_home.horizontalSpacing =
                    mActivity.resources.getDimension(R.dimen._3sdp).toInt()
                mholder.rv_sub_home.adapter = productAdapter

            }

        } else if (mholder is HeaderItemViewHolder) {
            this.continuousButton = mholder.layout_continue

            mholder.layout_continue.setOnClickListener {
                clickListener.onContinuteClick()
            }
            mholder.layout_continue.setOnFocusChangeListener { _, hasFocus ->
                mholder.bn_shopping.isSelected = hasFocus
            }
        }
    }

    fun getContinuousButton(): RelativeLayout {
        return this.continuousButton
    }

    @Suppress("PropertyName")
    inner class HeaderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_shopping: SfTextView = view.bn_shopping_
        val layout_continue: RelativeLayout = view.layout_continue
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_sub_home: HorizontalGridView = view.rv_sub_home
        var tv_sub_home: SfTextView = view.tv_sub_home
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onItemHighlightClick(product: Product)
        fun onContinuteClick()
    }
}
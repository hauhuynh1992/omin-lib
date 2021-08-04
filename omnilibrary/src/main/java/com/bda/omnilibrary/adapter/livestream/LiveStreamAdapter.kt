package com.bda.omnilibrary.adapter.livestream

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.adapter.homev2.LiveAdapter
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.views.SfTextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_item_v2.view.*

class LiveStreamAdapter(
    activity: BaseActivity,
    liveStream: LiveStream,
    private val titleLive: String = "",
    private var live: List<LiveStream>?,
    private val titleLived: String = "",
    private var lived: List<LiveStream>?
) : BaseAdapter(activity) {

    private var mList: List<LiveStream.StreamProduct> = liveStream.products.map {
        it.copy()
    }

    private lateinit var itemLiveStreamAdapter: ItemLiveStreamAdapter
    private lateinit var liveAdapter: LiveAdapter

    private lateinit var clickListener: OnCallBackListener
    private var focusPosition = -1
    private var liveFocusPosition = -1

    private var highlightPosition = -1
    private var highlightPreviousPosition = -1

    private var rvProduct: HorizontalGridView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val sharedPool = RecyclerView.RecycledViewPool()
        val v: View = getLayoutInflater()
            .inflate(R.layout.item_live_stream_activity, parent, false)
        v.rv_sub_home.setRecycledViewPool(sharedPool)
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {

        val holder: ItemViewHolder = mholder as ItemViewHolder
        when (position) {
            0 -> {
                if (mList.isNotEmpty()) {
                    holder.tv_sub_home.visibility = View.GONE

                    this.itemLiveStreamAdapter =
                        ItemLiveStreamAdapter(
                            mActivity,
                            mList,
                            position
                        )

                    this.itemLiveStreamAdapter.setOnItemFocusListener(object :
                        ItemLiveStreamAdapter.OnFocusChangeListener {
                        override fun onItemProduct(
                            product: LiveStream.StreamProduct,
                            position: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
                            dataObject.itemName = product.product.name
                            dataObject.itemIndex = position.toString()
                            dataObject.itemId = product.product.uid
                            dataObject.itemBrand = product.product.brand.name
                            dataObject.itemListPriceVat = product.product.price.toString()
                            val data = Gson().toJson(dataObject).toString()
                            mActivity.logTrackingVersion2(
                                QuickstartPreferences.CLICK_PRODUCT_v2,
                                data
                            )
                            clickListener.onItemClick(product, 0, "")
                        }

                        override fun onPositionListFocus(
                            product: LiveStream.StreamProduct,
                            position: Int,
                            positionEnterList: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
                            dataObject.itemName = product.product.name
                            dataObject.itemIndex = position.toString()
                            dataObject.itemId = product.product.uid
                            dataObject.itemBrand = product.product.brand.name
                            dataObject.itemListPriceVat = product.product.price.toString()
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

                        override fun onFocus(position: Int, hasFocus: Boolean) {
                            clickListener.onFocusProducts(hasFocus)
                        }
                    })
                    if (position == 0) {
                        val params: LinearLayout.LayoutParams =
                            LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        params.setMargins(
                            0,
                            mActivity.resources.getDimension(R.dimen._175sdp).toInt(),
                            0,
                            0
                        )
                        holder.rv_sub_home.layoutParams = params
                    }

                    holder.rv_sub_home.setPadding(
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0,
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0
                    )

                    holder.rv_sub_home.adapter = itemLiveStreamAdapter
                    this.rvProduct = holder.rv_sub_home

                } else {
                    holder.layout_content_home.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
            1 -> {
                if (live != null && live!!.isNotEmpty()) {
                    holder.tv_sub_home.visibility = View.VISIBLE

                    holder.tv_sub_home.text = titleLive


                    this.liveAdapter =
                        LiveAdapter(
                            mActivity,
                            live!!,
                            position
                        )

                    this.liveAdapter.setOnItemClickListener(object :
                        LiveAdapter.OnItemClickListener {
                        override fun onItemClick(
                            product: LiveStream,
                            position: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
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
                                QuickstartPreferences.CLICK_HOT_DEAL_FROM_HOME
                            )
                        }

                        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                        override fun onPositionListFocus(
                            product: LiveStream,
                            mPosition: Int,
                            positionOnList: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
                            dataObject.liveStreamId = product.uid
                            dataObject.liveStreamName = product.name
                            val data = Gson().toJson(dataObject).toString()
                            mActivity.logTrackingVersion2(
                                QuickstartPreferences.HOVER_LIVESTREAM,
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

                    if (position == 1) {
                        val params: LinearLayout.LayoutParams =
                            LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        params.setMargins(
                            mActivity.resources.getDimension(R.dimen._30sdp).toInt(),
                            mActivity.resources.getDimension(R.dimen._155sdp).toInt(),
                            0,
                            0
                        )
                        holder.tv_sub_home.layoutParams = params
                    }

                    holder.rv_sub_home.setPadding(
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0,
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0
                    )
                    holder.rv_sub_home.horizontalSpacing =
                        mActivity.resources.getDimension(R.dimen._3sdp).toInt()

                    holder.rv_sub_home.adapter = liveAdapter

                } else {
                    if (lived != null && lived!!.isNotEmpty()) {
                        holder.tv_sub_home.visibility = View.VISIBLE

                        holder.tv_sub_home.text = titleLived


                        this.liveAdapter =
                            LiveAdapter(
                                mActivity,
                                lived!!,
                                position
                            )

                        this.liveAdapter.setOnItemClickListener(object :
                            LiveAdapter.OnItemClickListener {
                            override fun onItemClick(
                                product: LiveStream,
                                position: Int
                            ) {

                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
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
                            }

                            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                            override fun onPositionListFocus(
                                product: LiveStream,
                                position: Int,
                                positionOnList: Int
                            ) {
                                val dataObject = LogDataRequest()
                                dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
                                dataObject.liveStreamId = product.uid
                                dataObject.liveStreamName = product.name
                                val data = Gson().toJson(dataObject).toString()
                                mActivity.logTrackingVersion2(
                                    QuickstartPreferences.HOVER_LIVESTREAM,
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

                        if (position == 1) {
                            val params: LinearLayout.LayoutParams =
                                LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            params.setMargins(
                                mActivity.resources.getDimension(R.dimen._30sdp).toInt(),
                                mActivity.resources.getDimension(R.dimen._155sdp).toInt(),
                                0,
                                0
                            )
                            holder.tv_sub_home.layoutParams = params
                        }

                        holder.rv_sub_home.setPadding(
                            mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                            0,
                            mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                            0
                        )
                        holder.rv_sub_home.horizontalSpacing =
                            mActivity.resources.getDimension(R.dimen._3sdp).toInt()

                        holder.rv_sub_home.adapter = liveAdapter

                    } else {
                        holder.layout_content_home.layoutParams.height = 0
                        holder.itemView.visibility = View.GONE
                    }
                }
            }
            2 -> {
                if (lived != null && lived!!.isNotEmpty()) {
                    holder.tv_sub_home.visibility = View.VISIBLE
                    holder.tv_sub_home.text = titleLived

                    if (position == 2) {
                        val params: LinearLayout.LayoutParams =
                            LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        params.setMargins(
                            mActivity.resources.getDimension(R.dimen._30sdp).toInt(),
                            0,
                            0,
                            0
                        )
                        holder.tv_sub_home.layoutParams = params
                    }


                    this.liveAdapter =
                        LiveAdapter(
                            mActivity,
                            lived!!,
                            position
                        )

                    this.liveAdapter.setOnItemClickListener(object :
                        LiveAdapter.OnItemClickListener {
                        override fun onItemClick(
                            product: LiveStream,
                            position: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
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
                        }

                        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
                        override fun onPositionListFocus(
                            product: LiveStream,
                            position: Int,
                            positionOnList: Int
                        ) {
                            val dataObject = LogDataRequest()
                            dataObject.screen = Config.SCREEN_ID.LIVESTREAM_SCREEN.name
                            dataObject.liveStreamId = product.uid
                            dataObject.liveStreamName = product.name
                            val data = Gson().toJson(dataObject).toString()
                            mActivity.logTrackingVersion2(
                                QuickstartPreferences.HOVER_LIVESTREAM,
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

                    holder.rv_sub_home.setPadding(
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0,
                        mActivity.resources.getDimension(R.dimen._25sdp).toInt(),
                        0
                    )
                    holder.rv_sub_home.horizontalSpacing =
                        mActivity.resources.getDimension(R.dimen._3sdp).toInt()

                    holder.rv_sub_home.adapter = liveAdapter

                } else {
                    holder.layout_content_home.layoutParams.height = 0
                    holder.itemView.visibility = View.GONE
                }
            }
        }
    }

    fun getCurrentPosition(): Int {
        return focusPosition
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rv_sub_home: HorizontalGridView = view.rv_sub_home
        var tv_sub_home: SfTextView = view.tv_sub_home
        var layout_content_home: LinearLayout = view.layout_content_home
    }

    override fun getItemCount(): Int {
        return if (live != null && live!!.isNotEmpty() && lived != null && lived!!.isNotEmpty()) {
            3
        } else if ((live != null && live!!.isNotEmpty()) || (lived != null && lived!!.isNotEmpty())) {
            2
        } else {
            1
        }
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    fun highlight(index: Int) {
        if (rvProduct != null) {

            rvProduct!!.smoothScrollToPosition(index)

            // storage index
            highlightPreviousPosition = highlightPosition
            highlightPosition = index

            if (highlightPosition != -1) {
                mList[highlightPosition].isHighlight = true
                itemLiveStreamAdapter.notifyItemChanged(highlightPosition)
            }

            if (highlightPreviousPosition != -1) {
                mList[highlightPreviousPosition].isHighlight = false
                itemLiveStreamAdapter.notifyItemChanged(highlightPreviousPosition)
            }

        }
    }

    interface OnCallBackListener {
        fun onItemClick(product: LiveStream.StreamProduct, position: Int, clickFrom: String)

        fun onLiveStreamClick(product: LiveStream, position: Int, clickFrom: String)

        fun onFocusProducts(hasFocus: Boolean)
    }
}
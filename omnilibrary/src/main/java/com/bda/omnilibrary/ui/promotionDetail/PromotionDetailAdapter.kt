package com.bda.omnilibrary.ui.promotionDetail

import android.graphics.Color
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.dialog.ProductDescriptionDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Promotion
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.home_item_v2.view.*
import kotlinx.android.synthetic.main.item_brand_shop_detail_header.view.image_header
import kotlinx.android.synthetic.main.item_promotion_detail.view.*
import kotlinx.android.synthetic.main.item_promotion_detail_header.view.*

class PromotionDetailAdapter(
    private val activity: BaseActivity,
    private val data: Promotion
) : BaseAdapter(activity) {

    private lateinit var clickListener: OnCallBackListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        when (viewType) {
            R.layout.item_promotion_detail_header -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_promotion_detail_header, parent, false)
                return HeaderItemViewHolder(v)
            }
            else -> {
                v = getLayoutInflater()
                    .inflate(R.layout.item_promotion_detail, parent, false)
                return ItemViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> return R.layout.item_promotion_detail_header
            else -> R.layout.item_promotion_detail
        }
    }

    override fun onBindViewHolder(mholder: RecyclerView.ViewHolder, position: Int) {
        if (mholder is ItemViewHolder) {
            mholder.rl_description.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {

                    Functions.setStrokeFocus(mholder.cardview_description, mActivity)
                } else {

                    Functions.setLostFocus(mholder.cardview_description, mActivity)
                }
            }
            mholder.text_edit.text = data.button_direct?.action

            mholder.bn_understand.setOnClickListener {
                clickListener.onClickUnderstand()
            }

            mholder.bn_understand.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    activity?.let {
                        val textColorId = ContextCompat.getColorStateList(
                            it,
                            R.color.color_white
                        )
                        mholder.text_edit.setTextColor(textColorId)

                    }
                } else {
                    activity?.let {
                        val textColorId = ContextCompat.getColorStateList(
                            it,
                            R.color.title_black
                        )
                        mholder.text_edit.setTextColor(textColorId)
                    }
                }
            }

            data.description_html?.let {
                mholder.tv_description.text = Html.fromHtml(data.description_html)
            }

            mholder.rl_description.setOnClickListener {
                data.description_html?.let {
                    ProductDescriptionDialog(
                        mActivity,
                        data.description_html!!
                    )
                }

            }

        } else if (mholder is HeaderItemViewHolder) {
            mActivity.initChildHeader(mholder.image_header)
            ImageUtils.loadImage(
                activity,
                mholder.imageBanner,
                data.image_banner,
                ImageUtils.TYPE_HYBRID
            )
        }
    }

    inner class HeaderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageBanner: ImageView = view.imageBanner
        val image_header: View = view.image_header
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var bn_understand: RelativeLayout = view.findViewById(R.id.bn_understand)
        var text_edit: SfTextView = view.findViewById(R.id.text_edit)
        var tv_description: SfTextView = view.findViewById(R.id.tv_description)
        var layout_main: RelativeLayout = view.findViewById(R.id.layout_main)
        var rl_description: RelativeLayout = view.findViewById(R.id.rl_description)
        var cardview_description: MaterialCardView = view.findViewById(R.id.cardview_description)
    }

    override fun getItemCount(): Int {
        return 2
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onClickUnderstand()
    }
}
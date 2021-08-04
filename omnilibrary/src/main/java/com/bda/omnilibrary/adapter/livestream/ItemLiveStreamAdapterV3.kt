package com.bda.omnilibrary.adapter.livestream


import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_product_livestream_v3.view.*


class ItemLiveStreamAdapterV3(
    val activity: BaseActivity,
    val mList: List<LiveStream.StreamProduct>
) : BaseAdapter(activity) {
    private lateinit var clickListener: OnCallBackListener
    private var currentSelectedPosition = -1
    var isTopFocus: Boolean = false
    var numColumn: Int = mList.size
    var isLeftFocus: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_product_livestream_v3, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as ViewHolder).setData(mList[position].product)

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.tv_price.visibility = View.VISIBLE
                holder.tv_price.setNewTextColor(R.color.color_A1B753)
                Functions.animateScaleUpLiveStream(holder.layout_product_content, 1.05f)
                Functions.animateScaleUpLiveStream(holder.revealView, 1f, 200L)
                holder.revealView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.color_white
                    )
                )
                currentSelectedPosition = -1

                if (position < numColumn)
                    isTopFocus = true

                for (i in 0..itemCount / numColumn)
                    if (position == i * numColumn)
                        isLeftFocus = true

            } else {
                if (currentSelectedPosition == position) {
                    holder.revealView.setCardBackgroundColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_CCCCCC
                        )
                    )
                    holder.tv_price.setNewTextColor(R.color.color_999999)
                } else {
                    holder.itemView.tv_price.visibility = View.GONE
                    Functions.animateScaleDownLiveStream(holder.layout_product_content)
                    Functions.animateScaleDownLiveStream(holder.revealView, 100L)
                }

                isTopFocus = false
                isLeftFocus = false
            }
        }
        holder.itemView.setOnClickListener {
            currentSelectedPosition = position
            clickListener.onItemClick(mList[position])
        }
    }

    fun clearSelected() {
        currentSelectedPosition = -1
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_price: SfTextView = view.tv_price
        val layout_product_content: CardView = view.layout_product_content
        val revealView: CardView = view.revealView
        val image: ImageView = view.image
        val name: SfTextView = view.name

        fun setData(product: Product) {
            name.text = product.display_name_detail
            tv_price.text = Functions.formatMoney(product.price)
            ImageUtils.loadImage(activity, image, product.imageCover, ImageUtils.TYPE_PRIVIEW_SMALL)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    interface OnCallBackListener {
        fun onItemClick(product: LiveStream.StreamProduct)
    }
}
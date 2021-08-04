package com.bda.omnilibrary.adapter.livestream

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_tab_live_stream.view.*

class TabLiveStreamAdapter(
    val activity: BaseActivity,
    private val listTab: List<String>
) : BaseAdapter(activity) {
    private lateinit var clickListener: OnCallBackListener
    private var currentSelectedPosition = -1
    var isFirst: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_tab_live_stream, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as ViewHolder
        holder.image.setColorFilter(ContextCompat.getColor(activity, R.color.color_484848))

        when (listTab[position]) {
            "info" -> {
                holder.name.text = "Thông tin"
            }

            "product" -> {
                holder.name.text = "Sản phẩm"
                holder.image.setImageResource(R.mipmap.ic_product)

            }

            "video" -> {
                holder.name.text = "Video liên quan"
                holder.image.setImageResource(R.mipmap.ic_tv)
            }

            "voucher" -> {
                holder.name.text = "Vouchers"

            }

            "comment" -> {
                holder.name.text = "Comment"

            }
        }

        if (currentSelectedPosition == position) {
            holder.background.setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.color_33000000
                )
            )
            holder.name.setNewTextColor(R.color.color_white)
            holder.image.setColorFilter(ContextCompat.getColor(activity, R.color.color_white))
        } else {
            holder.background.setBackgroundColor(
                ContextCompat.getColor(
                    activity,
                    R.color.trans
                )
            )
            holder.name.setNewTextColor(R.color.color_484848)
            holder.image.setColorFilter(ContextCompat.getColor(activity, R.color.color_484848))
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUpLiveStream(holder.revealView, 1.05f)
                holder.background.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.color_FCFCFC
                    )
                )
                holder.name.setNewTextColor(R.color.color_484848)
                holder.image.setColorFilter(ContextCompat.getColor(activity, R.color.color_484848))

                if (position == 0) isFirst = true
            } else {

                Functions.animateScaleDownLiveStream(holder.revealView)
                isFirst = false

                if (currentSelectedPosition == position) {
                    holder.background.setBackgroundColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_33000000
                        )
                    )
                    holder.name.setNewTextColor(R.color.color_white)
                    holder.image.setColorFilter(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_white
                        )
                    )
                } else {
                    holder.background.setBackgroundColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.trans
                        )
                    )
                    holder.name.setNewTextColor(R.color.color_484848)
                    holder.image.setColorFilter(
                        ContextCompat.getColor(
                            activity,
                            R.color.color_484848
                        )
                    )
                }
            }
        }

        holder.itemView.setOnClickListener {
            val old = currentSelectedPosition
            currentSelectedPosition = position
            clickListener.onItemClick(position)

            if (old >= 0)
                notifyItemChanged(old)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.name
        val revealView: CardView = view.revealView
        val image: ImageView = view.image
        val background: LinearLayout = view.background_content
    }

    override fun getItemCount(): Int {
        return listTab.size
    }

    fun setOnCallbackListener(clickListener: OnCallBackListener) {
        this.clickListener = clickListener
    }

    fun setCurrentIndex(index: Int) {
        val old = currentSelectedPosition
        currentSelectedPosition = index
        notifyItemChanged(old)
        notifyItemChanged(currentSelectedPosition)
    }

    interface OnCallBackListener {
        fun onItemClick(p: Int)
    }
}
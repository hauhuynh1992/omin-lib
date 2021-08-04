package com.bda.omnilibrary.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfHtmlTextView
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.detail_info_item.view.*
import java.util.*


class DetailInfoAdapter(
    activity: BaseActivity,
    list: ArrayList<Product.Detail>
) :
    BaseAdapter(activity) {

    private var mList: ArrayList<Product.Detail> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.detail_info_item, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ViewHolder
        holder.title.text = mList[position].title
        holder.content.text = mList[position].content
        ImageUtils.loadImage(mActivity, holder.imageIconSpec, mList[position].icon)
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position])
        }
        holder.itemView.setOnFocusChangeListener { v: View, hasFocus: Boolean ->
            if (hasFocus) {
                v.background = shape
            } else {
                v.background = null
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageIconSpec: ImageView = view.image_ic_spec
        val title: SfTextView = view.tv_title
        val content: SfHtmlTextView = view.tv_content
    }

    private val shape = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(Color.TRANSPARENT)
        cornerRadius = activity.resources.getDimension(R.dimen._6sdp)
        setStroke(1, Color.WHITE)
    }

    override fun getItemCount(): Int {
        if (mList.size > 1) {
            return 1
        } else
            return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(Detail: Product.Detail)
    }
}
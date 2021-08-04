package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R

class ReviewOrderAdapter(
    activity: Activity,
    list: ArrayList<String>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<String> = list
    private var mActivity: Activity = activity
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.item_review_order, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemViewHolder
        holder.bind(mList.get(position))
        holder.bn_review.setOnClickListener {
            clickListener.onReviewProductClick(position)
        }

        holder.bn_detail.setOnClickListener {
            clickListener.onViewDetailClick(position)
        }

        holder.bn_re_order.setOnClickListener {
            clickListener.onReOrderItemClick(position)
        }


        holder.bn_review.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    holder.bn_review.setTextColor(textColorId)
                }
            } else {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    holder.bn_review.setTextColor(textColorId)
                }
            }
        }

        holder.bn_detail.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    holder.bn_detail.setTextColor(textColorId)
                }
            } else {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    holder.bn_detail.setTextColor(textColorId)
                }
            }
        }

        holder.bn_re_order.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    holder.bn_re_order.setTextColor(textColorId)
                }
            } else {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    holder.bn_re_order.setTextColor(textColorId)
                }
            }
        }

        holder.bn_detail.setOnFocusChangeListener { _, isFocus ->
            if (isFocus) {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    holder.bn_detail.setTextColor(textColorId)
                }
            } else {
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    holder.bn_detail.setTextColor(textColorId)
                }
            }
        }



        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.itemView.requestFocus()
                holder.itemView.invalidate()
                android.os.Handler().postDelayed({
                    holder.bn_review.requestFocus()
                }, 0)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView = view.findViewById(R.id.cardview)
        val layout_content: RelativeLayout = view.findViewById(R.id.layout_content)
        val img_product: ImageView = view.findViewById(R.id.img_product)
        val bn_review: Button = view.findViewById(R.id.bn_review)
        val bn_detail: Button = view.findViewById(R.id.bn_detail)
        val bn_re_order: Button = view.findViewById(R.id.bn_re_order)

        fun bind(
            mProduct: String
        ) {

//            mProduct.display_name_detail?.let {
//                tv_product_name.text = it
//            }
//
//            ImageUtils.loadImage(
//                mActivity,
//                img_product,
//                mProduct.imageHighlight,
//                ImageUtils.TYPE_CART
//            )
//
//            mProduct.price?.let {
//                tv_sell_price.text = Functions.formatMoney(it)
//            }
//
//            mProduct.listedPrice?.let {
//                tv_list_price.text = Functions.formatMoney(it)
//                tv_list_price.setPaintFlags(tv_list_price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
//            }
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    fun setData(data: ArrayList<String>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onViewDetailClick(position: Int)
        fun onReOrderItemClick(position: Int)
        fun onReviewProductClick(position: Int)
    }
}
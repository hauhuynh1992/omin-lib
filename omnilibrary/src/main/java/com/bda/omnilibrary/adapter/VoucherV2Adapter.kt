package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.item_voucher_v2.view.*
import java.util.*

class VoucherV2Adapter(
    activity: BaseActivity,
    list: ArrayList<Voucher>
) : BaseAdapter(activity) {
    private var mList: ArrayList<Voucher> = list
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_voucher_v2, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // binding
        (holder as ItemViewHolder).name.text = mList[position].voucher_label
        if (mList[position].condition_type_label.isNotBlank() && mList[position].condition_type != 0) {
            holder.description.visibility = View.VISIBLE
            holder.description.text =
                mList[position].condition_type_label + " " + Functions.format(mList[position].condition_value)

        } else if (mList[position].voucher_type == 1 && mList[position].max_applied_value > 0) {
            holder.description.visibility = View.VISIBLE
            holder.description.text =
                mActivity.getString(R.string.max_voucher_value) + " " + Functions.format(mList[position].max_applied_value)

        } else {
            holder.description.visibility = View.GONE
        }

        if (mList[position].voucher_type == 1) {
            holder.discount.text = "-" + mList[position].voucher_value.toInt().toString() + "%"
        } else {
            holder.discount.text = Functions.format(mList[position].voucher_value.toLong())
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            holder.text_bn_choose.isSelected = hasFocus


            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_bn_choose.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.end_color)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_bn_choose.outlineSpotShadowColor =
                        ContextCompat.getColor(mActivity, R.color.text_black_70)
                }
            }
        }

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(mList[position])
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val discount: SfTextView = view.discount

        val name: SfTextView = view.name
        val description: SfTextView = view.description
        val rl_con: RelativeLayout = view.rl_con
        val bn_choose: LinearLayout = view.bn_choose
        val item_bn_choose: MaterialCardView = view.item_bn_choose
        val text_bn_choose: SfTextView = view.text_bn_choose

    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    interface OnItemClickListener {
        fun onItemClick(v: Voucher)
    }
}
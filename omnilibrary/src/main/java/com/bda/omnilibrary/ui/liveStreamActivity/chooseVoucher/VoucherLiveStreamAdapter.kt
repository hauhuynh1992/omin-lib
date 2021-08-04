package com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_livestream_voucher.view.*

class VoucherLiveStreamAdapter(
    val activity: BaseActivity,
    val mList: List<Voucher>,
    val onClicked: (v: Voucher) -> Unit
) : BaseAdapter(activity) {
    var isFocusBottom = false
    var isFocusTop = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_livestream_voucher, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder)
        //(holder as ViewHolder).setData

        holder.name.text = mList[position].voucher_label
        if (mList[position].condition_type_label.isNotBlank() && mList[position].condition_type != 0) {
            holder.detail.visibility = View.VISIBLE
            holder.detail.text =
                mList[position].condition_type_label + " " + Functions.format(mList[position].condition_value)

        } else if (mList[position].voucher_type == 1 && mList[position].max_applied_value > 0) {
            holder.detail.visibility = View.VISIBLE
            holder.detail.text =
                mActivity.getString(R.string.max_voucher_value) + " " + Functions.format(mList[position].max_applied_value)

        } else {
            holder.detail.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClicked.invoke(mList[position])
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            holder.con.isSelected = hasFocus

            if (hasFocus) {
                if (position == 0) isFocusTop = true
                if (position == itemCount - 1) isFocusBottom = true

                Functions.animateScaleUp(holder.itemView, 1.05f)
            } else {
                isFocusTop = false
                isFocusBottom = false
                Functions.animateScaleDown(holder.itemView)
            }
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val detail: SfTextView = view.detail
        val image: ImageView = view.image
        val name: SfTextView = view.name
        val con: LinearLayout = view.con
    }


    override fun getItemCount(): Int {
        return mList.size
    }
}
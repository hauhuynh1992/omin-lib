package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import java.util.*

class CheckoutStep2Adapter(
    activity: BaseActivity,
    list: ArrayList<String>,
    val onClick: (int: String) -> Unit
) : BaseAdapter(activity) {
    private var mList: ArrayList<String> = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_checkout_step2, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder)

        when (mList[position]) {
            "momo" -> {
                holder.name.text = mActivity.getString(R.string.text_vi_momo)

                holder.image.setImageResource(R.mipmap.image_momo)
            }

            "vnpay" -> {
                holder.name.text = mActivity.getString(R.string.text_vi_vnpay)

                holder.image.setImageResource(R.mipmap.ic_vnpay)

            }

            "moca" -> {
                holder.name.text = mActivity.getString(R.string.text_vi_moca)

                holder.image.setImageResource(R.mipmap.ic_moca)

            }

            "cod" -> {
                holder.name.text = mActivity.getString(R.string.text_thanh_toan_cod)

                holder.image.setImageResource(R.mipmap.ic_cod)

            }
        }

        holder.itemView.setOnClickListener {
            onClick(mList[position])
        }

        holder.itemView.setOnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                Functions.setStrokeFocus(holder.con, mActivity)
                Functions.animateScaleUp(holder.itemView, 1.02f)

            } else {
                Functions.setLostFocus(holder.con, mActivity)
                Functions.animateScaleDown(holder.itemView)
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
        val image: ImageView = view.findViewById(R.id.image)
        val con: MaterialCardView = view.findViewById(R.id.con)
        val rl_con: RelativeLayout = view.findViewById(R.id.rl_con)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
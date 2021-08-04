package com.bda.omnilibrary.adapter.checkout

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView
import java.util.*

class PaymentTypeAdapter(
    activity: BaseActivity,
    list: ArrayList<Product>,
    val onClick: (int: Product) -> Unit
) : BaseAdapter(activity) {
    private var mList: ArrayList<Product> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_payment_type, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder)

        val params: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        if (position == 0) {
            params.setMargins(
                0,
                0,
                0,
                0
            )

        } else {
            params.setMargins(
                0,
                mActivity.resources.getDimension(R.dimen._minus8sdp).toInt(),
                0,
                0
            )
        }

        holder.rl_con.layoutParams = params

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
        val describe: SfTextView = view.findViewById(R.id.describe)

        val image_cover: ImageView = view.findViewById(R.id.image_cover)
        val image_left: ImageView = view.findViewById(R.id.image_left)

        val con: MaterialCardView = view.findViewById(R.id.con)
        val rl_con: RelativeLayout = view.findViewById(R.id.rl_con)
    }


    override fun getItemCount(): Int {
        return 3
    }

}
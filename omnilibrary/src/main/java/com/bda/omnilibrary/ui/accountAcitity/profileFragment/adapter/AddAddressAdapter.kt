package com.bda.omnilibrary.ui.accountAcitity.profileFragment.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfButton

class AddAddressAdapter(
    private val activity: Activity,
    private val onAddAddress: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = mInflater
            .inflate(R.layout.item_layout_add_address, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).itemView.setOnClickListener {
            onAddAddress.invoke()
        }

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                holder.bn_add.isSelected = true
                activity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    val bg = ContextCompat.getDrawable(
                        it,
                        R.drawable.background_button_product_active
                    )
                    holder.bn_add.setTextColor(textColorId)
                    holder.bn_add.background = bg


                }
            } else {
                holder.bn_add.isSelected = false
                activity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    val bg = ContextCompat.getDrawable(
                        it,
                        R.drawable.background_button_product_default
                    )
                    holder.bn_add.setTextColor(textColorId)
                    holder.bn_add.background = bg
                }
            }
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_add: SfButton = view.findViewById(R.id.bn_add)
    }
}
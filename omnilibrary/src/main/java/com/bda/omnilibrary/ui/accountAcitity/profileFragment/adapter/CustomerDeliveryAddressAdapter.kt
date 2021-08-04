package com.bda.omnilibrary.ui.accountAcitity.profileFragment.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.views.SfButton
import com.bda.omnilibrary.views.SfTextView

class CustomerDeliveryAddressAdapter(
    val mActivity: Activity,
    var items: ArrayList<ContactInfo>,
    val onChangeDelivery: (ContactInfo) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile, parent, false)
        view.isFocusableInTouchMode = true
        view.isFocusable = true
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = items.get(position)
        (holder as ProfileViewHolder).itemView.setOnClickListener {
            onChangeDelivery.invoke(data)
        }

        if (data.is_default_address) {
            holder.tv_address_default.visibility = View.VISIBLE
        } else {
            holder.tv_address_default.visibility = View.GONE
        }

        data.customer_name?.let {
            holder.tv_name.text = it
        }

        data.phone_number?.let {
            holder.tv_phone.text = it
        }

        data.phone_number?.let {
            holder.tv_phone.text = it
        }

        data.address?.let {
            holder.tv_address.text = it.address_des
        }

        holder.itemView.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                holder.bn_edit.isSelected = true
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.color_white
                    )
                    val bg = ContextCompat.getDrawable(
                        it,
                        R.drawable.background_button_product_active
                    )
                    holder.bn_edit.setTextColor(textColorId)
                    holder.bn_edit.background = bg


                }
            } else {
                holder.bn_edit.isSelected = false
                mActivity?.let {
                    val textColorId = ContextCompat.getColorStateList(
                        it,
                        R.color.title_black
                    )
                    val bg = ContextCompat.getDrawable(
                        it,
                        R.drawable.background_button_product_default
                    )
                    holder.bn_edit.setTextColor(textColorId)
                    holder.bn_edit.background = bg
                }
            }
        }
    }

    fun updateData(mList: ArrayList<ContactInfo>) {
        items.clear()
        items.addAll(mList)
        notifyDataSetChanged()
    }

    inner class ProfileViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val tv_name: SfTextView = view.findViewById(R.id.tv_name)
        val tv_phone: SfTextView = view.findViewById(R.id.tv_phone)
        val tv_address: SfTextView = view.findViewById(R.id.tv_address)
        val bn_edit: SfButton = view.findViewById(R.id.bn_edit)
        val tv_address_default: SfTextView = view.findViewById(R.id.tv_address_default)
    }


    override fun getItemCount(): Int {
        return items.size
    }
}
package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_layout_profile.view.*

class AltAddressAdapter(
    activity: BaseActivity,
    var userInfo: CustomerProfileResponse,
    var userInfoTemp: ArrayList<ContactInfo>,
    val onChooseDelivery: (uid: String, contactId: ContactInfo) -> Unit,
    val onAddDelivery: () -> Unit,
    val onDeleteDelivery: (uid: String, contactInfo: ContactInfo) -> Unit
) : BaseAdapter(activity) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return if (viewType == R.layout.item_layout_add_address) {
            v = getLayoutInflater()
                .inflate(R.layout.item_layout_add_address, parent, false)
            AddAddressViewHolder(v)
        } else {
            v = getLayoutInflater()
                .inflate(R.layout.item_alt_address_dialog, parent, false)
            DeliveryAddressViewHolder(v)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_layout_add_address
            else -> R.layout.item_alt_address_dialog
        }
    }

    override fun getItemCount(): Int {
        return if (userInfoTemp.size > 0) {
            userInfoTemp.size + 1
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddAddressViewHolder -> {
                holder.itemView.setOnClickListener {
                    onAddDelivery.invoke()
                }

                exportBnAddView = holder.itemView

                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_add.isSelected = true
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.color_white
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_add.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.end_color)
                            }
                            holder.text_edit.setTextColor(textColorId)


                        }
                    } else {
                        holder.bn_add.isSelected = false
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.title_black
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_add.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.text_black_30)
                            }
                            holder.text_edit.setTextColor(textColorId)

                        }
                    }
                }
            }
            is DeliveryAddressViewHolder -> {
                val data = userInfoTemp[position - 1]
                holder.bn_choose.setOnClickListener {
                    userInfo.let { info ->
                        onChooseDelivery.invoke(info.uid.toString(), data)
                    }
                }

                holder.bn_delete.setOnClickListener {
                    userInfo.let { info ->
                        onDeleteDelivery.invoke(info.uid.toString(), data)
                    }
                }

                if (data.is_default_address) {
                    holder.tv_address_default.visibility = View.VISIBLE
                } else {
                    holder.tv_address_default.visibility = View.GONE
                }

                data.customer_name.let {
                    holder.tv_name.text = it
                }

                data.phone_number.let {
                    holder.tv_phone.text = it
                }

                data.phone_number.let {
                    holder.tv_phone.text = it
                }

                data.address.let {
                    holder.tv_address.text = it.address_des
                }

                holder.bn_choose.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_choose.isSelected = true
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.color_white
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_choose.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.end_color)
                            }
                            holder.text_choose.setTextColor(textColorId)


                        }
                    } else {
                        holder.bn_choose.isSelected = false
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.title_black
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_choose.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.text_black_70)
                            }
                            holder.text_choose.setTextColor(textColorId)
                        }
                    }
                }

                holder.bn_delete.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_delete.isSelected = true
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.color_white
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_delete.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.end_color)
                            }
                            holder.text_delete.setTextColor(textColorId)


                        }
                    } else {
                        holder.bn_delete.isSelected = false
                        mActivity.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.title_black
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_delete.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.text_black_70)
                            }
                            holder.text_delete.setTextColor(textColorId)
                        }
                    }
                }

                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.itemView.requestFocus()
                        holder.itemView.invalidate()
                        android.os.Handler().postDelayed({
                            holder.bn_choose.requestFocus()
                        }, 0)
                    }
                }
            }
        }
    }

    fun removeAltId(id: String) {
        userInfoTemp.indexOfFirst {
            it.uid == id
        }.let { posistion ->
            if (posistion != -1) {
                userInfoTemp.removeAt(posistion)
                notifyItemRemoved(posistion + 1)
            }
        }
    }

    @Suppress("PropertyName")
    inner class DeliveryAddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: SfTextView = view.findViewById(R.id.tv_name)
        val tv_phone: SfTextView = view.findViewById(R.id.tv_phone)
        val tv_address: SfTextView = view.findViewById(R.id.tv_address)
        val bn_choose: RelativeLayout = view.findViewById(R.id.bn_choose)
        val bn_delete: RelativeLayout = view.findViewById(R.id.bn_delete)
        val tv_address_default: SfTextView = view.findViewById(R.id.tv_address_default)
        val text_choose: SfTextView = view.findViewById(R.id.text_bn_choose)
        val text_delete: SfTextView = view.findViewById(R.id.text_delete)
    }

    var exportBnAddView: View? = null

    @Suppress("PropertyName")
    inner class AddAddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_add: RelativeLayout = view.findViewById(R.id.bn_add)
        val text_edit: SfTextView = view.text_edit
    }
}
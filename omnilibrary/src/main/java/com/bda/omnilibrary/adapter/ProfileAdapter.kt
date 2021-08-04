package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.util.DateUtils
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_layout_profile.view.*

class ProfileAdapter(
    val mActivity: Activity,
    var userInfo: CheckCustomerResponse,
    var userInfoTemp: ArrayList<ContactInfo>,
    val onChangeDelivery: (ContactInfo) -> Unit,
    val onAddDelivery: () -> Unit,
    val onChangeUserInfo: (CheckCustomerResponse) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View
        return when (viewType) {
            R.layout.item_layout_profile -> {
                v = mInflater
                    .inflate(R.layout.item_layout_profile, parent, false)
                ProfileViewHolder(v)
            }
            R.layout.item_layout_add_address -> {
                v = mInflater
                    .inflate(R.layout.item_layout_add_address, parent, false)
                AddAddressViewHolder(v)
            }
            else -> {
                v = mInflater
                    .inflate(R.layout.item_profile, parent, false)
                DeliveryAddressViewHolder(v)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_layout_profile
            1 -> R.layout.item_layout_add_address
            else -> R.layout.item_profile
        }
    }

    override fun getItemCount(): Int {
        return if (userInfoTemp.size > 0) {
            userInfoTemp.size + 2
        } else {
            2
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProfileViewHolder -> {
                val data = userInfo
                holder.itemView.setOnClickListener {
                    onChangeUserInfo.invoke(data)
                }
                data.data.name?.let {
                    holder.tv_name.text = it
                }
                data.data.customer_phone?.let {
                    holder.tv_phone.text = it
                }


                when (data.data.gender) {
                    1 -> holder.tv_gender.text = mActivity.getString(R.string.text_nu)//"Nữ"
                    0 -> holder.tv_gender.text = mActivity.getString(R.string.text_nam)//"Nam"
                    else -> holder.tv_gender.text = mActivity.getString(R.string.text_khac)//"Khác"
                }

                data.data.email?.let {
                    holder.tv_email.text = it
                }


                data.data.dateOfBirth?.let {
                    if(!it.isNullOrBlank()){
                        holder.tv_birth_day.text = DateUtils.longTimeString(it.toLong(), "dd/MM/yyyy")
                    }
                }
                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_edit.isSelected = true
                        mActivity?.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.color_white
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_edit.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.end_color)
                            }
                            holder.text_edit.setTextColor(textColorId)


                        }
                    } else {
                        holder.bn_edit.isSelected = false
                        mActivity?.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.title_black
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_edit.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.text_black_70)
                            }
                            holder.text_edit.setTextColor(textColorId)

                        }
                    }
                }
            }
            is AddAddressViewHolder -> {
                holder.itemView.setOnClickListener {
                    onAddDelivery.invoke()
                }

                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_add.isSelected = true
                        mActivity?.let {
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
                        mActivity?.let {
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
                val data = userInfoTemp[position - 2]
                holder.itemView.setOnClickListener {
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

                holder.itemView.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        holder.bn_edit.isSelected = true
                        mActivity?.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.color_white
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_edit.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.end_color)
                            }
                            holder.text_edit.setTextColor(textColorId)


                        }
                    } else {
                        holder.bn_edit.isSelected = false
                        mActivity?.let {
                            val textColorId = ContextCompat.getColorStateList(
                                it,
                                R.color.title_black
                            )
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                holder.bn_edit.outlineSpotShadowColor =
                                    ContextCompat.getColor(mActivity, R.color.text_black_70)
                            }
                            holder.text_edit.setTextColor(textColorId)
                        }
                    }
                }
            }
        }
    }

    @Suppress("PropertyName")
    inner class ProfileViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val tv_name: SfTextView = view.findViewById(R.id.tv_name)
        val tv_phone: SfTextView = view.findViewById(R.id.tv_phone)
        val tv_email: SfTextView = view.findViewById(R.id.tv_email)
        val tv_gender: SfTextView = view.findViewById(R.id.tv_gender)
        val tv_birth_day: SfTextView = view.findViewById(R.id.tv_birth_day)
        val bn_edit: RelativeLayout = view.findViewById(R.id.bn_edit)
        val text_edit: SfTextView = view.text_edit
    }

    @Suppress("PropertyName")
    inner class DeliveryAddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_name: SfTextView = view.findViewById(R.id.tv_name)
        val tv_phone: SfTextView = view.findViewById(R.id.tv_phone)
        val tv_address: SfTextView = view.findViewById(R.id.tv_address)
        val bn_edit: RelativeLayout = view.findViewById(R.id.bn_edit)
        val tv_address_default: SfTextView = view.findViewById(R.id.tv_address_default)
        val text_edit: SfTextView = view.text_edit
    }

    @Suppress("PropertyName")
    inner class AddAddressViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bn_add: RelativeLayout = view.findViewById(R.id.bn_add)
        val text_edit: SfTextView = view.text_edit
    }
}
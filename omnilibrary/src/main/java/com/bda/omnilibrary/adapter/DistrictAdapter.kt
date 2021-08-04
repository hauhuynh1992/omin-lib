package com.bda.omnilibrary.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ProvinceDistrictModel


class DistrictAdapter(
    activity: Context,
    list: ArrayList<ProvinceDistrictModel.Data>,
    idDistrict: String,
    layout: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<ProvinceDistrictModel.Data> = list
    private var mActivity: Context = activity
    private var mIdDistrict = idDistrict
    private var mLayout = layout
    private var clickPosition = -1
    private lateinit var clickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(mLayout, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).rb_district.text = mList[position].name
        if (mList[position].uid == mIdDistrict) {
            mIdDistrict = ""
            clickListener.onItemClick(position)
            clickPosition = position
        }
        if (position == clickPosition) {
            Handler().postDelayed({
                holder.itemView.requestFocus()
            }, 0)

            holder.rb_district.setCompoundDrawablesWithIntrinsicBounds(
                R.mipmap.ic_radio_on_button,
                0,
                0,
                0
            )
        } else {

            holder.rb_district.setCompoundDrawablesWithIntrinsicBounds(
                R.mipmap.ic_radio_off_button,
                0,
                0,
                0
            )
        }
        holder.itemView.setOnClickListener {
            clickListener.onItemClick(position)
            clickPosition = position
            notifyDataSetChanged()
        }
        holder.itemView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.rb_district.background = rbFocusShape
            } else {
                holder.rb_district.background = null
            }
        }

    }

    private val rbFocusShape: GradientDrawable by lazy {
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.TRANSPARENT)
            cornerRadius = mActivity.resources.getDimension(R.dimen._3sdp)
            setStroke(2, Color.BLACK)
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mView: View = view
        val rb_district: TextView = view.findViewById(R.id.rb_district)
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    fun setOnItemClickListener(clickListener: OnItemClickListener) {
        this.clickListener = clickListener
    }

    fun setItemSelected(name: String) {
        val position = mList.indexOfFirst { it.name.toUpperCase().contains(name.toUpperCase()) }
        if (position == -1) {
            clickListener.onVoiceNoMatch()
        } else {
            clickListener.onItemClick(position)
            clickPosition = position
            notifyDataSetChanged()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onVoiceNoMatch()
    }
}
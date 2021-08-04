package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ProvinceDistrictModel
import com.bda.omnilibrary.model.Region

class SelectProvinceAdapter(
    activity: Activity,
    list: ArrayList<ProvinceDistrictModel.Data>,
    idDistrictProvince: String?,
    val onItemClick: (item: Region, position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<ProvinceDistrictModel.Data> = list
    private var currentPositionFocus: Int = 0
    private var mIdDistrictProvince = idDistrictProvince
    var clickPosition = -1
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.item_select_province, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        return ItemViewHolder(v)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).name.text = mList[position].name


        if (mIdDistrictProvince == mList[position].uid) {

            clickPosition = position
            val region = Region(
                uid = mList[position].uid,
                name = mList[position].name
            )
            onItemClick(region, position)
            mIdDistrictProvince = ""
        }
        holder.name.isChecked = position == clickPosition

        holder.itemView.setOnFocusChangeListener { _, b ->
            if (b) {
                currentPositionFocus = position
                holder.name.isSelected = true
                holder.itemView.bringToFront()
            } else {
                holder.name.isSelected = false
            }
        }

        holder.itemView.setOnClickListener {
            clickPosition = position
            val region = Region(
                uid = mList[position].uid,
                name = mList[position].name
            )
            onItemClick(region, position)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: RadioButton = view.findViewById(R.id.name)
    }

    fun setData(data: ArrayList<ProvinceDistrictModel.Data>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun getCurrentPositionFocus(): Int {
        return currentPositionFocus
    }
}
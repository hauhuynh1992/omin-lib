package com.bda.omnilibrary.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView
import com.google.android.material.card.MaterialCardView

class MenuOrderAdapter(
    private val activity: Activity,
    list: ArrayList<String>,
    private val onClickText: (position: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<String> = list
    private var currentPositionSelected: Int = 0
    private var msHandler: Handler = Handler()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.menu_order_item, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).name.text = mList[position]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            holder.item_content.outlineSpotShadowColor =
                ContextCompat.getColor(activity, R.color.trans)
            holder.item_content.outlineAmbientShadowColor =
                ContextCompat.getColor(activity, R.color.trans)
        }

        if (currentPositionSelected == position) {
            holder.name.setTextColor(ContextCompat.getColor(activity, R.color.end_color))
        } else {
            holder.name.setTextColor(
                ContextCompat.getColorStateList(
                    activity,
                    R.drawable.selector_collection_name
                )
            )
        }

        holder.itemView.setOnFocusChangeListener { _, b ->
            holder.name.isSelected = b
            if (b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_content.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.color_shadow)
                }
                if (currentPositionSelected != position) {
                    msHandler.removeCallbacksAndMessages(null)
                    msHandler.postDelayed({
                        onClickText.invoke(position)
                        currentPositionSelected = position
                    }, 1000)

                }
                if (position == currentPositionSelected)
                    holder.name.setTextColor(
                        ContextCompat.getColorStateList(
                            activity,
                            R.drawable.selector_collection_name
                        )
                    )
            } else {
                msHandler.removeCallbacksAndMessages(null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    holder.item_content.outlineSpotShadowColor =
                        ContextCompat.getColor(activity, R.color.trans)
                }

                if (position == currentPositionSelected)
                    holder.name.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.end_color
                        )
                    )
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
        val item_content: MaterialCardView = view.findViewById(R.id.item_content)
    }

    fun setData(data: ArrayList<String>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

}
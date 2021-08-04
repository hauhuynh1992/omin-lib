package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView

class SuggestAssistantAdapter(
    activity: Activity,
    list: ArrayList<String>,
    private val onClickText: (text: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<String> = list
    private var currentPositionFocus: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.suggest_assistant_item, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).name.text = mList[position]
        holder.itemView.setOnClickListener {
            onClickText.invoke(mList[position])
        }

        holder.itemView.setOnFocusChangeListener { _, b ->
            if (b) {
                currentPositionFocus = position
                holder.name.isSelected = true
            } else {
                holder.name.isSelected = false
            }
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
    }

    fun setData(data: ArrayList<String>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun getCurrentPositionFocus(): Int {
        return currentPositionFocus
    }

}
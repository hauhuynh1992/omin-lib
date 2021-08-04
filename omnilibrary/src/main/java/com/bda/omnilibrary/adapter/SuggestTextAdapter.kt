package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.SfTextView

class SuggestTextAdapter(
    activity: Activity,
    list: ArrayList<String>,
    isCallSearch: Boolean,
    // todo call on click
    private val onClickText: (text: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mIsCallSearch = isCallSearch
    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<String> = list
    private var mActivity: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.suggest_text_item, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return if (mIsCallSearch) {
            if (mList.size > 5) {
                5
            } else {
                mList.size
            }
        } else {
            mList.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).name.text = mList[position]
        holder.itemView.setOnClickListener {
            onClickText.invoke(mList[position])
        }
        if (mIsCallSearch) {
            holder.img_history_search.visibility = View.GONE
        } else {
            holder.img_history_search.visibility = View.VISIBLE
        }
        holder.itemView.setOnFocusChangeListener { view, b ->
            if (b) {
                holder.name.isSelected = true
                holder.img_history_search.isSelected = true
            } else {
                holder.name.isSelected = false
                holder.img_history_search.isSelected = false
            }
        }
    }

    @Suppress("PropertyName")
    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
        var img_history_search: ImageView = view.findViewById(R.id.img_history_search)
    }

}
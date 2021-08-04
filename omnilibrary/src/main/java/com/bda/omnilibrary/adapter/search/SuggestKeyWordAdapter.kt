package com.bda.omnilibrary.adapter.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.views.SfTextView

class SuggestKeyWordAdapter(
    activity: BaseActivity,
    list: ArrayList<String>,
    isCallSearch: Boolean,
    private val onClickText: (text: String) -> Unit
) : BaseAdapter(activity) {
    private var mIsCallSearch = isCallSearch
    private var mList: ArrayList<String> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_suggest_key_word, parent, false)
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
        holder.itemView.setOnFocusChangeListener { _, b ->
            holder.name.isSelected = b
        }
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
    }

}
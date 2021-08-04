package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Comment
import com.bda.omnilibrary.ui.baseActivity.BaseActivity

class CommentAdapter(
    activity: BaseActivity,
    list: ArrayList<Comment>,
    private val onClickText: (position: Int) -> Unit
) : BaseAdapter(activity) {
    private var mList: ArrayList<Comment> = list
    private var currentPositionFocus: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_comment, parent, false)
        v.isFocusableInTouchMode = true
        v.isFocusable = true
        val holder = ItemViewHolder(v)
        v.requestFocus()

        holder.itemView.setOnClickListener {
            mList[holder.absoluteAdapterPosition].isChecked =
                !mList[holder.absoluteAdapterPosition].isChecked
            notifyItemChanged(holder.absoluteAdapterPosition)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ItemViewHolder).name.text = mList[position].name

        holder.name.isChecked = mList[position].isChecked

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
        val name: CheckBox = view.findViewById(R.id.name)
    }

    fun setData(data: ArrayList<Comment>) {
        mList.clear()
        mList.addAll(data)
        notifyDataSetChanged()
    }

    fun getSelectComment(): List<String> {
        return mList.filter { it.isChecked }.map { it.name }
    }
}
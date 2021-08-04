package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.AddressSuggestResponce
import com.bda.omnilibrary.views.SfTextView


class SuggestAddressAdapter(
    activity: Activity,
    list: ArrayList<AddressSuggestResponce.Result>,
    // todo call on click
    private val onClickAddress: (result: AddressSuggestResponce.Result) -> Unit,
    // todo call new suggest address
    val onNewRequest: (query: String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    Filterable {

    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<AddressSuggestResponce.Result> = list
    private var mFilterList: ArrayList<AddressSuggestResponce.Result> = list
    private var mActivity: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.suggest_address_item, parent, false)
        return ItemViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (mFilterList.size > 0) {
            (holder as ItemViewHolder).name.text = mFilterList[position].formattedAddress
        }

        holder.itemView.setOnFocusChangeListener { _, hasFocus: Boolean ->
            if (hasFocus) {
                holder.itemView.background =
                    ContextCompat.getDrawable(mActivity, R.drawable.border_shape_white)
            } else {
                holder.itemView.background = null
            }
        }

        holder.itemView.setOnClickListener {
            onClickAddress.invoke(mList[position])
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    mFilterList = mList
                } else {
                    val filteredList = ArrayList<AddressSuggestResponce.Result>()
                    for (row in mList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.formattedAddress.toLowerCase().contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }

                    mFilterList = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = mFilterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mFilterList = results!!.values as ArrayList<AddressSuggestResponce.Result>

                if (mFilterList.size == 0) {
                    onNewRequest.invoke(constraint.toString())
                }

                notifyDataSetChanged()
            }
        }
    }

    fun updateData(list: ArrayList<AddressSuggestResponce.Result>) {
        mList = list
        mFilterList = list
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
    }

}
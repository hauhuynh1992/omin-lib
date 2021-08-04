package com.bda.omnilibrary.adapter

import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import java.util.*

class DeliveryDetailAdapter(
    activity: BaseActivity,
    list: ArrayList<ListOrderResponce.Data.Item>
) :
    BaseAdapter(activity) {
    private var mList: ArrayList<ListOrderResponce.Data.Item> = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_invoice_v1, parent, false)
        v.isFocusable = true
        v.isFocusableInTouchMode = true
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quantity = mList[position].quantity
        (holder as ItemViewHolder).name.text = mList[position].productName
        holder.quantity.text = quantity.toString()
        holder.price.text = Functions.formatMoney(mList[position].sell_price * quantity)
        holder.cv.visibility = View.GONE
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
        val quantity: SfTextView = view.findViewById(R.id.quantity)
        val price: SfTextView = view.findViewById(R.id.price)
        val cv: CardView = view.findViewById(R.id.cv_thumbnail)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}
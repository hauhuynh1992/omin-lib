package com.bda.omnilibrary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import java.util.*

class InvoiceAdapter(
    activity: Activity,
    list: ArrayList<Product>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mInflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var mList: ArrayList<Product> = list
    private var mActivity: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = mInflater
            .inflate(R.layout.item_invoice, parent, false)
        /*v.isFocusable = true
        v.isFocusableInTouchMode = true*/
        return ItemViewHolder(v)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val quantity = mList[position].order_quantity
        (holder as ItemViewHolder).name.text = mList[position].display_name_detail
        holder.quantity.text = quantity.toString()
        holder.price.text = Functions.formatMoney(mList[position].price * quantity)

    }


    /*private val shape = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setColor(Color.TRANSPARENT)
        cornerRadius = mActivity.resources.getDimension(R.dimen._6sdp)
        setStroke(1, Color.WHITE)
    }*/

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.findViewById(R.id.name)
        val quantity: SfTextView = view.findViewById(R.id.quantity)
        val price: SfTextView = view.findViewById(R.id.price)
    }


    override fun getItemCount(): Int {
        if (mList.size > 4) {
            return 4
        } else {
            return mList.size
        }
    }

}
package com.bda.omnilibrary.ui.liveStreamActivity.AddedListToCart

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.BaseAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.views.SfTextView
import kotlinx.android.synthetic.main.item_supplier_live_stream.view.*

class ItemSupplierAdapter(
    val activity: BaseActivity,
    val mList: ArrayList<Pair<String, ArrayList<Product>>>
) : BaseAdapter(activity) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = getLayoutInflater()
            .inflate(R.layout.item_supplier_live_stream, parent, false)
        v.isFocusable = false
        v.isFocusableInTouchMode = false
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).setData(mList[position])

        if (position == 0) {
            holder.bg.background =
                ContextCompat.getDrawable(activity, R.drawable.live_stream_detail_product_first_bg)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: SfTextView = view.supplier_name
        val list: RecyclerView = view.productList
        val bg: LinearLayout = view.bg

        fun setData(pair: Pair<String, ArrayList<Product>>) {
            name.text = pair.second[0].supplier.supplier_name
            list.adapter = ItemProductAdapter(activity, pair.second)
            list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        }
    }


    override fun getItemCount(): Int {
        return if (mList.size > 3) 3 else mList.size
    }
}
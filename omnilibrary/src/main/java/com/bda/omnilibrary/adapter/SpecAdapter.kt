package com.bda.omnilibrary.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfHtmlTextView
import com.bda.omnilibrary.views.SfTextView

class SpecAdapter(val activity: Activity, list: ArrayList<Product.Detail>) :
    PagerAdapter() {

    private var mViews: ArrayList<View?> = ArrayList()
    private var mList: ArrayList<Product.Detail> = list

    init {
        for (i in 0 until list.size) {
            mViews.add(null)
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return mList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context)
            .inflate(R.layout.specs_dialog_item, container, false)
        container.addView(view)
        bind(mList[position], view, position)

        val cardView: RelativeLayout = view.findViewById(R.id.cv_spec)

        mViews[position] = cardView

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
        mViews[position] = null
        //super.destroyItem(container, position, `object`)
    }

    private fun bind(item: Product.Detail, view: View, position: Int) {
        val icType: ImageView = view.findViewById(R.id.ic_type)
        val tvTitle: SfTextView = view.findViewById(R.id.tv_title)
        val tvContent: SfHtmlTextView = view.findViewById(R.id.tv_content)
        tvTitle.text = item.title
        tvContent.text = item.content
        ImageUtils.loadImage(activity,icType, item.icon)
    }

}
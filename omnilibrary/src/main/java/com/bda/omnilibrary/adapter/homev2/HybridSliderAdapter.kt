package com.bda.omnilibrary.adapter.homev2

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.HybridSection
import com.bda.omnilibrary.util.ImageUtils
import kotlinx.android.synthetic.main.image_slider_item.view.*

class HybridSliderAdapter(
    private val activity: Activity,
    private val list: ArrayList<HybridSection>,
) : PagerAdapter() {
    var size: Int? = null
    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view =
            LayoutInflater.from(container.context)
                .inflate(R.layout.image_slider_item, container, false)

        ImageUtils.loadImage(
            activity,
            view.iv_auto_image_slider,
            list[position].image,
            ImageUtils.TYPE_HYBRID
        )

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object` as View

    override fun destroyItem(parent: ViewGroup, position: Int, `object`: Any) =
        parent.removeView(`object` as View)

}
package com.bda.omnilibrary.ui.liveStreamActivity.detailLiveStreamProduct

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.FavouriteResponse
import com.bda.omnilibrary.model.FavouriteResquest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.addToCart.AddToCartFragment
import com.bda.omnilibrary.ui.liveStreamActivity.favourite.FavouriteFragment
import com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream.QuickPayLiveStreamFragment
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_item_detail_livestream.*

class DetailLiveStreamProductFragment : LiveStreamBaseFragment(), MotionLayout.TransitionListener,
    DetailLiveStreamProductContract.View {
    private lateinit var product: Product
    private var isPlayAnimation = false
    var isFavourite: Boolean = false
    private lateinit var presenter: DetailLiveStreamProductContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )
        }

        presenter = DetailLiveStreamProductPresenter(this, context!!)
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_detail_livestream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.loadFavourite(
            (activity as BaseActivity).getCheckCustomerResponse()!!.data.uid,
            product.uid
        )

        if (lastFocusId() == null) {
            if (product.is_disabled_quickpay)
                setLastFocusId(R.id.bn_add_to_cart)
            else
                setLastFocusId(R.id.bn_detail_quickpay)
        }

        lastFocus = view.findViewById(lastFocusId()!!)

        if (hasInCart()) {
            image_bn_add_to_cart.setImageResource(R.mipmap.ic_tick_green)
            image_bn_add_to_cart.setColorFilter(
                ContextCompat.getColor(
                    context!!,
                    R.color.color_484848
                )
            )
        }

        ImageUtils.loadImage(activity!!, detail_image, product.imageCover, ImageUtils.TYPE_PRODUCT)

        detail_name.text = product.display_name_detail

        if (product.tags != null && product.tags.size > 0) {
            for (tag in product.tags) {
                if (tag.tag_category == "name_tag"
                    && tag.name_tag_value_1 != null
                    && tag.name_tag_value_1 != ""
                ) {
                    ln_tag_value_1.visibility = View.VISIBLE
                    name_tag_value_1.text = "-${tag.name_tag_value_1}%"
                }
            }
        }

        detail_price.text = Functions.formatMoney(product.price)

        if (product.price < product.listedPrice)
            detail_list_price.text = Functions.formatMoney(product.listedPrice)


        bn_detail_quickpay?.apply {

            if (product.is_disabled_quickpay) {
                visibility = View.GONE
            }

            setOnClickListener {
                if (activity != null) {
                    (activity as BaseActivity).focusDummyView()
                    setLastFocusId(it.id)

                    val f = QuickPayLiveStreamFragment.newInstance(product)
                    (activity as BaseActivity).loadFragment(
                        f,
                        (activity as BaseActivity).layoutToLoadId(),
                        true

                    )
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (bn_detail_quickpay != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(bn_detail_quickpay, 1.05f)
                        text_bn_detail_quickpay.setNewTextColor(R.color.color_white)
                        image_bn_detail_quickpay.setColorFilter(Color.WHITE)

                    } else {
                        Functions.animateScaleDown(bn_detail_quickpay)
                        text_bn_detail_quickpay.setNewTextColor(R.color.color_A1B753)
                        image_bn_detail_quickpay.setColorFilter(
                            ContextCompat.getColor(
                                context!!,
                                R.color.color_A1B753
                            )
                        )
                    }
            }
        }




        if (product.short_descriptions.short_des_1 != null && product.short_descriptions.short_des_1.isNotEmpty()) {
            short_des_1.visibility = View.VISIBLE
            short_des_1.text = product.short_descriptions.short_des_1
            try {
                short_des_1.setTextColor(Color.parseColor(product.short_descriptions.short_des_1_color.takeIf { product.short_descriptions.short_des_1_color != null && product.short_descriptions.short_des_1_color.isNotEmpty() }
                    ?: "#999999"))
            } catch (e: IllegalArgumentException) {

            }
        }

        if (product.short_descriptions.short_des_2 != null && product.short_descriptions.short_des_2.isNotEmpty()) {
            short_des_2.visibility = View.VISIBLE
            short_des_2.text = product.short_descriptions.short_des_2
            try {
                short_des_2.setTextColor(Color.parseColor(product.short_descriptions.short_des_2_color.takeIf { product.short_descriptions.short_des_2_color != null && product.short_descriptions.short_des_2_color.isNotEmpty() }
                    ?: "#999999"))
            } catch (e: IllegalArgumentException) {

            }
        }

        if (product.short_descriptions.short_des_3 != null && product.short_descriptions.short_des_3.isNotEmpty()) {
            short_des_3.visibility = View.VISIBLE
            short_des_3.text = product.short_descriptions.short_des_3
            try {
                short_des_3.setTextColor(Color.parseColor(product.short_descriptions.short_des_3_color.takeIf { product.short_descriptions.short_des_3_color != null && product.short_descriptions.short_des_3_color.isNotEmpty() }
                    ?: "#999999"))
            } catch (e: IllegalArgumentException) {

            }
        }

        if (product.short_descriptions.short_des_4 != null && product.short_descriptions.short_des_4.isNotEmpty()) {
            short_des_4.visibility = View.VISIBLE
            short_des_4.text = product.short_descriptions.short_des_4
            try {
                short_des_4.setTextColor(Color.parseColor(product.short_descriptions.short_des_4_color.takeIf { product.short_descriptions.short_des_4_color != null && product.short_descriptions.short_des_4_color.isNotEmpty() }
                    ?: "#999999"))
            } catch (e: IllegalArgumentException) {

            }
        }


        bn_add_to_cart?.apply {
            setOnClickListener {
                (activity as BaseActivity).focusDummyView()
                setLastFocusId(it.id)

                (activity as BaseActivity).loadFragment(
                    AddToCartFragment.newInstance(product = product),
                    (activity as BaseActivity).layoutToLoadId(),
                    true
                )

                /*if (hasInCart()) {
                    (activity as BaseActivity).loadFragment(
                        AddedProductInCartFragment.newInstance(product = product),
                        (activity as BaseActivity).layoutToLoadId(),
                        true
                    )
                } else {
                    (activity as BaseActivity).loadFragment(
                        AddToCartFragment.newInstance(product = product),
                        (activity as BaseActivity).layoutToLoadId(),
                        true
                    )
                }*/
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (bn_add_to_cart != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(bn_add_to_cart, 1.05f)
                        text_bn_add_to_cart.setNewTextColor(R.color.color_white)
                        image_bn_add_to_cart.setColorFilter(Color.WHITE)
                    } else {
                        Functions.animateScaleDown(bn_add_to_cart)
                        text_bn_add_to_cart.setNewTextColor(R.color.color_484848)
                        image_bn_add_to_cart.setColorFilter(
                            ContextCompat.getColor(
                                context!!,
                                R.color.color_484848
                            )
                        )
                    }
            }
        }

        bn_favourite?.apply {
            setOnClickListener {
                if (this@DetailLiveStreamProductFragment.isFavourite) {
                    image_bn_favourite.setImageResource(R.mipmap.ic_fouverite_livestream)
                    presenter.postDeleteFavourite(
                        FavouriteResquest(
                            (activity as BaseActivity).getCheckCustomerResponse()!!.data.uid,
                            product.uid
                        )
                    )
                    this@DetailLiveStreamProductFragment.isFavourite = false
                } else {
                    (activity as BaseActivity).focusDummyView()
                    setLastFocusId(it.id)

                    val f =
                        FavouriteFragment.newInstance(product = product, isFavourite = isFavourite)
                    f.setListener(object : FavouriteFragment.FavouriteListener {
                        override fun onFavourite(isLike: Boolean) {
                            this@DetailLiveStreamProductFragment.isFavourite = isLike
                        }
                    })

                    (activity as BaseActivity).loadFragment(
                        f,
                        (activity as BaseActivity).layoutToLoadId(),
                        true
                    )
                }
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (bn_favourite != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(bn_favourite, 1.05f)
                        image_bn_favourite.setColorFilter(Color.WHITE)
                    } else {
                        Functions.animateScaleDown(bn_favourite)
                        if (isFavourite) {
                            image_bn_favourite.setColorFilter(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.color_A1B753
                                )
                            )
                        } else {
                            image_bn_favourite.setColorFilter(
                                ContextCompat.getColor(
                                    context!!,
                                    R.color.color_484848
                                )
                            )
                        }
                    }
            }
        }

        if (product.brand_shop != null && product.brand_shop!!.size > 0) {
            ImageUtils.loadImage(
                activity!!,
                image_supplier,
                product.brand_shop!![0].skin_image,
                ImageUtils.TYPE_BRAND_SHOP_AVATAR
            )
            detail_supplier_name.text = product.brand_shop!![0].display_name_in_product
            supplier_number_count.text = getString(
                R.string.live_stream_product_count,
                Functions.format(product.brand_shop!![0].product_counts.toLong())
            )
        } else {
            detail_supplier.visibility = View.GONE
            bn_detail.visibility = View.GONE

            bn_add_to_cart.nextFocusDownId = bn_add_to_cart.id
            bn_favourite.nextFocusDownId = bn_favourite.id
        }

        detail_supplier?.apply {
            setOnClickListener {
                setLastFocusId(it.id)
                (activity as BaseActivity).gotoBrandShopDetail(product.brand_shop!![0].uid)
            }

            setOnFocusChangeListener { _, hasFocus ->
                if (detail_supplier != null)
                    if (hasFocus) {
                        Functions.animateScaleUp(detail_supplier, 1.05f)
                        ic_focus_supplier.visibility = View.VISIBLE
                    } else {
                        Functions.animateScaleDown(detail_supplier)
                        ic_focus_supplier.visibility = View.GONE
                    }
            }
        }
    }

    fun myOnkeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP
            && detail_supplier.visibility == View.VISIBLE
            && !isPlayAnimation
            && ((bn_detail_quickpay != null && bn_detail_quickpay.hasFocus())
                    || ((bn_add_to_cart != null && bn_add_to_cart.hasFocus()
                    && bn_detail_quickpay != null
                    && bn_detail_quickpay?.visibility != View.VISIBLE)
                    || (bn_favourite != null
                    && bn_favourite.hasFocus()
                    && bn_detail_quickpay != null
                    && bn_detail_quickpay?.visibility != View.VISIBLE)
                    ))
        ) {
            showInfo()
            return true

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN
            && bn_add_to_cart != null
            && bn_favourite != null
            && (bn_add_to_cart.hasFocus() || bn_favourite.hasFocus())
            && detail_supplier?.visibility == View.VISIBLE
            && !isPlayAnimation
        ) {
            hideInfo()
        }

        return false
    }

    private fun hasInCart(): Boolean {
        val cart = (activity as BaseActivity).getDatabaseHandler()?.getLProductList()
        if (cart == null || cart.size == 0)
            return false

        for (i in cart)
            if (i.uid == product.uid)
                return true

        return false
    }

    override fun onDestroy() {
        presenter?.disposeAPI()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            lastFocus?.requestFocus()
        }, 300)
    }

    override fun isAbleToHideByClickLeft(): Boolean {
        return bn_detail_quickpay != null && bn_add_to_cart != null
                && bn_detail != null && detail_supplier != null
                && (bn_detail_quickpay.hasFocus() || bn_add_to_cart.hasFocus() || bn_detail.hasFocus() || detail_supplier.hasFocus())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GOTO_DETAIL_INVOICE_REQUEST -> {
                    val orderId = data!!.getStringExtra("ORDER_ID")
                    //deliveryFilterAdapter.deleteOrderById(orderId)
                }
            }
        }
    }

    private fun hideInfo() {
        if (!isPlayAnimation)
            motionLayout?.let {
                it.setTransition(R.id.transition_hiding_info)
                it.setTransitionListener(this)
                it.transitionToEnd()
            }
    }

    private fun showInfo() {
        if (!isPlayAnimation)
            motionLayout?.let {
                it.setTransition(R.id.transition_showing_info)
                it.addTransitionListener(this)
                it.transitionToEnd()
            }
    }

    companion object {
        val GOTO_DETAIL_INVOICE_REQUEST = 123
        private val STR_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            DetailLiveStreamProductFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                }
            }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        isPlayAnimation = true
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        isPlayAnimation = false
    }

    override fun sendLoadFavouriteSuccess(response: FavouriteResponse) {
        isFavourite = true
        image_bn_favourite.setImageResource(R.mipmap.ic_favourite_hurt)
    }

}
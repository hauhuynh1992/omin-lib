package com.bda.omnilibrary.ui.liveStreamActivity.addToCart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.addedProductInCart.AddedProductInCartFragment
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_add_to_cart.*

class AddToCartFragment : LiveStreamBaseFragment() {
    private lateinit var product: Product
    private var quatity: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_to_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        price?.text = Functions.formatMoney(product.price)
        name?.text = product.display_name_detail

        ImageUtils.loadImage(activity!!, image, product.imageCover, ImageUtils.TYPE_PRIVIEW_SMALL)

        setSetQuantity(quatity)

        bn_minus?.setOnClickListener {
            if (quatity > 1)
                setSetQuantity(--quatity)
        }

        bn_minus?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUpLiveStream(rl_quantity, 1.05f)
                image_bn_minus?.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_A1B753
                    )
                )
                rl_quantity?.isSelected = true
            } else {
                image_bn_minus?.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_484848
                    )
                )

                if (!bn_minus.hasFocus() && !bn_plus.hasFocus()) {
                    Functions.animateScaleDown(rl_quantity)
                    rl_quantity?.isSelected = false
                }

            }
        }

        bn_plus?.setOnClickListener {
            setSetQuantity(++quatity)
        }

        bn_plus?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUpLiveStream(rl_quantity, 1.05f)

                image_bn_plus?.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_A1B753
                    )
                )
                rl_quantity?.isSelected = true
            } else {
                image_bn_plus?.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_484848
                    )
                )

                if (!bn_minus.hasFocus() && !bn_plus.hasFocus()) {
                    rl_quantity?.isSelected = false
                    Functions.animateScaleDown(rl_quantity)
                }
            }
        }

        bn_confirm?.setOnClickListener {
            (activity as BaseActivity).focusDummyView()

            product.order_quantity = quatity
            (activity as BaseActivity).addItemToCart(product)

            (activity as BaseActivity).loadFragment(
                AddedProductInCartFragment.newInstance(
                    product
                ),
                (activity as BaseActivity).layoutToLoadId(),
                true
            )
            //(activity as BaseActivity).getFManager().po
        }

        bn_confirm?.setOnFocusChangeListener { _, hasFocus ->
            if (bn_confirm != null)
                if (hasFocus) {
                    Functions.animateScaleUp(bn_confirm, 1.05f)
                    text_bn_confirm.setNewTextColor(R.color.color_white)
                } else {
                    Functions.animateScaleDown(bn_confirm)
                    text_bn_confirm.setNewTextColor(R.color.color_A1B753)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    private fun setSetQuantity(q: Int) {
        quantity.text = "$q"
    }

    override fun requestFocus() {
        Handler().postDelayed({
            bn_confirm?.requestFocus()
        }, 100)
    }

    override fun isAbleToHideByClickLeft() = bn_minus.hasFocus() || bn_confirm.hasFocus()

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

    companion object {
        val GOTO_DETAIL_INVOICE_REQUEST = 123
        private val STR_PRODUCT = "product"

        @JvmStatic
        fun newInstance(product: Product) =
            AddToCartFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                }
            }
    }

}
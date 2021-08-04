package com.bda.omnilibrary.ui.liveStreamActivity.addedProductInCart

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.favourite.ItemFavouriteAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_added_product_in_cart.*

class AddedProductInCartFragment : LiveStreamBaseFragment() {
    private lateinit var product: Product
    private lateinit var mAdapter: ItemFavouriteAdapter
    private var timer: CountDownTimer? = null

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
        return inflater.inflate(R.layout.fragment_added_product_in_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        mAdapter = ItemFavouriteAdapter(activity as BaseActivity, arrayListOf(product))
        voucher_list?.apply {
            adapter = mAdapter
        }

        timer = (activity as BaseActivity).startTimer({
            (activity as BaseActivity).focusDummyView()
            // todo change, dumb
            (activity as BaseActivity).getFManager().popBackStack()
            (activity as BaseActivity).getFManager().popBackStack()
        }) {
            text_bn_detail?.text = getString(R.string.hiding_after_moment, it)
        }

        bn_detail?.setOnClickListener {
            //todo something
        }

        /*bn_detail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_detail, 1.05f)
                text_bn_detail.setNewTextColor(R.color.color_484848)
            } else {
                Functions.animateScaleDown(bn_detail)
                text_bn_detail.setNewTextColor(R.color.text_color_gray)
            }
        }*/
    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            bn_detail?.requestFocus()
        }, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (timer != null)
            timer!!.cancel()
    }

    override fun isAbleToHideByClickLeft() = true

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
            AddedProductInCartFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                }
            }
    }
}
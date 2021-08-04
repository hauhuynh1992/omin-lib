package com.bda.omnilibrary.ui.liveStreamActivity.successOrder

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher.VoucherLiveStreamAdapter
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_live_stream_success.*

class SuccessFragment : LiveStreamBaseFragment() {

    private lateinit var mAdapter: VoucherLiveStreamAdapter
    private lateinit var oid: String
    private lateinit var product: Product
    private lateinit var price: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            oid = it.getString(STR_OID) ?: ""
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )
            price = it.getString(STR_PRICE) ?: ""
        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_stream_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (product.brand_shop != null && product.brand_shop!!.size > 0) {
            supplier_name.text = product.brand_shop!![0].display_name_in_product
        } else {
            supplier_name.text = product.supplier.supplier_name
        }

        _oid.text = getString(R.string.oder_id, oid)

        quantity.text = getString(R.string.count_products, product.order_quantity.toString())

        _price.text = price

        ship_time.text = Functions.shippingTime(arrayListOf(product))

        mAdapter = VoucherLiveStreamAdapter(
            activity = activity as BaseActivity,
            mList = arrayListOf(),
            onClicked = {

            }
        )

        bn_confirm?.setOnClickListener {

            (activity as BaseActivity).hideDetailProduct()
        }

        bn_confirm.setOnFocusChangeListener { _, hasFocus ->
            if (bn_confirm != null)
                if (hasFocus) {
                    Functions.animateScaleUp(bn_confirm, 1.05f)
                    text_bn_confirm.setNewTextColor(R.color.color_white)
                } else {
                    Functions.animateScaleDown(bn_confirm)
                    text_bn_confirm.setNewTextColor(R.color.color_A1B753)
                }
        }

        bn_detail.setOnClickListener {
            (activity as BaseActivity).gotoAccount(true)
        }

        bn_detail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_detail, 1.05f)
                text_bn_detail.setNewTextColor(R.color.text_color_gray)
            } else {
                Functions.animateScaleDown(bn_detail)
                text_bn_detail.setNewTextColor(R.color.color_484848)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            bn_confirm?.requestFocus()
        }, 100)
    }

    override fun isAbleToHideByClickLeft() = true


    companion object {
        private val STR_OID = "uid"
        private val STR_PRODUCT = "product"
        private val STR_PRICE = "price"

        @JvmStatic
        fun newInstance(uid: String, product: Product, price: String) =
            SuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_OID, uid)
                    putString(STR_PRODUCT, Gson().toJson(product))
                    putString(STR_PRICE, price)
                }
            }
    }

}
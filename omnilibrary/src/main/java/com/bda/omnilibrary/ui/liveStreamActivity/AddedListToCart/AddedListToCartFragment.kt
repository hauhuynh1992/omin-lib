package com.bda.omnilibrary.ui.liveStreamActivity.AddedListToCart

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
import com.bda.omnilibrary.ui.liveStreamActivity.quickPayLiveStream.QuickPayLiveStreamFragment
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_added_to_cart.*

class AddedListToCartFragment : LiveStreamBaseFragment() {
    private lateinit var product: Product

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
        return inflater.inflate(R.layout.fragment_added_to_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartList = (activity as BaseActivity).getDatabaseHandler()!!.getLProductList()

        list.adapter = ItemSupplierAdapter(activity as BaseActivity, sortListBySupplier(cartList))


        Handler().postDelayed({
            list.invalidate()
        }, 100)

        bn_detail_quickpay.setOnClickListener {
            (activity as BaseActivity).focusDummyView()
            (activity as BaseActivity).loadFragment(
                QuickPayLiveStreamFragment.newInstance(product),
                (activity as BaseActivity).layoutToLoadId(), true
            )
        }

        bn_detail_quickpay.setOnFocusChangeListener { _, hasFocus ->
            if (bn_detail_quickpay != null)
            if (hasFocus) {
                Functions.animateScaleUp(bn_detail_quickpay, 1.05f)
                text_bn_detail_quickpay.setNewTextColor(R.color.color_white)
                image_bn_detail_quickpay.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_white
                    )
                )
            } else {
                Functions.animateScaleDown(bn_detail_quickpay)
                text_bn_detail_quickpay.setNewTextColor(R.color.color_A1B753)
                image_bn_detail_quickpay.setColorFilter(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.color_A1B753
                    )
                )
            }
        }
    }


    private fun sortListBySupplier(list: ArrayList<Product>): ArrayList<Pair<String, ArrayList<Product>>> {
        val pairList = ArrayList<Pair<String, ArrayList<Product>>>()

        for (i in 0 until list.size)
            if (list[i].uid == product.uid) {
                product = list[i]
                list.removeAt(i)
                break
            }

        list.add(0, product)

        for (i in list) {
            val mList = Functions.findPairList(i.supplier.supplier_id, pairList)

            if (mList != null) {
                mList.add(i)
            } else {
                pairList.add(Pair(i.supplier.supplier_id, arrayListOf(i)))
            }
        }

        return pairList
    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            bn_detail_quickpay?.requestFocus()
        }, 100)
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
            AddedListToCartFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                }
            }
    }

}
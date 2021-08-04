package com.bda.omnilibrary.ui.liveStreamActivity.paymentMethod

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher.ChooseVoucherFragment
import com.bda.omnilibrary.ui.liveStreamActivity.successOrder.SuccessFragment
import com.bda.omnilibrary.util.Config.freeShipValue
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_payment_method_live_stream.*

class PaymentMethodFragment : LiveStreamBaseFragment(), PaymentMethodLiveContract.View {

    private lateinit var product: Product
    private var name: String = ""
    private var phone: String = ""
    private var mVoucher: Voucher? = null
    private lateinit var presenter: PaymentMethodLiveContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )

            name = it.getString(STR_NAME) ?: ""
            phone = it.getString(STR_PHONE) ?: ""
        }

        presenter = PaymentMethodLivePresenter(this, context!!)
        //setFragmentResultListener()
    }

    override fun onDestroy() {
        presenter?.disposeAPI()
        super.onDestroy()
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_method_live_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bn_confirm?.setOnClickListener {
            val v = if (mVoucher == null) "" else mVoucher!!.voucher_code
            presenter.loadPresenter(name, phone, v, "", 2, arrayListOf(product))
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

        price.text = Functions.formatMoney(product.price * product.order_quantity)

        bn_choose_voucher.setOnClickListener {

            val f = ChooseVoucherFragment.newInstance(product)
            f.setListener(object : ChooseVoucherFragment.ChooseVoucherListener {
                override fun onChoose(voucher: Voucher) {
                    this@PaymentMethodFragment.mVoucher = voucher
                }

            })

            (activity as BaseActivity).focusDummyView()

            (activity as BaseActivity).loadFragment(
                f,
                (activity as BaseActivity).layoutToLoadId(),
                true
            )
        }

        bn_choose_voucher.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_choose_voucher, 1.05f)
                text_bn_choose_voucher.setNewTextColor(R.color.color_white)
            } else {
                Functions.animateScaleDown(bn_choose_voucher)
                text_bn_choose_voucher.setNewTextColor(R.color.color_484848)
            }
        }

        /*bn_cod.setOnClickListener {
            // todo
        }*/

        bn_cod?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_cod, 1.05f)
                //text_bn_choose_voucher.setNewTextColor(R.color.color_white)
            } else {
                Functions.animateScaleDown(bn_cod)
                //text_bn_choose_voucher.setNewTextColor(R.color.color_A1B753)
            }
        }
    }

    private fun totalPrice(): String {
        var voucherValue = 0.0

        if (mVoucher != null) voucherValue = mVoucher!!.applied_value

        voucher.text =
            if (voucherValue > 0) "-${Functions.formatMoney(voucherValue)}" else Functions.formatMoney(
                voucherValue
            )

        val total = product.price * product.order_quantity - voucherValue

        if (total >= freeShipValue) {
            ship.text = getString(R.string.text_mien_phi)
        } else {
            ship.text = getString(R.string.text_xac_dinh_sau)
        }

        return Functions.formatMoney(total)
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({ bn_confirm?.requestFocus() }, 100)

        total_vat?.text = totalPrice()
        text_bn_choose_voucher?.text =
            if (mVoucher != null) mVoucher!!.voucher_code else getString(R.string.choose_fill_voucher)

    }

    override fun requestFocus() {
        //Handler().postDelayed({ bn_confirm.requestFocus() }, 0)
    }

    override fun isAbleToHideByClickLeft() = true

    fun myOnkeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

        }


        return false
    }


    companion object {
        private val STR_PRODUCT = "product"
        private val STR_NAME = "name"
        private val STR_PHONE = "phone"


        @JvmStatic
        fun newInstance(product: Product, name: String, phone: String) =
            PaymentMethodFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                    putString(STR_NAME, name)
                    putString(STR_PHONE, phone)
                }
            }
    }

    override fun sendSuccess(Oid: String) {
        val f = SuccessFragment.newInstance(Oid, product, totalPrice())
        (activity as BaseActivity).focusDummyView()
        //bn_confirm?.clearFocus()
        (activity as BaseActivity).loadFragment(
            f,
            (activity as BaseActivity).layoutToLoadId(),
            true
        )
    }

    override fun sendFalsed(message: String) {
        Functions.showMessage(context!!, message)
    }

}
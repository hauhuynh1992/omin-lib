package com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.model.BestVoucherForCartResponse
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_choose_voucher_live_stream.*

class ChooseVoucherFragment : LiveStreamBaseFragment(), ChooseVoucherContract.View {

    private var mAdapter: VoucherLiveStreamAdapter? = null
    private var listener: ChooseVoucherListener? = null
    private lateinit var product: Product
    private lateinit var presenter: ChooseVoucherContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )
        }
        presenter = ChooseVoucherPresenter(this, context!!)
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_voucher_live_stream, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.loadVaucher((activity as BaseActivity).getCheckCustomerResponse()!!.data.uid)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bn_confirm.setOnClickListener {
            if (edt_voucher.text.isNotEmpty()) {
                presenter.checkVoucher(product, edt_voucher.text.toString())
            } else {
                Functions.showMessage(context!!, getString(R.string.nhap_voucher))
                Handler().postDelayed({
                    edt_voucher.requestFocus()
                }, 0)
            }

        }

        bn_confirm.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_confirm, 1.05f)
                text_bn_confirm.setNewTextColor(R.color.color_white)
            } else {
                Functions.animateScaleDown(bn_confirm)
                text_bn_confirm.setNewTextColor(R.color.color_A1B753)
            }
        }

        edt_voucher.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_voucher.text.toString(),
                edt_voucher, edt_voucher
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_voucher.setOnFocusChangeListener { _, hasFocus ->
            if (edt_voucher != null)
                if (hasFocus) {
                    Functions.animateScaleUp(edt_voucher, 1.1f)
                    edt_voucher.setSelection(edt_voucher.text.length)
                } else {
                    Functions.animateScaleDown(edt_voucher)
                }
        }

        edt_voucher.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                return@OnKeyListener true
            }

            false
        })
    }

    fun setListener(listener: ChooseVoucherListener) {
        this.listener = listener
    }

    fun myOnkeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && voucher_list != null && voucher_list.hasFocus()
            && voucher_list.visibility == View.VISIBLE
        ) {
            if (mAdapter != null && mAdapter!!.isFocusBottom) {
                bn_confirm.requestFocus()
                return true
            }
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && voucher_list != null
            && voucher_list.hasFocus() && voucher_list.visibility == View.VISIBLE
        ) {
            if (mAdapter != null && mAdapter!!.isFocusTop) {
                edt_voucher.requestFocus()
                return true
            }
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && voucher_list.hasFocus() && voucher_list.visibility == View.VISIBLE) {
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && voucher_list.hasFocus() && voucher_list.visibility == View.VISIBLE) {
            return true
        }

        return false
    }

    override fun onDestroy() {
        presenter.disposeAPI()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            edt_voucher?.requestFocus()
        }, 0)
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
        fun newInstance(p: Product) =
            ChooseVoucherFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(p))
                }
            }
    }

    interface ChooseVoucherListener {
        fun onChoose(voucher: Voucher)
    }

    override fun sendListVoucherSuccess(vouchers: ArrayList<Voucher>) {
        if (vouchers.size > 0) {
            text_choose_voucher.visibility = View.VISIBLE
            mAdapter = VoucherLiveStreamAdapter((activity as BaseActivity), vouchers) { voucher ->
                presenter.checkVoucher(product, voucher.voucher_code)
            }

            voucher_list.apply {
                adapter = mAdapter
            }

        } else {
            text_choose_voucher.visibility = View.GONE
            voucher_list.visibility = View.GONE

            edt_voucher.nextFocusDownId = bn_confirm.id
            bn_confirm.nextFocusUpId = edt_voucher.id
        }
    }

    override fun sendApplyVoucherSuccess(response: BestVoucherForCartResponse) {
        (activity as BaseActivity).focusDummyView()
        (activity as BaseActivity).getFManager().popBackStack()
        listener?.onChoose(response.data!!)
    }

    override fun sendApplyVoucherFalsed(message: String) {
        Functions.showMessage(context!!, message)
    }
}
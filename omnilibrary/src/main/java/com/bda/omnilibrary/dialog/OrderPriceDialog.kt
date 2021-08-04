package com.bda.omnilibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.checkout.OrderPriceAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import kotlinx.android.synthetic.main.dialog_detail.*
import kotlinx.android.synthetic.main.dialog_detail.vg_product
import kotlinx.android.synthetic.main.dialog_supplier_product.*

class OrderPriceDialog(
    private var detail: PriceDetail,
    private val list: ArrayList<Pair<String, ArrayList<Product>>>,
    private val gotoVoucherDialog: () -> Unit,
    private val onClickSubOrder: (data: Pair<String, ArrayList<Product>>) -> Unit
) :
    DialogFragment() {

    private lateinit var adapter: OrderPriceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_detail, container).apply {
            //url = arguments?.getString("URL_LANDING")
        }
    }

    fun updateDetail(detail: PriceDetail) {
        this.detail = detail
        adapter.updateDetail(this.detail)
        adapter.notifyItemChanged(0)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = OrderPriceAdapter(activity!! as BaseActivity, detail, list, {
            gotoVoucherDialog.invoke()
        }, {
            onClickSubOrder.invoke(it)
        }) {
            if (it) {
                choose_voucher?.text = getText(R.string.text_to_choose_voucher)
            } else {
                choose_voucher?.text = getText(R.string.text_to_go_to_cart)
            }
        }
        vg_product.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(true)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.CENTER
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val lp = window.attributes
            lp.dimAmount = 0.6f
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND

            dialog.window?.attributes = lp
            /*dialog.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        dismiss()
                    }
                }
                false
            }*/
        }
    }
}

data class PriceDetail(
    val price: Double,
    var voucher: Double,
    var voucherCode: String,
    val shipping: String,
    val total: Double,
)
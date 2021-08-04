package com.bda.omnilibrary.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.checkout.SupplierAdapter
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import kotlinx.android.synthetic.main.dialog_supplier_product.*

class ProductBySupplierDialog(
    private val list: ArrayList<Pair<String, ArrayList<Product>>>,
    val isHighlightDeliveryDay: Boolean,
    val onClick: (data: Pair<String, ArrayList<Product>>) -> Unit
) :
    DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_supplier_product, container).apply {
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vg_product.adapter =
            SupplierAdapter(activity!! as BaseActivity, list, isHighlightDeliveryDay) { pair ->
                onClick.invoke(pair)
            }

        if (isHighlightDeliveryDay) {
            title.text = getString(R.string.text_chi_tiet_ngay_ngay_du_kien_don_hang)
        }

        if (list.size == 1)
            note_choose_list.visibility = View.GONE
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
            lp.dimAmount = 0.0f
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND

            dialog.window?.attributes = lp
            /*dialog.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

                    }

                }
                false
            }*/
        }
    }
}

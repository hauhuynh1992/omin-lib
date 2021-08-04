package com.bda.omnilibrary.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.CompactVoucherV2Adapter
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.orderDetail.OrderActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_voucher.*

class VoucherDialog(
    private val list: ArrayList<Voucher>,
    private val onSelectedVoucher: (code: String) -> Unit
) :
    DialogFragment() {
    private var isChooseVoucher = true
    private lateinit var adapter: CompactVoucherV2Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.dialog_voucher, container).apply {
            //url = arguments?.getString("URL_LANDING")
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initial()
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
            //dialog.window?.setWindowAnimations(R.style.DialogAnimation)

            dialog.window?.attributes = lp
            dialog.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if ((vg.size >= 2 && (vg[0].hasFocus() || vg[1].hasFocus()))
                            || (vg.size > 0 && vg[0].hasFocus())
                        ) {
                            Handler().postDelayed({
                                bn_select_voucher.requestFocus()
                            }, 0)
                        }
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (bn_select_voucher.hasFocus() || bn_input_voucher.hasFocus()) {
                            if (vg.visibility == View.VISIBLE && vg.size == 0) {
                                if (bn_select_voucher.hasFocus()) {
                                    Handler().postDelayed({
                                        bn_select_voucher.requestFocus()
                                    }, 0)
                                } else if (bn_input_voucher.hasFocus()) {
                                    Handler().postDelayed({
                                        bn_input_voucher.requestFocus()
                                    }, 0)
                                }
                            }
                        }

                    }
                }
                false
            }
        }
    }

    private fun initial() {
        loadVoucher()
        edt_code.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.setStrokeFocus(cv_edt_code, activity!!)

                cv_edt_code.cardElevation =
                    resources.getDimension(R.dimen._4sdp)
                cv_edt_code.alpha = 1f
                cv_edt_code.strokeWidth = 4
                cv_edt_code.strokeColor =
                    ContextCompat.getColor(activity!!, R.color.start_color)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cv_edt_code.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                }

            } else {
                cv_edt_code.alpha = 0.2f
                Functions.setLostFocus(cv_edt_code, activity!!)
            }
        }

        edt_code.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_INPUT.name
            dataObject.inputType = "KEYBOARD_VOUCHER_TYPE"
            dataObject.inputName = "Voucher"
            val data = Gson().toJson(dataObject).toString()

            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_code.text.toString(),
                edt_code
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }


        bn_select_voucher.setOnFocusChangeListener { _, hasFocus ->
            text_bn_select_voucher.setTextColor(
                ContextCompat.getColorStateList(
                    activity!!,
                    R.drawable.selector_button_header
                )
            )

            text_bn_select_voucher.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_select_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                    item_bn_select_voucher.cardElevation = resources.getDimension(R.dimen._3sdp)
                }


            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_select_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.text_black_70)
                    item_bn_select_voucher.cardElevation = 0f
                }

                if (isChooseVoucher)
                    text_bn_select_voucher.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.start_color
                        )
                    )
            }
        }

        bn_apply.setOnFocusChangeListener { _, hasFocus ->
            text_bn_apply.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_apply.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                    item_bn_apply.cardElevation = resources.getDimension(R.dimen._3sdp)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_apply.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.text_black_70)
                    item_bn_apply.cardElevation = resources.getDimension(R.dimen._1sdp)
                }
            }
        }

        bn_input_voucher.setOnFocusChangeListener { _, hasFocus ->
            text_bn_input_voucher.setTextColor(
                ContextCompat.getColorStateList(
                    activity!!,
                    R.drawable.selector_button_header
                )
            )

            text_bn_input_voucher.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_input_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.end_color)
                    item_bn_input_voucher.cardElevation = resources.getDimension(R.dimen._3sdp)
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_input_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(activity!!, R.color.text_black_70)
                    item_bn_input_voucher.cardElevation = 0f
                }

                if (!isChooseVoucher)
                    text_bn_input_voucher.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.start_color
                        )
                    )
            }
        }

        bn_select_voucher.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_LIST.name
            val data = Gson().toJson(dataObject).toString()
            chooseVoucher()
        }

        bn_input_voucher.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_INPUT.name
            val data = Gson().toJson(dataObject).toString()

            inputVoucher()
        }

        bn_apply.setOnClickListener {

            if (Functions.checkInternet(activity!!)) {
                if (edt_code.text.toString().trim().isNotEmpty()) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.VOUCHER_LIST.name
                    dataObject.voucherCode = edt_code.text.toString()
                    val data = Gson().toJson(dataObject).toString()

                    onSelectedVoucher.invoke(edt_code.text.toString().toUpperCase())
                } else {
                    Functions.showMessage(activity!!, getString(R.string.enter_voucher))
                }
            } else {
                Functions.showMessage(activity!!, getString(R.string.no_internet))
            }
        }
    }

    @SuppressLint("ResourceType")
    private fun chooseVoucher() {
        vg.visibility = View.VISIBLE
        rl_card_number.visibility = View.GONE
        isChooseVoucher = true
        text_bn_input_voucher.setTextColor(
            ContextCompat.getColorStateList(
                activity!!,
                R.drawable.selector_button_header
            )
        )

        Handler().postDelayed({
            if (vg.size > 0) {
                vg.requestFocus()
            }
        }, 0)
    }

    @SuppressLint("ResourceType")
    private fun inputVoucher() {
        vg.visibility = View.GONE
        rl_card_number.visibility = View.VISIBLE

        isChooseVoucher = false

        text_bn_select_voucher.setTextColor(
            ContextCompat.getColorStateList(
                activity!!,
                R.drawable.selector_button_header
            )
        )

        Handler().postDelayed({
            bn_input_voucher.requestFocus()
        }, 0)

        //bn_input_voucher.requestFocus()
    }

    private fun loadVoucher() {
        if (list.size > 0) {
            for (i in list.size - 1 downTo 0 step 1) {
                if (!Functions.isExpTimestamp(list[i].stop_at)) {
                    list.removeAt(i)
                }
            }

            //empty.visibility = View.GONE
            adapter = CompactVoucherV2Adapter(activity as BaseActivity, list)
            adapter.setOnItemClickListener(object : CompactVoucherV2Adapter.OnItemClickListener {

                override fun onItemClick(v: Voucher) {
                    if (Functions.checkInternet(activity!!)) {
                        onSelectedVoucher.invoke(v.voucher_code)
                    } else {
                        Functions.showMessage(
                            activity!!,
                            getString(R.string.no_internet)
                        )
                    }
                }

            })
            vg.setNumColumns(2)
            vg.adapter = adapter

            chooseVoucher()

        } else {

            inputVoucher()
        }
    }

}
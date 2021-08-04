package com.bda.omnilibrary.ui.voucherV2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.size
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.VoucherV2Adapter
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.BestVoucherForCartResponse
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.model.Voucher
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_voucher_v2.*

class VoucherV2Activity : BaseActivity(), VoucherV2Contract.View {

    private lateinit var adapter: VoucherV2Adapter
    private lateinit var presenter: VoucherV2Contract.Presenter
    private var listVouchers = ArrayList<Voucher>()

    private var isChooseVoucher = true

    private var products: ArrayList<Product>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher_v2)
        listVouchers = intent.getParcelableArrayListExtra("VOUCHERS")!!
        val json = intent.getStringExtra("PRODUCTS")

        if (json != null && json.isNotEmpty()) {
            products = Gson().fromJson(
                json,
                object : TypeToken<ArrayList<Product>>() {}.type
            )
        }

        initChildHeader(image_header)

        initial()
    }

    @SuppressLint("ResourceType", "DefaultLocale")
    private fun initial() {
        presenter = VoucherV2Presenter(this, this, listVouchers, products)

        edt_code.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.setStrokeFocus(cv_edt_code, this@VoucherV2Activity)

                cv_edt_code.cardElevation =
                    resources.getDimension(R.dimen._4sdp)
                cv_edt_code.alpha = 1f
                cv_edt_code.strokeWidth = 4
                cv_edt_code.strokeColor =
                    ContextCompat.getColor(this, R.color.start_color)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    cv_edt_code.outlineSpotShadowColor =
                        ContextCompat.getColor(this, R.color.end_color)
                }

            } else {
                cv_edt_code.alpha = 0.2f
                Functions.setLostFocus(cv_edt_code, this@VoucherV2Activity)
            }
        }

        edt_code.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_INPUT.name
            dataObject.inputType = "KEYBOARD_VOUCHER_TYPE"
            dataObject.inputName = "Voucher"
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_code.text.toString(),
                edt_code
            )
            keyboardDialog.show(supportFragmentManager, keyboardDialog.tag)
        }


        bn_select_voucher.setOnFocusChangeListener { _, hasFocus ->
            text_bn_select_voucher.setTextColor(
                ContextCompat.getColorStateList(
                    this,
                    R.drawable.selector_button_header
                )
            )

            text_bn_select_voucher.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_select_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.end_color)
                    item_bn_select_voucher.cardElevation = resources.getDimension(R.dimen._3sdp)
                }


            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_select_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.text_black_70)
                    item_bn_select_voucher.cardElevation = 0f
                }

                if (isChooseVoucher)
                    text_bn_select_voucher.setTextColor(
                        ContextCompat.getColor(
                            this,
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
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.end_color)
                    item_bn_apply.cardElevation = resources.getDimension(R.dimen._3sdp)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_apply.outlineSpotShadowColor =
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.text_black_70)
                    item_bn_apply.cardElevation = resources.getDimension(R.dimen._1sdp)
                }
            }
        }

        bn_input_voucher.setOnFocusChangeListener { _, hasFocus ->
            text_bn_input_voucher.setTextColor(
                ContextCompat.getColorStateList(
                    this,
                    R.drawable.selector_button_header
                )
            )

            text_bn_input_voucher.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_input_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.end_color)
                    item_bn_input_voucher.cardElevation = resources.getDimension(R.dimen._3sdp)
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_input_voucher.outlineSpotShadowColor =
                        ContextCompat.getColor(this@VoucherV2Activity, R.color.text_black_70)
                    item_bn_input_voucher.cardElevation = 0f
                }

                if (!isChooseVoucher)
                    text_bn_input_voucher.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.start_color
                        )
                    )
            }
        }

        bn_select_voucher.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_LIST.name
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_LIST_VOUCHER_MENU_v2,
                data
            )
            chooseVoucher()
        }

        bn_input_voucher.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.VOUCHER_INPUT.name
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_VOUCHER_MENU_v2,
                data
            )
            inputVoucher()
        }

        bn_apply.setOnClickListener {

            if (Functions.checkInternet(this)) {
                if (edt_code.text.toString().trim().isNotEmpty()) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.VOUCHER_LIST.name
                    dataObject.voucherCode = edt_code.text.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_APPLY_VOUCHER_BUTTON_v2,
                        data
                    )
                    presenter.loadVaucher(edt_code.text.toString().toUpperCase())
                } else {
                    Functions.showMessage(this, getString(R.string.enter_voucher))
                }
            } else {
                Functions.showMessage(this, getString(R.string.no_internet))
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
                this,
                R.drawable.selector_button_header
            )
        )

        Handler().postDelayed({
            vg.requestFocus()
        }, 0)
    }

    @SuppressLint("ResourceType")
    private fun inputVoucher() {
        vg.visibility = View.GONE
        rl_card_number.visibility = View.VISIBLE

        isChooseVoucher = false

        text_bn_select_voucher.setTextColor(
            ContextCompat.getColorStateList(
                this,
                R.drawable.selector_button_header
            )
        )

        Handler().postDelayed({
            bn_input_voucher.requestFocus()
        }, 0)

        //bn_input_voucher.requestFocus()
    }

    override fun sendVaucherSuccess(response: BestVoucherForCartResponse) {
        val intent = Intent()
        intent.putExtra("VALUE", response.data!!.applied_value)
        intent.putExtra("VOUCHER_UID", response.data.uid)
        intent.putExtra("VOUCHER_CODE", response.data.voucher_code)
        setResult(100, intent)
        finish()
    }

    override fun sendVaucherFalsed(message: String) {
        Functions.showMessage(this, message)

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {

            KeyEvent.KEYCODE_DPAD_UP -> {
                if ((vg.size >= 2 && (vg[0].hasFocus() || vg[1].hasFocus()))
                    || (vg.size > 0 && vg[0].hasFocus())
                ) {
                    Handler().postDelayed({
                        bn_select_voucher.requestFocus()
                    }, 0)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun sendVouchersFromDatabase(list: ArrayList<Voucher>) {
        if (list.size > 0) {
            //empty.visibility = View.GONE
            adapter = VoucherV2Adapter(this, list)
            adapter.setOnItemClickListener(object : VoucherV2Adapter.OnItemClickListener {

                override fun onItemClick(v: Voucher) {
                    if (Functions.checkInternet(this@VoucherV2Activity)) {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.VOUCHER_LIST.name
                        dataObject.voucherCode = v.voucher_code
                        dataObject.voucherId = v.uid
                        dataObject.voucherValue = v.voucher_value.toString()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.CLICK_VOUCHER_v2,
                            data
                        )
                        presenter.loadVaucher(v.voucher_code)

                    } else {
                        Functions.showMessage(
                            this@VoucherV2Activity,
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

    override fun sendTotalMoney(total: String) {
        //tv_total.text = total
    }

    override fun newAddress() {
        gotoAltAddress(2)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }
}

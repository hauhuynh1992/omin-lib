package com.bda.omnilibrary.ui.PaymentMethod

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.core.view.get
import androidx.core.view.size
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.checkout.CheckoutStep2Adapter
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_step2.*
import kotlinx.android.synthetic.main.item_activity_header.view.*
import java.util.*

class Step2Activity : BaseActivity(), Step2Contract.View {

    private lateinit var adapter: CheckoutStep2Adapter

    private lateinit var presenter: Step2Contract.Presenter

    private lateinit var mHelper: PreferencesHelper
    private var isDisableCOD = false
    private var mPrice = 0.0
    private var voucherid = ""
    private var vaucherCode = ""
    private var vaucherValue = ""
    private var deliveryDay = ""
    private var typeRequest = 2

    private var isCod = false
    private var paymentMethod = ""

    private var reorderList: ArrayList<Product>? = null
    private var isNewUser = false

    companion object {
        var step2Activity: Step2Activity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step2)

        step2Activity = this

        initChildHeader(image_header)

        val json = intent.getStringExtra("list")

        if (json != null) {
            reorderList = Gson().fromJson(
                json,
                object : TypeToken<ArrayList<Product>>() {}.type
            )
        }

        presenter = Step2Presenter(this, this)

        mPrice = intent.getDoubleExtra("PRICE", 0.0)
        mHelper = PreferencesHelper(this)
        isDisableCOD = intent.getBooleanExtra("DISABLECOD", false)
        voucherid = intent.getStringExtra("VOUCHER_UID").toString()
        vaucherCode = intent.getStringExtra("VOUCHER_CODE").toString()
        vaucherValue = intent.getStringExtra("VOUCHER_VALUE").toString()
        deliveryDay = intent.getStringExtra("DELIVERY_DAY").toString()
        isNewUser = intent.getBooleanExtra("isNewUser", false)

        price.text = Functions.formatMoney(mPrice)
        date.text = deliveryDay

        val data: ArrayList<String> = if (isDisableCOD) {
            arrayListOf("momo", "vnpay", "moca")
        } else {
            if (LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString()){
                arrayListOf("momo", "vnpay", "moca", "cod")
            } else {
                arrayListOf("cod")
            }
        }

        adapter = CheckoutStep2Adapter(this, data) {
            val dataObject = LogDataRequest()
            dataObject.voucherCode = vaucherCode
            dataObject.voucherId = voucherid
            dataObject.voucherValue = vaucherValue
            dataObject.cartValue = mPrice.toString()
            paymentMethod = it
            dataObject.paymentMethodName = it
            val dataTracking = Gson().toJson(dataObject).toString()
            when (it) {
                "momo", "vnpay", "moca" -> {
                    if (reorderList != null) {
                        gotoEwallet(
                            vaucherCode,
                            voucherid,
                            vaucherValue,
                            it,
                            price.text.toString(),
                            deliveryDay,
                            typeRequest,
                            reorderList
                        )

                    } else {
                        gotoEwallet(
                            vaucherCode,
                            voucherid,
                            vaucherValue,
                            it,
                            price.text.toString(),
                            deliveryDay,
                            typeRequest
                        )

                    }
                }

                "cod" -> {
                    if (Functions.checkInternet(this)) {
                        /*logTracking(
                            "PaymentMethodActivity",
                            QuickstartPreferences.CLICK_COD,
                            QuickstartPreferences.COD
                        )*/
                        progressBar6.visibility = View.VISIBLE
                        presenter.loadPresenter(vaucherCode, voucherid, typeRequest, reorderList)

                        isCod = true

                    } else {
                        Functions.showMessage(this, getString(R.string.no_internet))
                    }

                }
            }
            logTrackingVersion2(
                QuickstartPreferences.CLICK_PAYMENT_METHOD_v2,
                dataTracking
            )
        }

        vg_product.adapter = adapter

        if (typeRequest == 1) {
            rab_workday.isChecked = true
            rab_allDay.isChecked = false
        } else {
            rab_workday.isChecked = false
            rab_allDay.isChecked = true
        }

        rl_workday.setOnClickListener {
            typeRequest = 1
            rab_allDay.isChecked = false
            rab_workday.isChecked = true
        }

        rl_workday.setOnFocusChangeListener { _, hasFocus ->
            rab_workday.isSelected = hasFocus
        }

        rl_allDay.setOnClickListener {
            typeRequest = 2
            rab_allDay.isChecked = true
            rab_workday.isChecked = false
        }

        rl_allDay.setOnFocusChangeListener { _, hasFocus ->
            rab_allDay.isSelected = hasFocus
        }

        // todo
        /*if (isNewUser)
            step.setBackgroundResource(R.mipmap.image_new_user_step_4)*/

        Handler().postDelayed({
            vg_product.requestFocus()

        }, 100)
    }

    override fun sendSuccess(Oid: String) {
        progressBar6.visibility = View.GONE
        //gotoSuccess()
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.CHECKOUT_RESULT.name
        dataObject.orderId = Oid
        dataObject.voucherCode = vaucherCode
        dataObject.voucherId = voucherid
        dataObject.voucherValue = vaucherValue
        dataObject.cartValue = mPrice.toString()
        dataObject.paymentMethodName = paymentMethod
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_COD_PAGE_v2,
            data
        )
        if (isCod) {
            if (reorderList != null) {
                gotoCheckoutStep3Activity(Oid, 4, isClearCart = false)

            } else {
                if (isNewUser) {
                    gotoCheckoutStep3Activity(Oid, 4, isClearCart = true, isShowStep = false)
                } else {
                    gotoCheckoutStep3Activity(Oid, 4, true)
                }

            }
        } else {
            gotoCheckoutStep3Activity(Oid, 1, true)
        }
    }

    override fun sendFalsed(message: String) {
        progressBar6.visibility = View.GONE
        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.CHECKOUT_RESULT.name
        dataObject.reason = message
        dataObject.voucherId = voucherid
        dataObject.voucherValue = vaucherValue
        dataObject.voucherCode = vaucherCode
        dataObject.cartValue = mPrice.toString()
        dataObject.paymentMethodName = paymentMethod
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.PAYMENT_FAILED_v2,
            data
        )
        Functions.showMessage(this, message)
    }

    override fun onResume() {
        super.onResume()
        val dataObject = LogDataRequest()
        dataObject.voucherCode = vaucherCode
        dataObject.voucherId = voucherid
        dataObject.voucherValue = vaucherValue
        dataObject.cartValue = mPrice.toString()
        val dataTracking = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_PAYMENT_METHOD_PAGE_v2,
            dataTracking
        )
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAPI()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (vg_product.size > 0 && vg_product[0].hasFocus()) {
                    image_header.bn_search.requestFocus()
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }
}

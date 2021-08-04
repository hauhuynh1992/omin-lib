package com.bda.omnilibrary.ui.ewallet

import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.ExitPaymentDialog
import com.bda.omnilibrary.dialog.PaymentErrorDialog
import com.bda.omnilibrary.dialog.RenewQrCodeDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ewallet.*
import kotlinx.android.synthetic.main.item_header_screen.view.*

class EwalletActivity : BaseActivity(), EwalletContract.View {

    private var type: String = "momo"

    private lateinit var qrImage: ImageView
    private lateinit var presenter: EwalletContract.Presenter

    private lateinit var tvMinute: SfTextView
    private lateinit var mHelper: PreferencesHelper

    private var mPrice = ""
    private var deliveryDay = ""

    private var timer: CountDownTimer? = null
    private var vaucher_code = ""
    private var vaucher_uid = ""
    private var vaucher_value = ""
    private var requestType = 2
    private var isNewUser = false

    private var list: ArrayList<Product>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ewallet)

        mHelper = PreferencesHelper(this)

        initChildHeader()

        vaucher_code = intent.getStringExtra("VOUCHER_CODE").toString()
        vaucher_value = intent.getStringExtra("VOUCHER_VALUE").toString()
        vaucher_uid = intent.getStringExtra("VOUCHER_UID").toString()
        type = intent.getStringExtra("METHOD").toString()

        mPrice = intent.getStringExtra("PRICE").toString()
        deliveryDay = intent.getStringExtra("DELIVERY_DAY").toString()

        requestType = intent.getIntExtra("REQUEST_TYPE", 2)

        val json = intent.getStringExtra("list")

        isNewUser = intent.getBooleanExtra("isNewUser", false)

        if (json != null) {
            list = Gson().fromJson(
                json,
                object : TypeToken<ArrayList<Product>>() {}.type
            )
        }

        // binding view
        qrImage = findViewById(R.id.qr)
        tvMinute = findViewById(R.id.time)

        // init presenter

        presenter = EwalletPresenter(this, this, type, requestType)
        presenter.loadPresenter(vaucher_code, vaucher_uid, list)


        price.text = mPrice
        date.text = deliveryDay

        initial()
    }

    override fun onDestroy() {
        if (timer != null) {
            timer?.cancel()
        }
        presenter.disposeAPI()

        if (mRunnable != null) mHandler.removeCallbacks(mRunnable!!)
        super.onDestroy()
    }

    private fun initial() {
        var data = ""
        val dataObject = LogDataRequest()
        dataObject.voucherValue = vaucher_value
        dataObject.voucherId = vaucher_uid
        dataObject.voucherCode = vaucher_code
        dataObject.cartValue = mPrice
        data = Gson().toJson(dataObject).toString()

        when (type) {
            "momo" -> {
                con_qr.setBackgroundResource(R.mipmap.image_momo_v2)
                name.text = getString(R.string.text_thanh_toan_momo)
                logTrackingVersion2(
                    QuickstartPreferences.LOAD_MOMO_PAGE_v2,
                    data
                )
            }

            "moca" -> {
                name.text = getString(R.string.text_thanh_toan_moca)
                con_qr.setBackgroundResource(R.mipmap.image_moca_v2)
                logTrackingVersion2(
                    QuickstartPreferences.LOAD_MOCA_PAGE_v2,
                    data
                )

            }

            "vnpay" -> {
                con_qr.setBackgroundResource(R.mipmap.image_vnpay_2)
                name.text = getString(R.string.text_thanh_toan_vnpay)
                logTrackingVersion2(
                    QuickstartPreferences.LOAD_VNPAY_PAGE_v2,
                    data
                )

            }
        }


    }

    override fun sendQrCodeBase64(encode: String) {
        timer = startTimer(tvMinute, {
            // timeout
            if (remainQrDialog != null) {
                remainQrDialog!!.dismiss()
                remainQrDialog = null
            }

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CHECKOUT_RESULT.name
            dataObject.voucherId = vaucher_uid
            dataObject.voucherValue = vaucher_value
            dataObject.voucherCode = vaucher_code
            dataObject.cartValue = mPrice
            dataObject.paymentMethodName = type
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.PAYMENT_FAILED_v2,
                data
            )

            RenewQrCodeDialog(this, {
                // click yes
                val dataObject1 = LogDataRequest()
                dataObject1.itemListPriceVat = mPrice
                when (type) {
                    "momo" -> {
                        dataObject1.screen = Config.SCREEN_ID.MOMO_CHECKOUT.name

                    }

                    "moca" -> {
                        dataObject1.screen = Config.SCREEN_ID.MOCA_CHECKOUT.name
                    }

                    "vnpay" -> {
                        dataObject1.screen = Config.SCREEN_ID.VNPAY_CHECKOUT.name

                    }
                }
                dataObject1.popUpName = "Pop-up renew QR code"
                val dataLog = Gson().toJson(dataObject1).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                    dataLog
                )

                presenter.loadPresenter(vaucher_code, vaucher_uid, list)
            }) {
                // click no
                val dataObject1 = LogDataRequest()
                when (type) {
                    "momo" -> {
                        dataObject1.screen = Config.SCREEN_ID.MOMO_CHECKOUT.name

                    }
                    "moca" -> {
                        dataObject1.screen = Config.SCREEN_ID.MOCA_CHECKOUT.name
                    }

                    "vnpay" -> {
                        dataObject1.screen = Config.SCREEN_ID.VNPAY_CHECKOUT.name

                    }
                }
                dataObject1.popUpName = "Pop-up renew QR code"
                val dataLog = Gson().toJson(dataObject1).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                    dataLog
                )
                finishActivity()
            }

        }, {
            if (remainQrDialog != null) {
                remainQrDialog!!.updateTime(it)
            }
        })

        createRemainArDialog(type)

        ImageUtils.loadQrCodeFromBase64(encode = encode, view = qrImage)
    }

    override fun sendSuccess(oid: String) {
        if (timer != null) {
            timer?.cancel()
        }

        if (list != null) {
            gotoCheckoutStep3Activity(oid, 1, false)

        } else {
            if (isNewUser) {
                gotoCheckoutStep3Activity(oid, 1, isClearCart = true, isShowStep = false)
            } else {
                gotoCheckoutStep3Activity(oid, 1, true)
            }
        }

        finishActivity()
    }

    override fun sendFalsed(message: String) {
        PaymentErrorDialog(this, message = message) {
            finishActivity()
        }
    }

    override fun finishActivity() {
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable!!)
        }

        finish()
    }

    override fun onBackPressed() {
        val dataObject = LogDataRequest()
        dataObject.voucherValue = vaucher_value
        dataObject.voucherCode = vaucher_code
        dataObject.voucherId = vaucher_uid
        dataObject.cartValue = mPrice
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.PAYMENT_CANCEL_v2,
            data
        )
        ExitPaymentDialog(this, {
            if (Functions.checkInternet(this)) {
                val dataObject1 = LogDataRequest()
                dataObject1.screen = Config.SCREEN_ID.ASK_FOR_EXIST_PAYMENT_POP_UP.name
                dataObject1.popUpName = getString(R.string.text_pop_up_thoat_thanh_toan)//"Pop-up thoát thanh toán"
                val dataLog = Gson().toJson(dataObject1).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                    dataLog
                )
                finishActivity()
            } else {
                Functions.showMessage(this, getString(R.string.no_internet))
            }
        }, {
            val dataObject1 = LogDataRequest()
            dataObject1.screen = Config.SCREEN_ID.ASK_FOR_EXIST_PAYMENT_POP_UP.name
            dataObject1.popUpName = getString(R.string.text_pop_up_thoat_thanh_toan)//"Pop-up thoát thanh toán"
            val dataLog = Gson().toJson(dataObject1).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                dataLog
            )
        })
    }

    override fun onResume() {
        super.onResume()
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        createRemainArDialog(type)
        return super.onKeyDown(keyCode, event)
    }

    private fun initChildHeader() {
        val d = getDatabaseHandler()!!.getLProductList()

        image_header.txt_quantity.text = d.size.toString()
        image_header.txt_header_total.text = getTotalMoney(d)

        ///////////// Set on Click /////////////

        image_header.bn_cart.visibility = View.GONE

        image_header.bn_account.visibility = View.GONE

        image_header.bn_search.visibility = View.GONE

        image_header.bn_header_back.setOnClickListener {
            ExitPaymentDialog(this, {
                if (Functions.checkInternet(this)) {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.ASK_FOR_EXIST_PAYMENT_POP_UP.name
                    dataObject.popUpName = getString(R.string.text_pop_up_thoat_thanh_toan)//"Pop-up thoát thanh toán"
                    val dataLog = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                        dataLog
                    )
                    finishActivity()
                } else {
                    Functions.showMessage(this, getString(R.string.no_internet))
                }
            }, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.ASK_FOR_EXIST_PAYMENT_POP_UP.name
                dataObject.popUpName = getString(R.string.text_pop_up_thoat_thanh_toan)//"Pop-up thoát thanh toán"
                val dataLog = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                    dataLog
                )
            })
        }

        /////////////////////// setOnFocusChangeListener ////////////////////
        image_header.bn_account.setOnFocusChangeListener { _, hasFocus ->
            image_header.txt_phone.isSelected = hasFocus
            image_header.txt_account.isSelected = hasFocus
            if (hasFocus) {
                image_header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )
            } else {
                image_header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_gray_C4C4C4
                    )
                )
            }
        }

        image_header.bn_cart.setOnFocusChangeListener { _, hasFocus ->
            image_header.txt_header_total.isSelected = hasFocus
            image_header.txt_cart.isSelected = hasFocus
            image_header.txt_quantity.isSelected = hasFocus

            if (hasFocus) {
                image_header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_white)
            } else {
                image_header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_gray)
            }
        }

        image_header.bn_search.setOnFocusChangeListener { _, hasFocus ->
            image_header.txt_search.isSelected = hasFocus
            image_header.txt_search_product.isSelected = hasFocus

            if (hasFocus) {
                image_header.ic_bn_search.setImageResource(R.mipmap.ic_search_white)
            } else {
                image_header.ic_bn_search.setImageResource(R.mipmap.ic_search_gray)
            }
        }
    }
}

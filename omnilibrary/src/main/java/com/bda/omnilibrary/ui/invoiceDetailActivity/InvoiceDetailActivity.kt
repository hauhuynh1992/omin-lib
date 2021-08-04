package com.bda.omnilibrary.ui.invoiceDetailActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.checkout.ProductCheckoutBySupplierAdapter
import com.bda.omnilibrary.dialog.ConfirmDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_invoice.*
import kotlinx.android.synthetic.main.item_header_screen.view.*
import java.util.*

class InvoiceDetailActivity : BaseActivity(), InvoiceDetailContact.View {

    private lateinit var adapter: ProductCheckoutBySupplierAdapter
    private var items: ArrayList<ListOrderResponceV3.Data.Item>? = null

    private lateinit var mHelper: PreferencesHelper
    private lateinit var presenter: InvoiceDetailContact.Presenter

    private var mVoucherCode = ""
    private var mVoucherValue = 0.0

    private var order: ListOrderResponceV3.Data? = null

    private var actionTracking = QuickstartPreferences.CLICK_CHECKOUT_AGAIN_BUTTON_v2

    companion object {
        var activity: InvoiceDetailActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        val json = intent.getStringExtra("order")

        if (json != null) {
            order = Gson().fromJson(
                    json,
                    object : TypeToken<ListOrderResponceV3.Data>() {}.type
            )
        }

        presenter = InvoiceDetailPresenter(this, this)

        activity = this

        initChildHeader(image_header)

        initOrder()

    }

    @SuppressLint("SetTextI18n")
    private fun initOrder() {
        when (order!!.orderStatus) {
            0 -> {
                // initial
                text_bn_confirm.text = getString(R.string.text_xac_nhan)
                actionTracking = QuickstartPreferences.CLICK_CHECKOUT_AGAIN_BUTTON_v2
            }

            1 -> {
                // confirming
                text_bn_confirm.text = getString(R.string.text_mua_them)
                setMilestoneDelivery()

                actionTracking = QuickstartPreferences.CLICK_CHECKOUT_AGAIN_BUTTON_v2
            }

            2 -> {
                // confirmed
                text_bn_confirm.text = getString(R.string.text_mua_them)
                setMilestoneDelivery()
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }

            3 -> {
                // delivering
                text_bn_confirm.text = getString(R.string.text_mua_them)
                setMilestoneDelivery()
                milestone.setImageResource(R.mipmap.milestone_step_2)
                step_2.setTextColor(ContextCompat.getColor(this, R.color.color_41AE96))
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }

            4 -> {
                // delivered
                text_bn_confirm.text = getString(R.string.text_mua_lai_lan_nua)
                milestone.setImageResource(R.mipmap.milestone_step_3)
                setMilestoneDelivery()
                step_2.setTextColor(ContextCompat.getColor(this, R.color.color_41AE96))
                step_3.setTextColor(ContextCompat.getColor(this, R.color.color_41AE96))

                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }

            5 -> {
                // cancel_request
                text_bn_confirm.text = getString(R.string.text_mua_them)
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }

            6 -> {
                // cancel
                text_bn_confirm.text = getString(R.string.text_mua_lai)
                tv_status.visibility = View.VISIBLE
                step.visibility = View.GONE
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }

            7 -> {
                // returning_cart
                text_bn_confirm.text = getString(R.string.text_mua_them)
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2

            }

            8 -> {
                // returned_cart
                text_bn_confirm.text = getString(R.string.text_mua_them)
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2

            }

            9 -> {
                // refunded
                text_bn_confirm.text = getString(R.string.text_mua_them)
                actionTracking = QuickstartPreferences.CLICK_BUY_MORE_BUTTON_v2
            }
        }

        when (order!!.pay_gateway) {
            "momo" -> {
                text_pay_method.text = getString(R.string.thanh_to_n_vi_momo)

            }

            "vnpay" -> {
                text_pay_method.text = getString(R.string.text_thanh_toan_vi_vnpay)

            }

            "moca" -> {
                text_pay_method.text = getString(R.string.text_thanh_toan_vi_moca)

            }

            "Thanh toán khi nhận hàng" -> {
                text_pay_method.text = getString(R.string.text_thanh_toan_khi_nhan_hang)

            }

            "Đặt hàng nhanh" -> {
                // todo something
                text_pay_method.text = getString(R.string.text_dat_hang_nhanh)

            }
        }

        rl_pay_method.visibility = View.VISIBLE

        when (order!!.payStatus) {
            1 -> {
                // Success
                pay_method.text = getString(R.string.text_thanh_toan_thanh_cong)
                pay_method.paint.shader = Functions.gradientText(
                        pay_method,
                        ContextCompat.getColor(this, R.color.title_green_50BC98),
                        ContextCompat.getColor(this, R.color.title_green_2E9B93)

                )

            }

            2 -> {
                // Failed
                pay_method.text = getString(R.string.text_thanh_toan_that_bai)

                pay_method.setTextColor(
                        ContextCompat.getColorStateList(
                                this,
                                R.color.text_cancel
                        )
                )

                pay_method.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.background_cancel_focus
                )
                pay_method.paint.shader = Functions.gradientText(
                        pay_method,
                        ContextCompat.getColor(this, R.color.title_green_50BC98),
                        ContextCompat.getColor(this, R.color.title_green_2E9B93)
                )
            }

            3 -> {
                // Pending
                pay_method.text = getString(R.string.text_chua_thanh_toan)

                pay_method.setTextColor(
                        ContextCompat.getColorStateList(
                                this,
                                R.color.bg_delivering
                        )
                )

                pay_method.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.background_delivering_focus
                )
                pay_method.paint.shader = Functions.gradientText(
                        pay_method,
                        ContextCompat.getColor(this, R.color.title_green_50BC98),
                        ContextCompat.getColor(this, R.color.title_green_2E9B93)

                )
            }
        }

        rl_products.setOnClickListener {
            gotoSupplierProductForInvoiceDialog(order!!)
        }

        rl_products.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                product_note.visibility = View.VISIBLE
            } else {
                product_note.visibility = View.GONE
            }
        }

        title_products.text = getString(R.string.text_toan_tat_don_hang)

        order_id.visibility = View.VISIBLE
        order_id.text = order!!.uid

        mHelper = PreferencesHelper(this)
        items = Functions.getItemFromSubOrder(order!!)

        mVoucherValue = order!!.voucher_value
        mVoucherCode = order!!.voucher_code

        if (mVoucherCode.isNotBlank()) {
            ln_voucher.visibility = View.VISIBLE
            voucherCode.text =
                    "#$mVoucherCode"
            voucherCode.paint.shader = Functions.gradientText(
                    voucherCode,
                    ContextCompat.getColor(this, R.color.title_orange_FF9736),
                    ContextCompat.getColor(this, R.color.title_orange_DA5205)
            )
        }

        if (mVoucherValue > 0) {
            voucher.text = "-${Functions.formatMoney(mVoucherValue)}"
        }

        loadData()

        if (order!!.orderStatus == 6) {
            ln_button.visibility = View.GONE
            ln_announce.visibility = View.VISIBLE
            bn_confirm_.setOnClickListener {
                val dataObject1 = LogDataRequest()
                dataObject1.orderId = order?.uid
                dataObject1.orderStatus = Functions.convertOrderStatus(order?.orderStatus ?: -1)
                dataObject1.orderPayStatus = Functions.convertOrderPayStatus(order?.payStatus ?: -1)
                dataObject1.orderValue = order?.totalPay.toString()
                //dataObject1.totalItem = order?.items?.size.toString()
                dataObject1.voucherCode = order?.voucher_code
                dataObject1.voucherValue = order?.voucher_value.toString()
                val data = Gson().toJson(dataObject1).toString()
                logTrackingVersion2(
                        QuickstartPreferences.CLICK_BUY_AGAIN_BUTTON_v2,
                        data
                )
                presenter.getListProductFromOrder(items!!)

            }

            bn_confirm_.setOnFocusChangeListener { _, hasFocus ->
                text_bn_confirm_.isSelected = hasFocus
            }

            bn_confirm_.nextFocusLeftId = bn_confirm_.id
            bn_confirm_.nextFocusUpId = bn_confirm_.id
            reason_detail.text = order!!.reason
            if (order!!.payStatus == 1) {
                ln_day_pay.visibility = View.VISIBLE

                day_pay.text = order!!.created_at

                when (order!!.pay_gateway) {
                    "momo" -> {
                        day_pay_detail.text = getString(
                                R.string.text_da_nhan_so_tien_qua_momo,
                                Functions.formatMoney(order!!.totalPay)
                        )
                        //"Đã nhận số tiền ${Functions.formatMoney(order!!.totalPay)} qua ví MoMo"

                    }

                    "moca" -> {
                        day_pay_detail.text = getString(
                                R.string.text_da_nhan_so_tien_qua_moca,
                                Functions.formatMoney(order!!.totalPay)
                        )
                        //"Đã nhận số tiền ${Functions.formatMoney(order!!.totalPay)} qua ví Moca"

                    }

                    "vnpay" -> {
                        day_pay_detail.text = getString(
                                R.string.text_da_nhan_so_tien_qua_vnpay,
                                Functions.formatMoney(order!!.totalPay)
                        )
                        //"Đã nhận số tiền ${Functions.formatMoney(order!!.totalPay)} qua ví VnPay"

                    }

                }


            } else {
                ln_day_pay.visibility = View.GONE

            }

            bn_confirm_.requestFocus()

        } else {
            bn_continue_shopping.visibility = View.GONE

            if (Functions.checkCancelableOrder(order!!)) {
                bn_voucher.visibility = View.VISIBLE
                text_bn_voucher.text = getString(R.string.text_yeu_cau_huy_don)//"Yêu cầu hủy đơn"

                bn_voucher.setOnClickListener {
                    val dataObject = LogDataRequest()
                    dataObject.orderId = order?.uid
                    dataObject.value = order?.totalPay.toString()
                    dataObject.orderStatus = order?.orderStatus.toString()
                    dataObject.orderPayStatus = order?.payStatus.toString()
                    //dataObject.totalItem = order?.items?.size.toString()
                    dataObject.voucherCode = order?.voucher_code
                    dataObject.voucherValue = order?.voucher_value.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                            QuickstartPreferences.CLICK_CANCEL_ORDER_BUTTON_v2,
                            data
                    )
                    ConfirmDialog(this,
                            getString(R.string.text_ban_co_muon_huy_don_hang_nay)/*"Bạn có muốn hủy đơn hàng này?"*/,
                            {
                                val dataObject1 = LogDataRequest()
                                dataObject1.screen = Config.SCREEN_ID.ORDER_DETAILS.name
                                dataObject1.popUpName =
                                        getString(R.string.text_pop_up_huy_don_hang)//"Pop-up hủy đơn hàng"
                                logTrackingVersion2(
                                        QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                                        Gson().toJson(dataObject1).toString()
                                )
                                presenter.cancelOrderByOrder(order!!.uid)
                            },
                            {
                                val dataObject1 = LogDataRequest()
                                dataObject1.screen = Config.SCREEN_ID.ORDER_DETAILS.name
                                dataObject1.popUpName =
                                        getString(R.string.text_pop_up_huy_don_hang)//"Pop-up hủy đơn hàng"
                                logTrackingVersion2(
                                        QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                                        Gson().toJson(dataObject1).toString()
                                )
                            })
                }

                bn_voucher.setOnFocusChangeListener { _, hasFocus ->
                    text_bn_voucher.isSelected = hasFocus
                }
                val params = LinearLayout.LayoutParams(
                        resources.getDimension(R.dimen._110sdp).toInt(),

                        resources.getDimension(R.dimen._45sdp).toInt()
                )
                params.setMargins(0, 0, resources.getDimension(R.dimen._minus15sdp).toInt(), 0)
                bn_voucher.layoutParams = params
                bn_confirm.nextFocusLeftId = bn_voucher.id

            } else {
                bn_voucher.visibility = View.GONE
                bn_confirm.nextFocusLeftId = bn_confirm.id

            }

            //bn_confirm.set
            val param = bn_confirm.layoutParams as LinearLayout.LayoutParams
            param.setMargins(0, 0, 0, 0)
            bn_confirm.layoutParams = param

            bn_confirm.setOnClickListener {
                /*gotoCheckoutStep2Activity(
                    order!!
                )*/

                if (Functions.checkInternet(this)) {
                    val dataObject = LogDataRequest()
                    dataObject.orderId = order?.uid
                    dataObject.orderStatus = Functions.convertOrderStatus(order?.orderStatus ?: -1)
                    dataObject.orderPayStatus =
                            Functions.convertOrderPayStatus(order?.payStatus ?: -1)
                    dataObject.orderValue = order?.totalPay.toString()
                    // todo
                    //dataObject.totalItem = order?.items?.size.toString()
                    dataObject.voucherCode = order?.voucher_code
                    dataObject.voucherValue = order?.voucher_value.toString()
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                            actionTracking,
                            data
                    )
                    presenter.getListProductFromOrder(items!!)

                } else {
                    Functions.showMessage(this, getString(R.string.no_internet))
                }

            }

            bn_confirm.setOnFocusChangeListener { _, hasFocus ->
                text_bn_confirm.isSelected = hasFocus
            }

            bn_confirm.requestFocus()
        }
    }

    private fun setMilestoneDelivery() {
        step_1.text = getString(R.string.milestone_processing)
        step_2.text = getString(R.string.milestone_delivering)
        step_3.text = getString(R.string.milestone_delivered)
    }

    private fun setCreatedDay() {
        date.text = order!!.created_at
    }

    var total = 0.0
    private fun loadData() {

        adapter = ProductCheckoutBySupplierAdapter(this, null, order!!.sub_orders)

        vg_product.adapter = adapter

        if (items != null) {
            for (i in items!!) {
                total += i.sell_price * i.quantity
            }
        }

        price.text = Functions.formatMoney(total)

        loadMoney()

        if (order!!.orderStatus == 4) {// delivered
            ship.text = getString(R.string.text_da_thanh_toan)

        } else {
            if (order!!.totalPay >= 200000) {
                ship.text = getString(R.string.text_mien_phi)
            } else {
                ship.text = getString(R.string.text_xac_dinh_sau)
            }
        }

        reloadInfo()
    }

    private fun loadMoney() {
        if (order != null) {
            total_vat.text = Functions.formatMoney(order!!.totalPay)
        }
    }

    private fun reloadInfo() {
        setCreatedDay()

        user_name.text = order!!.name
        phone.text = order!!.phone
        var add = order!!.address

        @Suppress("SENSELESS_COMPARISON")
        if (order!!.province != null && order!!.province.name.isNotEmpty()) {
            add +=
                    ", " + order!!.district.name + ", " + order!!.province.name
        }

        address.text = add


    }

    override fun onResume() {
        super.onResume()

        MainActivity.getMaiActivity()?.pauseMusicBackground()

    }

    override fun sendListProductSuccessful(list: ArrayList<Product>) {

        for (i in list) {
            for (o in items!!) {
                if (i.uid == o.itemUid) {
                    i.order_quantity = o.quantity
                }
            }
        }

        gotoCartByOrder(list)
    }

    override fun sendListProductFalse() {
    }

    override fun cancelOrderSuccess() {

        val intent = Intent()
        intent.putExtra("ORDER_ID", order!!.uid)
        setResult(Activity.RESULT_OK, intent)

        finish()
    }

    override fun cancelOrderFailed() {
        Functions.showMessage(
                this,
                getString(R.string.text_huy_don_hang_that_bai)/*"Hủy đơn hàng thất bại!"*/
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (bn_confirm_.hasFocus()) {
                    Handler().postDelayed({
                        rl_products.requestFocus()
                    }, 0)
                }
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (image_header.bn_header_back.hasFocus() || image_header.bn_search.hasFocus()
                        || image_header.bn_account.hasFocus() || image_header.bn_cart.hasFocus()
                ) {
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAPI()
    }
}

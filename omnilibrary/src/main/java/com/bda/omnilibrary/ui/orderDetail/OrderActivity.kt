package com.bda.omnilibrary.ui.orderDetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.checkout.ProductCheckoutBySupplierAdapter
import com.bda.omnilibrary.dialog.*
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_step1.*
import kotlinx.android.synthetic.main.milestone_4_step.view.*
import org.json.JSONObject


class OrderActivity : BaseActivity(), OrderContract.View {

    var data: ArrayList<Product>? = null
    private lateinit var adapter: ProductCheckoutBySupplierAdapter
    private var listNotMatch = ArrayList<Product>()
    private lateinit var mHelper: PreferencesHelper
    lateinit var presenter: OrderContract.Presenter
    private var voucher_code = ""
    private var voucher_uid = ""
    private var voucher_value = 0.0
    private var tempPrice = 0.0
    private var total = 0.0

    var dayAsString: String = ""
    var listVouchers = ArrayList<Voucher>()
    private var mAddress: ContactInfo? = null
    private var isReorder = false
    private var isNewUser = false
    private lateinit var supplierList: ArrayList<Pair<String, ArrayList<Product>>>
    private var voucherDialog: VoucherDialog? = null
    private var priceDialog: OrderPriceDialog? = null

    companion object {
        var orderActivity: OrderActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step1)

        val jsonProduct = intent.getStringExtra("list")
        if (jsonProduct != null) {
            isReorder = true

            data = Gson().fromJson(
                jsonProduct,
                object : TypeToken<ArrayList<Product>>() {}.type
            )
        }

        isNewUser = intent.getBooleanExtra("isNewUser", false)

        if (intent.hasExtra("voucherCode"))
            voucher_code = intent.getStringExtra("voucherCode").toString()

        if (intent.hasExtra("voucherValue"))
            voucher_value = intent.getDoubleExtra("voucherValue", 0.0)


        presenter = OrderPresenter(this, this)

        orderActivity = this

        initChildHeader(image_header)

        init()
        if (Config.oncartdetail.trim().isNotEmpty()) {
            tv_innounce_detail.visibility = View.VISIBLE
            view_innounce_detail.visibility = View.VISIBLE
            try {
                val obj = JSONObject(Config.oncartdetail)
                tv_innounce_detail.text = obj.getString("value")
                tv_innounce_detail.setTextColor(Color.parseColor(obj.getString("color")))
            } catch (tx: Throwable) {
                view_innounce_detail.visibility = View.GONE
                tv_innounce_detail.text = ""
            }
        } else {
            view_innounce_detail.visibility = View.GONE
            tv_innounce_detail.visibility = View.GONE
        }
    }

    private fun loadData() {
        supplierList = Functions.getListSupplierProduct(data!!)

        adapter = ProductCheckoutBySupplierAdapter(this, supplierList, null)
        vg_product.adapter = adapter

        if (data != null) {
            for (i in data!!) {
                tempPrice += i.price * i.order_quantity
            }

        }

        total = tempPrice

        price.text = Functions.formatMoney(total)
        loadMoney()
        if (getCheckCustomerResponse() != null) {
            presenter.loadVoucher(VoucherRequest(getCheckCustomerResponse()!!.data.uid))
        }

        loadShippingTimeFromProduct()

        loadUserInfo()
    }

    private fun reloadInfo() {
        if (mAddress != null) {
            user_name.text = mAddress!!.customer_name
            phone.text = mAddress!!.phone_number
            var add = mAddress!!.address.address_des

            @Suppress("SENSELESS_COMPARISON")
            if (mAddress!!.address.customer_province != null && mAddress!!.address.customer_province.name.isNotEmpty()) {
                add +=
                    ", " + mAddress!!.address.customer_district.name + ", " + mAddress!!.address.customer_province.name
            }

            address.text = add
        } else {
            user_name.text = ""
            phone.text = ""
            address.text = ""
        }
    }

    private fun loadUserInfo() {
        reloadInfo()
    }

    private fun loadShippingTimeFromProduct() {
        dayAsString = Functions.shippingTime(data!!)

        if (dayAsString.isNotBlank()) {
            date.text = dayAsString
        } else {
            rl_date.visibility = View.GONE
        }
    }

    private fun reloadUserInfo() {
        if (getCheckCustomerResponse() != null) {
            presenter.fetchProfile(getCheckCustomerResponse()!!.data.uid)
        }
        bn_continue_shopping.setOnClickListener {
            val dataObject = LogDataRequest()
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CHANGE_RECIPIENT_BUTTON_v2,
                data
            )
            createNewAddress()
        }
        bn_confirm.requestFocus()
    }

    private fun init() {

        if (isNewUser) {
            milestone.visibility = View.GONE

            milestone_new_user.visibility = View.VISIBLE
            milestone_new_user.step.setImageResource(R.mipmap.milestone_4_step_3)
            val c = ContextCompat.getColor(this, R.color.color_41AE96)
            milestone_new_user.step_2.setTextColor(c)
            milestone_new_user.step_3.setTextColor(c)
        }

        if (data == null && !isReorder)
            data = getDatabaseHandler()!!.getLProductList()

        Functions.removeProductBySupplierCondition(data!!)
        mHelper = PreferencesHelper(this)

        loadData()

        rl_products.setOnClickListener {
            gotoSupplierProductDialog(supplierList) {
                finish()
            }
        }

        rl_products.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                product_note.visibility = View.VISIBLE

            } else {
                product_note.visibility = View.GONE

            }
        }

        ln_bil.setOnClickListener {
            priceDialog = OrderPriceDialog(
                PriceDetail(
                    tempPrice,
                    voucher_value,
                    voucher_code,
                    ship.text.toString(),
                    total
                ), supplierList,
                {
                    voucherDialog = VoucherDialog(listVouchers) { choseVoucherCode ->
                        presenter.checkVoucher(data!!, choseVoucherCode)
                    }
                    voucherDialog!!.show(supportFragmentManager, voucherDialog!!.tag)
                }, {
                    //todo
                    finish()
                })
            /*bundle.putString("URL_LANDING", url);
            dialog.arguments = bundle*/
            priceDialog!!.show(supportFragmentManager, priceDialog!!.tag)
        }

        ln_bil.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                detail_note.visibility = View.VISIBLE

            } else {
                detail_note.visibility = View.GONE

            }
        }

        ln_delivery.setOnClickListener {
            gotoSupplierProductDialog(supplierList, true) {
                finish()
            }
        }

        ln_delivery.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                delivery_note.visibility = View.VISIBLE

            } else {
                delivery_note.visibility = View.GONE

            }
        }

        bn_confirm.setOnClickListener {
            @Suppress("SENSELESS_COMPARISON")
            if (mAddress != null && mAddress!!.phone_number != null
                && mAddress!!.phone_number.isNotEmpty()
            ) {
                listNotMatch.clear()
                for (i in data!!) {
                    if (!Functions.isAvailableArea(
                            mAddress!!.address.customer_province.uid,
                            i.areas
                        )
                    ) {
                        listNotMatch.add(i)
                    }
                }

                if (listNotMatch.size > 0) {
                    RemoveProductNotMatchLocationDialog(
                        this,
                        listNotMatch,
                        mAddress!!.address.customer_province.name
                    ) {
                        val dataObject = LogDataRequest()
                        val data = Gson().toJson(dataObject).toString()
                        logTrackingVersion2(
                            QuickstartPreferences.CLICK_CHANGE_RECIPIENT_BUTTON_v2,
                            data
                        )
                        createNewAddress()
                    }
                } else {
                    val dataObject = LogDataRequest()
                    dataObject.totalItem = data?.size.toString()
                    var mTotalMoney = 0.0
                    for (i in 0 until data!!.size) {
                        mTotalMoney += (data!![i].price * data!![i].order_quantity)
                    }
                    dataObject.cartValue = mTotalMoney.toString()
                    dataObject.listCart = Functions.toListProduct(data!!)
                    dataObject.voucherCode = voucher_code
                    dataObject.voucherValue = voucher_value.toString()
                    dataObject.voucherId = voucher_uid
                    val dataTracking = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_CHECKOUT_CONFIRM_v2,
                        dataTracking
                    )

                    //todo change
                    if (isReorder) {
                        gotoCheckoutStep2Activity(
                            Functions.isDisableCod(data),
                            total - voucher_value,
                            dayAsString,
                            voucher_uid,
                            voucher_code,
                            voucher_value.toString(),
                            data
                        )

                    } else {
                        gotoCheckoutStep2Activity(
                            Functions.isDisableCod(data),
                            total - voucher_value,
                            dayAsString,
                            voucher_uid,
                            voucher_code,
                            voucher_value.toString(),
                            isNewUser = isNewUser
                        )
                    }
                }

            } else {
                //Functions.showMessage(this, getString(R.string.need_name))
                PaymentErrorDialog(this@OrderActivity, R.string.text_create_address) {
                    createNewAddress()
                }
            }


        }

        bn_continue_shopping.setOnClickListener {
            val dataObject = LogDataRequest()
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CHANGE_RECIPIENT_BUTTON_v2,
                data
            )
            createNewAddress()
        }

        bn_confirm.setOnFocusChangeListener { _, hasFocus ->
            text_bn_confirm.isSelected = hasFocus
        }

        bn_continue_shopping.setOnFocusChangeListener { _, hasFocus ->
            text_bn_continue_shopping.isSelected = hasFocus
        }
    }

    private fun loadMoney() {
        if (voucher_code.isNotBlank()) {
            ln_voucher.visibility = View.VISIBLE
            voucherCode.text =
                "#$voucher_code"
            voucherCode.paint.shader = Functions.gradientText(
                voucherCode,
                ContextCompat.getColor(this, R.color.title_orange_FF9736),
                ContextCompat.getColor(this, R.color.title_orange_DA5205)
            )
        }

        if (voucher_value > 0) {
            voucher.text = "-${Functions.formatMoney(voucher_value)}"
        }

        total_vat.text =
            Functions.formatMoney(total - voucher_value)

        if (total - voucher_value >= 200000) {
            ship.text = getString(R.string.text_mien_phi)
        } else {
            ship.text = getString(R.string.text_xac_dinh_sau)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 100) {
            voucher_value = data!!.getDoubleExtra("VALUE", 0.0)
            voucher_code = data.getStringExtra("VOUCHER_CODE").toString()
            voucher_uid = data.getStringExtra("VOUCHER_UID").toString()
        }
        loadMoney()
    }

    private fun createNewAddress() {
        gotoAltAddress(2)
    }

    override fun onResume() {
        super.onResume()

        reloadUserInfo()

        MainActivity.getMaiActivity()?.pauseMusicBackground()

    }

    override fun sendListVoucherSuccess(vouchers: VoucherResponse) {
        listVouchers = vouchers.data
        //todo change
        /*
        @Suppress("SENSELESS_COMPARISON")
        if (vouchers.data != null && vouchers.data.size > 0) {

            AskVoucherDialog(this, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.ORDER_DETAILS.name
                dataObject.popUpName =
                    getString(R.string.text_pop_up_hoi_chon_voucher)//"Pop-up hỏi chọn voucher"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                    data
                )

                loadVoucher()
            }, {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.ORDER_DETAILS.name
                dataObject.popUpName =
                    getString(R.string.text_pop_up_hoi_chon_voucher)//"Pop-up hỏi sử dụng voucher"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_NO_BUTTON_POP_UP_v2,
                    data
                )
            })
        }*/
    }

    override fun sendProfileSuccess(contact: ContactInfo?) {
        mAddress = contact

        data?.let { list ->
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CHECKOUT_DETAILS.name
            dataObject.voucherValue = voucher_value.toString()
            dataObject.voucherCode = voucher_code
            dataObject.voucherId = voucher_uid
            dataObject.totalItem = list.size.toString()
            var mTotalMoney = 0.0
            for (i in 0 until list.size) {
                mTotalMoney += (list[i].price * list[i].order_quantity)
            }
            dataObject.cartValue = mTotalMoney.toString()
            dataObject.listCart = Functions.toListProduct(list)
            dataObject.recipientName = mAddress?.customer_name
            dataObject.recipientPhone = mAddress?.phone_number
            dataObject.recipientAddress = mAddress?.address?.address_des
            dataObject.recipientDistrict = mAddress?.address?.customer_district?.name
            dataObject.recipientCity = mAddress?.address?.customer_province?.name
            if (mAddress?.address?.address_type == 1) {
                dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
            } else {
                dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
            }
            val dataTracking = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.LOAD_CHECKOUT_DETAILS_v2,
                dataTracking
            )
        }

        reloadInfo()
    }

    override fun sendProfileFalsed(message: String) {
        Functions.showMessage(this, message)

    }

    override fun sendVoucherSuccess(response: BestVoucherForCartResponse) {
        voucherDialog?.dismiss()

        voucher_value = response.data!!.applied_value
        voucher_code = response.data.voucher_code
        voucher_uid = response.data.uid

        priceDialog!!.updateDetail(
            PriceDetail(
                tempPrice,
                voucher_value,
                voucher_code,
                ship.text.toString(),
                total
            )
        )

        loadMoney()
    }

    override fun sendVoucherFalsed(message: String) {
        Functions.showMessage(this, message)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                /*if (bn_voucher.hasFocus()) {
                    image_header.bn_header_back.requestFocus()
                } else if (bn_confirm.hasFocus()
                    || bn_continue_shopping.hasFocus()
                )
                    image_header.bn_search.requestFocus()
                 */
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                /*if (image_header.bn_header_back.hasFocus() || image_header.bn_search.hasFocus()
                    || image_header.bn_account.hasFocus() || image_header.bn_cart.hasFocus()
                ) {
                    bn_confirm.requestFocus()
                }*/
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAPI()
    }
}

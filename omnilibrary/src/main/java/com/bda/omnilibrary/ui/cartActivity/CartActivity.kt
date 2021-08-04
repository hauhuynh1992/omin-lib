package com.bda.omnilibrary.ui.cartActivity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.cart.CartV1Adapter
import com.bda.omnilibrary.adapter.cart.EmptyCartAdapter
import com.bda.omnilibrary.dialog.AskVoucherDialog
import com.bda.omnilibrary.dialog.VoucherDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.ui.orderDetail.OrderActivity
import com.bda.omnilibrary.ui.productDetailActivity.ProductDetailActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.util.Functions.sortListBySupplier
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.item_activity_header.view.*
import org.json.JSONObject


class CartActivity : BaseActivity(), CartContract.View {

    companion object {
        var cartActivity: CartActivity? = null
    }

    private lateinit var cartPresenter: CartContract.Presenter
    private lateinit var cartAdapter: CartV1Adapter
    private lateinit var emptyAdapter: EmptyCartAdapter

    private var list: ArrayList<Product>? = null
    private lateinit var preferencesHelper: PreferencesHelper
    private var holder: RecyclerView.ViewHolder? = null
    private var cartPosition = 0
    private var totalMoney: Double = 0.0
    private var voucher_code = ""
    private var voucher_uid = ""
    private var voucher_value = 0.0
    private var tempPrice = 0.0
    private var dialog: VoucherDialog? = null

    private var isReorder = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        val json = intent.getStringExtra("renew_list")

        if (json != null) {
            isReorder = true
            list = Gson().fromJson(
                json,
                object : TypeToken<ArrayList<Product>>() {}.type
            )
        }

        cartActivity = this
        preferencesHelper = PreferencesHelper(this)
        cartPresenter = CartPresenter(this, this)
        if (Config.oncart.trim().isNotEmpty()) {
            tv_innounce.visibility = View.VISIBLE
            view_innounce.visibility = View.VISIBLE
            try {
                val obj = JSONObject(Config.oncart)
                tv_innounce.text = obj.getString("value")
                tv_innounce.setTextColor(Color.parseColor(obj.getString("color")))
            } catch (tx: Throwable) {
                view_innounce.visibility = View.GONE
                tv_innounce.text = ""
            }
        } else {
            view_innounce.visibility = View.GONE
            tv_innounce.visibility = View.GONE
        }

        initChildHeader(image_top, Tab.CART)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            initial()
            MainActivity.getMaiActivity()?.pauseMusicBackground()
        }, 100)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        when (keyCode) {
            KeyEvent.KEYCODE_MENU -> {

            }
            KeyEvent.KEYCODE_DPAD_CENTER -> {

            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    if (holder != null) {
                        if (cartPosition == list!!.size - 1 && !checkTopInLeftFocus() && vg_empty.visibility == View.GONE) {
                            bn_continue_shopping.requestFocus()
                        }
                    }
                }
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                if ((cartPosition == 0 && !checkTopFocus() && ln_product_count.visibility == View.VISIBLE)
                    || (this::emptyAdapter.isInitialized &&
                            emptyAdapter.getContinuousButton().hasFocus())
                ) {
                    image_top.bn_search.requestFocus()
                }
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (event!!.action == KeyEvent.ACTION_DOWN) {
                    if (holder != null) {
                        if ((holder as CartV1Adapter.ItemViewHolder).bn_detail.hasFocus()) {
                            bn_continue_shopping.requestFocus()
                        }
                    }
                }
            }
            else -> return super.onKeyDown(keyCode, event)
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun initial() {
        if (!isReorder) {
            list = getDatabaseHandler()!!.getLProductList()
        }

        list = sortListBySupplier(list!!)
        if (list!!.size > 0) {
            showCart(true)
            tv_addcart_quantity.text =
                getString(R.string.text_number_item_cart, list!!.size.toString())

            if (!isReorder) {
                initCart()
            } else {
                initRenewOrder()
            }
        } else {
            showCart(false)
            emptyAdapter = EmptyCartAdapter(this)
            emptyAdapter.setOnCallbackListener(object : EmptyCartAdapter.OnCallBackListener {
                override fun onItemHighlightClick(product: Product) {
                    if (ProductDetailActivity.productDetailActivity != null) {
                        ProductDetailActivity.productDetailActivity!!.finish()
                    }

                    gotoProductDetail(product, 0, "CartActivity", isPopup = false)
                    finish()

                }

                override fun onContinuteClick() {
                    comeHome()

                }
            })

            vg_empty.adapter = emptyAdapter

            cartPresenter.callHightlight()
        }
    }

    private fun initRenewOrder() {
        loadData()
    }

    private fun initCart() {
        if (Functions.checkInternet(this)) {
            loadData()
            if (getCheckCustomerResponse() != null) {
                cartPresenter.updateCartOnline(
                    CartRequest(
                        getCheckCustomerResponse()!!.data.uid,
                        list!!
                    )
                )
            }
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }


    private fun getTotalMoney() {
        tempPrice = 0.0
        totalMoney = 0.0

        val d = getDatabaseHandler()!!.getLProductList()
        if (d.size > 0) {
            for (i in d) {
                totalMoney += (i.price * i.order_quantity)
            }
        }

        for (i in 0 until list!!.size) {
            if (!list!![i].isDisableBySupplierCondition) {
                tempPrice += (list!![i].price * list!![i].order_quantity)
            }
        }

        tv_total.text = Functions.formatMoney(tempPrice - voucher_value)
        temp_price.text = Functions.formatMoney(tempPrice)

        if (voucher_code.isNotBlank()) {
            rl_voucher.visibility = View.VISIBLE
            voucherCode.text =
                "#$voucher_code"
            voucherCode.paint.shader = Functions.gradientText(
                voucherCode,
                ContextCompat.getColor(this, R.color.title_orange_FF9736),
                ContextCompat.getColor(this, R.color.title_orange_DA5205)
            )
        } else {
            rl_voucher.visibility = View.GONE
        }

        if (voucher_value > 0) {
            voucher.text = "-${Functions.formatMoney(voucher_value)}"
        }

        if (tempPrice - voucher_value >= 200000) {
            ship.text = getString(R.string.text_mien_phi)
        } else {
            ship.text = getString(R.string.text_xac_dinh_sau)
        }

        image_top.txt_header_total.text = Functions.formatMoney(totalMoney)
    }

    override fun sendEditFalsed(message: Int) {
        progressBar3.visibility = View.GONE
    }

    override fun sendHightlightSuccess(list: ArrayList<Product>) {
        emptyAdapter.setNewHighlight(list)
    }

    override fun sendHighlightFail(erroMessage: Int) {

    }

    override fun sendProfileSuccess(profile: CustomerProfileResponse) {
        val add = Functions.getDefaultAddress(profile)

        if (add == null) {
            if (profile.alt_info != null && profile.alt_info!!.size > 0) {
                gotoAltAddress(2)
            } else {
                gotoUserInfomation(2, true)
            }
        } else {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CART.name
            dataObject.totalItem = list?.size.toString()
            var mTotalMoney = 0.0
            for (i in 0 until list!!.size) {
                mTotalMoney += (list!![i].price * list!![i].order_quantity)
            }
            dataObject.cartValue = mTotalMoney.toString()
            dataObject.listCart = Functions.toListProduct(list!!)
            val data = Gson().toJson(dataObject).toString()
            logTrackingVersion2(
                QuickstartPreferences.CLICK_CHECKOUT_BUTTON_v2,
                data
            )

            //gotoInvoiceDetail()
            if (isReorder) {
                if (OrderActivity.orderActivity != null)
                    OrderActivity.orderActivity!!.finish()

                gotoInvoiceDetail(list, false, voucher_value, voucher_code)

            } else {
                gotoInvoiceDetail(null, false, voucher_value, voucher_code)

            }
        }
    }

    override fun sendProfileFalsed(message: String) {
        Functions.showMessage(this, message)
    }

    override fun sendListVoucherSuccess(vouchers: VoucherResponse) {
        dialog = VoucherDialog(vouchers.data) { code ->
            cartPresenter.checkVoucher(list!!, code)
        }
        dialog!!.show(supportFragmentManager, dialog!!.tag)
    }

    override fun sendApplyVoucherSuccess(response: BestVoucherForCartResponse) {
        dialog?.dismiss()

        voucher_value = response.data!!.applied_value
        voucher_code = response.data.voucher_code
        voucher_uid = response.data.uid

        getTotalMoney()
    }

    override fun sendApplyVoucherFalsed(message: String) {
        Functions.showMessage(this, message)
    }

    override fun sendApplyVoucherSuccessContinue(response: BestVoucherForCartResponse) {
        if (getCheckCustomerResponse() != null) {
            cartPresenter.fetchProfile(getCheckCustomerResponse()!!.data.uid)
        }
    }

    override fun sendApplyVoucherFalsedContinue(message: String) {
        //todo voucher
        AskVoucherDialog(this, {
            // yes
            if (getCheckCustomerResponse() != null) {
                cartPresenter.loadVoucher(getCheckCustomerResponse()!!.data.uid)
            }
        }, {
            // no
        }, R.string.text_chon_lai_voucher)

        voucher_value = 0.0
        voucher_code = ""
        voucher_uid = ""

        getTotalMoney()
    }

    private fun loadData() {
        if (Functions.checkSupplierCondition(list!!)) {
            tv_attention.visibility = View.VISIBLE
        } else {
            tv_attention.visibility = View.GONE
        }

        cartAdapter = CartV1Adapter(this, list!!)

        vg_cart.adapter = cartAdapter

        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.CART.name
        dataObject.totalItem = list?.size.toString()
        var mTotalMoney = 0.0
        for (i in 0 until list!!.size) {
            mTotalMoney += (list!![i].price * list!![i].order_quantity)
        }
        dataObject.cartValue = mTotalMoney.toString()
        dataObject.listCart = Functions.toListProduct(list!!)
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.LOAD_CART_DETAILS_v2,
            data
        )


        bn_continue_shopping.setOnClickListener {
            if (list!!.size > 0) {
                if (checkConditionPayment(list!!)) {
                    if (Functions.checkInternet(this)) {
                        if (voucher_code.isNotBlank()) {
                            cartPresenter.checkVoucherForContinueShopping(list!!, voucher_code)
                        } else {
                            if(getCheckCustomerResponse()!=null) {
                                cartPresenter.fetchProfile(getCheckCustomerResponse()!!.data.uid)
                            }
                        }

                    } else {
                        Functions.showMessage(this, getString(R.string.no_internet))
                    }
                } else {
                    Functions.showMessage(this, getString(R.string.text_sp_k_du_dk))

                }
            }
        }

        bn_continue_shopping.setOnFocusChangeListener { _, hasFocus ->
            text_bn_continue_shopping.isSelected = hasFocus
        }


        bn_choose_voucher.setOnClickListener {
            if(getCheckCustomerResponse()!=null) {
                cartPresenter.loadVoucher(getCheckCustomerResponse()!!.data.uid)
            }
        }

        bn_choose_voucher.setOnFocusChangeListener { _, hasFocus ->
            text_bn_choose_voucher.isSelected = hasFocus
        }

        vg_cart.setOnChildViewHolderSelectedListener(object :
            OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView?,
                child: RecyclerView.ViewHolder?,
                position: Int,
                subposition: Int,
            ) {
                super.onChildViewHolderSelected(parent, child, position, subposition)
                cartPosition = position
                holder = child
            }
        })

        cartAdapter.setOnItemClickListener(object : CartV1Adapter.OnItemClickListener {

            override fun onRemoveClick(position: Int) {
                if (position < list!!.size) {
                    val logDataObject = LogDataRequest()
                    logDataObject.screen = Config.SCREEN_ID.CART.name
                    logDataObject.itemNo = position.toString()
                    logDataObject.itemName = list?.get(position)?.name
                    logDataObject.itemId = list?.get(position)?.uid
                    logDataObject.itemQuantity = list?.get(position)?.order_quantity.toString()
                    logDataObject.itemListPriceVat = list?.get(position)?.price.toString()

                    if (isReorder) {
                        list!!.removeAt(position)

                        tv_addcart_quantity.text =
                            getString(R.string.text_number_item_cart, list!!.size.toString())

                        if (list!!.size == 0) {
                            finish()
                        }
                    } else {
                        getDatabaseHandler()!!.deleteItemCart(list!![position].uid)
                        list!!.removeAt(position)
                        val count = getDatabaseHandler()!!.getCountCartItem()

                        if (count == 0) {
                            showCart(false)
                            emptyAdapter = EmptyCartAdapter(this@CartActivity)
                            emptyAdapter.setOnCallbackListener(object :
                                EmptyCartAdapter.OnCallBackListener {
                                override fun onItemHighlightClick(product: Product) {
                                    if (ProductDetailActivity.productDetailActivity != null) {
                                        ProductDetailActivity.productDetailActivity!!.finish()
                                    }

                                    gotoProductDetail(product, 0, "CartActivity", isPopup = false)
                                    finish()
                                }

                                override fun onContinuteClick() {
                                    val logDataObject1 = LogDataRequest()
                                    logDataObject1.screen = Config.SCREEN_ID.CART.name
                                    logTrackingVersion2(
                                        QuickstartPreferences.CLICK_CONTINUE_SHOPPING_BUTTON_v2,
                                        Gson().toJson(logDataObject1).toString()
                                    )
                                    comeHome()
                                }
                            })

                            vg_empty.adapter = emptyAdapter

                            cartPresenter.callHightlight()

                            image_top.txt_quantity.text = "0"
                            image_top.txt_header_total.text =
                                getString(R.string.text_money_format, "0")//"0đ"

                            Handler().postDelayed({
                                emptyAdapter.getContinuousButton().requestFocus()

                            }, 100)

                        } else {
                            image_top.txt_quantity.text = "$count"
                            tv_addcart_quantity.text =
                                getString(R.string.text_number_item_cart, count.toString())
                        }
                    }

                    if (Functions.checkSupplierCondition(list!!)) {
                        tv_attention.visibility = View.VISIBLE
                    } else {
                        tv_attention.visibility = View.GONE
                    }

                    cartAdapter.notifyDataSetChanged()
                    getTotalMoney()


                    logDataObject.totalItem = list?.size.toString()
                    var totalMoney = 0.0
                    for (i in 0 until list!!.size) {
                        totalMoney += (list!![i].price * list!![i].order_quantity)
                    }
                    logDataObject.cartValue = totalMoney.toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_CONFIRM_DELETE_BUTTON_v2,
                        Gson().toJson(logDataObject).toString()
                    )
                }
            }

            override fun onPlusClick(position: Int, quantity: Int) {
                list!![position].order_quantity = quantity

                if (!isReorder) {
                    getDatabaseHandler()!!.updateItemCart(
                        list!![position].uid,
                        Gson().toJson(list!![position])
                    )
                }

                val dataObject1 = LogDataRequest()
                dataObject1.screen = Config.SCREEN_ID.CART.name
                dataObject1.itemNo = position.toString()
                dataObject1.itemName = list!![position].name
                dataObject1.itemId = list!![position].uid
                dataObject1.itemQuantity = quantity.toString()
                dataObject1.itemListPriceVat = list?.get(position)?.price.toString()
                dataObject1.totalItem = list?.size.toString()
                var totalMoney = 0.0
                for (i in 0 until list!!.size) {
                    totalMoney += (list!![i].price * list!![i].order_quantity)
                }
                dataObject1.cartValue = totalMoney.toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_ADD_MORE_BUTTON_v2,
                    Gson().toJson(dataObject1).toString()
                )

                if (Functions.checkSupplierCondition(list!!)) {
                    tv_attention.visibility = View.VISIBLE
                } else {
                    tv_attention.visibility = View.GONE
                }

                cartAdapter.notifyItemChanged(position)
                getTotalMoney()

            }

            override fun onMinusClick(position: Int, quantity: Int) {
                list!![position].order_quantity = quantity

                if (!isReorder) {
                    getDatabaseHandler()!!.updateItemCart(
                        list!![position].uid,
                        Gson().toJson(list!![position])
                    )
                }

                val dataObject1 = LogDataRequest()
                dataObject1.screen = Config.SCREEN_ID.CART.name
                dataObject1.itemNo = position.toString()
                dataObject1.itemName = list!![position].name
                dataObject1.itemId = list!![position].uid
                dataObject1.itemQuantity = quantity.toString()
                dataObject1.itemListPriceVat = list?.get(position)?.price.toString()
                dataObject1.totalItem = list?.size.toString()
                var mTotal = 0.0
                for (i in 0 until list!!.size) {
                    mTotal += (list!![i].price * list!![i].order_quantity)
                }
                dataObject1.cartValue = mTotal.toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_REDUCE_QUANTITY_BUTTON_v2,
                    Gson().toJson(dataObject1).toString()
                )

                if (Functions.checkSupplierCondition(list!!)) {
                    tv_attention.visibility = View.VISIBLE
                } else {
                    tv_attention.visibility = View.GONE
                }

                cartAdapter.notifyItemChanged(position)
                getTotalMoney()
            }

            override fun onDetailClick(product: Product) {
                gotoProductDetail(product, 1, "CartActivity")
            }

        })
        getTotalMoney()

        Handler().postDelayed({
            bn_continue_shopping.requestFocus()
        }, 300)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 123) {
            if (isReorder) {
                gotoInvoiceDetail(list, false, voucher_value, voucher_code)

            } else {
                gotoInvoiceDetail(null, false, voucher_value, voucher_code)

            }
        }
    }

    override fun onStop() {
        if(getCheckCustomerResponse()!=null) {
            cartPresenter.updateCartOnline(
                CartRequest(
                    getCheckCustomerResponse()!!.data.uid,
                    list!!
                )
            )
        }
        super.onStop()
    }

    override fun onDestroy() {
        cartPresenter.disposeAPI()
        super.onDestroy()
    }

    private fun checkTopFocus() = image_top.bn_account.hasFocus() || image_top.bn_cart.hasFocus() ||
            image_top.bn_search.hasFocus() || bn_continue_shopping.hasFocus()


    private fun checkTopInLeftFocus() = image_top.bn_search.hasFocus()


    private fun showCart(boolean: Boolean) {
        if (boolean) {
            ln_product_count.visibility = View.VISIBLE
            layout_content_cart.visibility = View.VISIBLE
            rl_pay.visibility = View.VISIBLE

            vg_empty.visibility = View.GONE
        } else {
            ln_product_count.visibility = View.GONE
            layout_content_cart.visibility = View.GONE
            rl_pay.visibility = View.GONE

            vg_empty.visibility = View.VISIBLE
        }
    }

    private fun checkConditionPayment(list: List<Product>): Boolean {
        for (i in list) {
            if (!i.isDisableBySupplierCondition)
                return true
        }

        return false
    }
}
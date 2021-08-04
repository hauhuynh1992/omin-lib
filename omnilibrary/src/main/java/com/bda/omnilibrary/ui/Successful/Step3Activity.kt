package com.bda.omnilibrary.ui.Successful

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_step3.*
import kotlinx.android.synthetic.main.item_activity_header.view.bn_account
import kotlinx.android.synthetic.main.item_activity_header.view.bn_cart
import kotlinx.android.synthetic.main.item_activity_header.view.bn_search
import kotlinx.android.synthetic.main.item_activity_header.view.ic_account_top
import kotlinx.android.synthetic.main.item_activity_header.view.ic_bn_search
import kotlinx.android.synthetic.main.item_activity_header.view.image_cart
import kotlinx.android.synthetic.main.item_activity_header.view.txt_account
import kotlinx.android.synthetic.main.item_activity_header.view.txt_cart
import kotlinx.android.synthetic.main.item_activity_header.view.txt_header_total
import kotlinx.android.synthetic.main.item_activity_header.view.txt_phone
import kotlinx.android.synthetic.main.item_activity_header.view.txt_quantity
import kotlinx.android.synthetic.main.item_activity_header.view.txt_search
import kotlinx.android.synthetic.main.item_activity_header.view.txt_search_product
import kotlinx.android.synthetic.main.item_header_screen.view.*

class Step3Activity : BaseActivity() {

    private var type: Int = 3
    private var uid = ""
    private var isClearCart = true
    private var isShowStep = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3)

        uid = intent.getStringExtra("OID").toString()
        type = intent.getIntExtra("TYPE", 1)
        isShowStep = intent.getBooleanExtra("ISSHOWSTEP", true)
        isClearCart = intent.getBooleanExtra("isClearCart", true)

        val dataObject = LogDataRequest()
        dataObject.screen = Config.SCREEN_ID.CHECKOUT_RESULT.name
        dataObject.orderId = uid
        val data = Gson().toJson(dataObject).toString()
        logTrackingVersion2(
            QuickstartPreferences.ORDER_SUCCESSFUL_v2,
            data
        )

        initial()
    }

    private fun initial() {
        if (isClearCart) {
            val listData =
                Functions.getProductsBySupplierCondition(getDatabaseHandler()!!.getLProductList())
            getDatabaseHandler()!!.deleteCart()

            if (listData.size > 0) {
                addListItemToCart(listData)
            }
        }

        initChildHeader()

        code.text = uid

        when (type) {
            1 -> {
                image.setImageResource(R.mipmap.image_success_pay)
            }

            2 -> {
                image.setImageResource(R.mipmap.image_falsed)
                bn_try_again.visibility = View.VISIBLE
                contact.visibility = View.VISIBLE
                failed.visibility = View.VISIBLE

                thanks.visibility = View.GONE

                tv_title.setText(R.string.pay_failed)

            }

            3 -> {
                image.setImageResource(R.mipmap.image_success_pay)
                contact.visibility = View.VISIBLE
                tv_title.setText(R.string.pay_pending)

                thanks.visibility = View.GONE
            }

            4 -> {
                image.setImageResource(R.mipmap.image_success_pay)
                tv_title.setText(R.string.order_success)
            }
        }

        if (isShowStep) {
            step.visibility = View.VISIBLE

        } else {
            step.visibility = View.GONE

        }

        bn_try_again.setOnClickListener {

        }

        bn_continue_shopping.setOnClickListener {
            clearActivity {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHECKOUT_RESULT.name
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_CONTINUE_SHOPPING_BUTTON_v2,
                    data
                )
            }
        }

        bn_try_again.setOnFocusChangeListener { _, hasFocus ->
            text_bn_try_again.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_try_again.outlineSpotShadowColor =
                        ContextCompat.getColor(this, R.color.end_color)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_try_again.outlineSpotShadowColor =
                        ContextCompat.getColor(this, R.color.text_black_70)
                }
            }
        }

        bn_continue_shopping.setOnFocusChangeListener { _, hasFocus ->
            text_bn_continue_shopping.isSelected = hasFocus

            if (hasFocus) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_continue_shopping.outlineSpotShadowColor =
                        ContextCompat.getColor(this, R.color.end_color)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    item_bn_continue_shopping.outlineSpotShadowColor =
                        ContextCompat.getColor(this, R.color.text_black_70)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        clearActivity {
        }

    }

    private fun initChildHeader() {
        val d = getDatabaseHandler()!!.getLProductList()

        image_header.txt_quantity.text = d.size.toString()
        image_header.txt_header_total.text = getTotalMoney(d)

        if (getPreferenceHelper()!!.userInfo != null) {
            val info: CheckCustomerResponse = Gson().fromJson(
                getPreferenceHelper()?.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )

            @Suppress("SENSELESS_COMPARISON")
            if (info.data.phone != null && info.data.phone.isNotBlank())
                image_header.txt_phone.text = info.data.phone
        }

        ///////////// Set on Click /////////////
        image_header.bn_cart.setOnClickListener {
            clearActivity {
                gotoCart()
            }
        }

        image_header.bn_account.setOnClickListener {
            clearActivity {
                gotoAccount(false)
            }
        }

        image_header.bn_search.setOnClickListener {
            clearActivity {
                gotoSearchActivity()
            }
        }

        image_header.bn_header_back.setOnClickListener {
            clearActivity {
            }
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

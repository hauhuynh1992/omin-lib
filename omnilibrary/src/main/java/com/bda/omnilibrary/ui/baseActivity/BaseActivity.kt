package com.bda.omnilibrary.ui.baseActivity


import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bda.omnilibrary.R
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.dialog.LandingPageDialog
import com.bda.omnilibrary.dialog.ProductBySupplierDialog
import com.bda.omnilibrary.dialog.RemainQrDialog
import com.bda.omnilibrary.dialog.SupplierProductForInvoiceDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.PaymentMethod.Step2Activity
import com.bda.omnilibrary.ui.promotionDetail.PromotionDetailActivity
import com.bda.omnilibrary.ui.Successful.Step3Activity
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.orderFragment.OrderFragment
import com.bda.omnilibrary.ui.altAddressActivity.AltAddressActivity
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity.Companion.REQUEST_CUSTOMER_RESULT_CODE
import com.bda.omnilibrary.ui.brandShopDetail.BrandShopDetailActivity
import com.bda.omnilibrary.ui.cartActivity.CartActivity
import com.bda.omnilibrary.ui.categoryDetail.CategoryDetailActivity
import com.bda.omnilibrary.ui.collectionDetail.CollectionDetailActivity
import com.bda.omnilibrary.ui.ewallet.EwalletActivity
import com.bda.omnilibrary.ui.invoiceDetailActivity.InvoiceDetailActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamLandscapeActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamPortraitActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamTransitionActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.ui.mediaActivity.MediaActivity
import com.bda.omnilibrary.ui.orderDetail.OrderActivity
import com.bda.omnilibrary.ui.productDetailActivity.ProductDetailActivity
import com.bda.omnilibrary.ui.searchActivity.SearchActivity
import com.bda.omnilibrary.ui.userInfomationActivity.UserInformationActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity
import com.bda.omnilibrary.ui.voucherV2.VoucherV2Activity
import com.bda.omnilibrary.ui.warrantyActivity.WarrantyActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.bda.omnilibrary.views.SfTextView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import kotlinx.android.synthetic.main.item_header_screen.*
import kotlinx.android.synthetic.main.item_header_screen.view.*
import java.util.*
import java.util.concurrent.TimeUnit


abstract class BaseActivity : BaseTracking() {

    private var databaseHandler: DatabaseHandler? = null
    private var fm: FragmentManager? = null
    private var ft: FragmentTransaction? = null
    private var preference: PreferencesHelper? = null
    private var customerInfo: CheckCustomerResponse? = null
    private var layoutToLoadId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.databaseHandler = DatabaseHandler(this)
        this.preference = PreferencesHelper(this)
        loadCustomerInfo()
    }

    override fun onResume() {
        loadCustomerInfo()
        super.onResume()
    }

    fun getCheckCustomerResponse(): CheckCustomerResponse? {
        return customerInfo;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val resultCustomer = data?.getStringExtra(AuthenticationActivity.RESPONSE_CUSTOMER_RESULT)
        if (resultCustomer != null && resultCustomer.isNotBlank()) {
            when (requestCode) {
                REQUEST_CUSTOMER_RESULT_CODE -> {
                    Functions.showMessage(this, resources.getString(R.string.txt_login_success))
                    loadCustomerInfo()
                    preLoadHeader()

                }
            }
        }
    }

    private fun preLoadHeader() {
        if (mActivityHeader != null) {
            initChildHeader(mActivityHeader!!, mActivityType)
        }
        if (mFragmentHeader != null) {
            initChildHeaderForFragment(mFragmentHeader!!)
        }
    }

    private fun loadCustomerInfo() {
        customerInfo = Gson().fromJson(
            preference!!.userInfo,
            object : TypeToken<CheckCustomerResponse>() {}.type
        )
    }

    fun setLanguage(locale: String) {
        Functions.setLocale(this, locale)
    }

    fun checkPermissions(): Boolean {
        var find = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        var course = PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return find && course
    }

    fun getFManager(): FragmentManager {
        if (fm == null)
            fm = supportFragmentManager
        return fm!!
    }

    open fun loadFragment(fragment: Fragment, layout: Int, isAnimation: Boolean) {
        if (fm == null) {
            fm = getFManager();
            ft = fm!!.beginTransaction();
            ft!!.add(layout, fragment, fragment.javaClass.name)
        } else {
            val tmp =
                fm!!.findFragmentByTag(fragment.javaClass.name)
            if (tmp != null && tmp.isVisible) {
                return
            }

            ft = fm!!.beginTransaction()
            if (isAnimation) {
                ft!!.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right
                )
            }
            ft!!.replace(layout, fragment, fragment.javaClass.name)
            if (isAnimation) {
                ft!!.addToBackStack(fragment.javaClass.name)
            }
        }
        ft!!.commit()
    }

    fun addItemToCart(product: Product) {
        val dbproduct = databaseHandler?.getExistItem(product.uid)
        product.order_quantity = 1
        if (dbproduct != null) {
            dbproduct.order_quantity += 1
            product.order_quantity = dbproduct.order_quantity
            databaseHandler?.deleteItemCart(product.uid)
        }
        databaseHandler?.insertProduct(product.uid, Gson().toJson(product))
    }

    fun addListItemToCart(products: ArrayList<Product>) {
        for (product in products) {
            databaseHandler?.insertProduct(product.uid, Gson().toJson(product))
        }
    }

    fun gotoMainActivity(
        productId: String?,
        isPopup: Boolean,
        isNotification: Boolean,
        popup_type: String,
        landingUrlImage: String?
    ) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(QuickstartPreferences.PRODUCT_ID, productId)
        intent.putExtra(QuickstartPreferences.POPUP, isPopup)
        intent.putExtra(QuickstartPreferences.NOTIFICATION, isNotification)
        intent.putExtra(QuickstartPreferences.POPUP_TYPE, popup_type)
        intent.putExtra(QuickstartPreferences.LANDING_PAGE, landingUrlImage)
        startActivity(intent)
    }

    fun gotoUserInfomation(mode: Int, isNewUser: Boolean = false) {
        val intent = Intent(this, UserInformationActivity::class.java)
        intent.putExtra("mode", mode)
        intent.putExtra("isNewUser", isNewUser)
        startActivity(intent)
    }

    fun gotoLandingPage(url: String?) {
        val dialog = LandingPageDialog()
        val bundle = bundleOf("URL_LANDING" to url)
        bundle.putString("URL_LANDING", url);
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, dialog.tag)
    }

    fun gotoSupplierProductDialog(
        list: ArrayList<Pair<String, ArrayList<Product>>>,
        isHighlightDeliveryDay: Boolean = false,
        onClick: (data: Pair<String, ArrayList<Product>>) -> Unit
    ) {
        val dialog = ProductBySupplierDialog(list, isHighlightDeliveryDay, onClick)
        /*bundle.putString("URL_LANDING", url);
        dialog.arguments = bundle*/
        dialog.show(supportFragmentManager, dialog.tag)
    }

    fun gotoSupplierProductForInvoiceDialog(
        order: ListOrderResponceV3.Data,
        isHighlightDeliveryDay: Boolean = false
    ) {
        val dialog = SupplierProductForInvoiceDialog(order)
        /*bundle.putString("URL_LANDING", url);
        dialog.arguments = bundle*/
        dialog.show(supportFragmentManager, dialog.tag)
    }

    fun gotoProductDetail(
        product: Product,
        position: Int,
        clickFrom: String,
        sectionName: String? = null,
        sectionType: String? = null,
        sectionIndex: String? = null,
        isPopup: Boolean = false
    ) {
        var mIntent = Intent(this, ProductDetailActivity::class.java)
        mIntent.putExtra("PRODUCT_POSITION", position)
        mIntent.putExtra("CLICK_FROM", clickFrom)
        mIntent.putExtra("SECTION_NAME", sectionName)
        mIntent.putExtra("SECTION_INDEX", sectionIndex)
        mIntent.putExtra("SECTION_TYPE", sectionType)
        mIntent.putExtra(QuickstartPreferences.PRODUCT_MODEL, Gson().toJson(product))
        mIntent.putExtra(QuickstartPreferences.POPUP, isPopup)
        startActivity(mIntent)
    }

    fun gotoProductDetail(
        product_id: String,
        position: Int,
        clickFrom: String,
        isHasVideo: Boolean = false
    ) {
        var mIntent = Intent(this, ProductDetailActivity::class.java)
        mIntent.putExtra("PRODUCT_POSITION", position)
        mIntent.putExtra("CLICK_FROM", clickFrom)
        mIntent.putExtra(QuickstartPreferences.PRODUCT_ID, product_id)
        startActivity(mIntent)
    }

    fun gotoCart() {
        if (Functions.checkInternet(this)) {
            val intent = Intent(this@BaseActivity, CartActivity::class.java)
            startActivity(intent)
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }

    fun gotoCartByOrder(list: ArrayList<Product>) {
        if (Functions.checkInternet(this)) {
            val intent = Intent(this@BaseActivity, CartActivity::class.java)
            intent.putExtra("renew_list", Gson().toJson(list))
            startActivity(intent)
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }

//    fun gotoInvoiceDetail(list: ArrayList<Product>?, isNewUser: Boolean = false) {
//        val intent = Intent(this, OrderActivity::class.java)
//        if (list != null)
//            intent.putExtra("list", Gson().toJson(list))
//
//        intent.putExtra("isNewUser", isNewUser)
//        startActivity(intent)
//    }

    fun gotoInvoiceDetail(
        list: ArrayList<Product>?, isNewUser: Boolean = false, voucherValue: Double,
        voucherCode: String
    ) {
        val intent = Intent(this, OrderActivity::class.java)
        if (list != null)
            intent.putExtra("list", Gson().toJson(list))
        intent.putExtra("voucherValue", voucherValue)
        intent.putExtra("voucherCode", voucherCode)

        intent.putExtra("isNewUser", isNewUser)
        startActivity(intent)
    }

    fun gotoInvoiceDetail(data: ListOrderResponceV3.Data) {
        val intent = Intent(this@BaseActivity, InvoiceDetailActivity::class.java)
        intent.putExtra("order", Gson().toJson(data))
        startActivityForResult(intent, OrderFragment.GOTO_DETAIL_INVOICE_REQUEST)
    }

    fun gotoAccount(isInvoice: Boolean) {
        if (Functions.checkInternet(this)) {
            val intent = Intent(this@BaseActivity, AccountActivity::class.java)
            intent.putExtra("ISINVOICE", isInvoice)
            startActivity(intent)
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }

    fun gotoAuthentication(requestCode: Int) {
        if (Functions.checkInternet(this)) {
            val intent = Intent(this@BaseActivity, AuthenticationActivity::class.java)
            intent.putExtra(AuthenticationActivity.REQUEST_CUSTOMER_RESULT, requestCode)
            startActivityForResult(intent, requestCode)
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }


    fun startTimer(
        timeView: SfTextView,
        onFinish: () -> Unit,
        timeDown: (time: String) -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(600000L, 1000L) {
            override fun onFinish() {
                timeView.text = "00 : 00"

                Functions.showMessage(
                    this@BaseActivity,
                    getString(R.string.text_het_han_thanh_toan)
                )

                onFinish.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {

                timeView.text = String.format(
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )

                timeDown.invoke(
                    String.format(
                        " %02d : %02d ",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        millisUntilFinished
                                    )
                                )
                    )
                )
            }
        }.start()
    }

    fun startTimer(
        onFinish: () -> Unit,
        timeDown: (time: String) -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(4000L, 1000L) {
            override fun onFinish() {
                onFinish.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {

                timeDown.invoke(
                    String.format(
                        "%02d",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        millisUntilFinished
                                    )
                                )
                    )
                )
            }
        }.start()
    }

    fun startLoginTimer(
        timeView: SfTextView,
        onFinish: () -> Unit,
        timeDown: (time: String) -> Unit
    ): CountDownTimer {
        return object : CountDownTimer(600000L, 1000L) {
            override fun onFinish() {
                timeView.text = "00 : 00"

                Functions.showMessage(
                    this@BaseActivity,
                    getString(R.string.text_het_han_login)
                )

                onFinish.invoke()
            }

            override fun onTick(millisUntilFinished: Long) {

                timeView.text = String.format(
                    "%02d : %02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            )
                )

                timeDown.invoke(
                    String.format(
                        " %02d : %02d ",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        millisUntilFinished
                                    )
                                )
                    )
                )
            }
        }.start()
    }

    fun gotoAltAddress(mode: Int) {
        val intent = Intent(this, AltAddressActivity::class.java)
        intent.putExtra("Mode", mode)
        startActivityForResult(intent, 321)
    }

    fun gotoSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    fun getConfig(partner: Config.PARTNER, isBox2021: Boolean = false) {
        when (partner) {
            Config.PARTNER.FPT -> {
                if (GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS
                ) {
                    Config.macAddress = Functions.getMacAddress()!!
                } else {
                    Functions.onGetFirmwareVersionAndMacAddress(this)
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Config.platform = "box2017"
                    Config.lastKeyDownTime = 200
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    Config.platform = "box2018"
                    Config.lastKeyDownTime = 200
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    Config.platform = "box2019"
                    Config.lastKeyDownTime = 100
                } else if (isBox2021) {
                    Config.platform = "box2021"
                    Config.lastKeyDownTime = 100
                    Config.macAddress = Functions.getMacAddressForSei()
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    Config.platform = "box2020"
                    Config.lastKeyDownTime = 100
                }

            }
            Config.PARTNER.OMNISHOPVNPT -> {
                Config.platform = "boxvnpt"
                Config.lastKeyDownTime = 100
            }
            Config.PARTNER.VIETTEL -> {

            }
            Config.PARTNER.OMNISHOPEU -> {
                Config.platform = "omnishopeu"
                Config.lastKeyDownTime = 100
            }
        }
    }

    fun gotoDiscoveryVoice(requestCode: Int) {
        val intent = Intent(this, DiscoveryVoiceActivity::class.java)
        intent.putExtra(DiscoveryVoiceActivity.REQUEST_VOICE_RESULT, requestCode)
        startActivityForResult(intent, requestCode)
    }

    val mHandler = Handler()
    var mRunnable: Runnable? = null
    var remainQrDialog: RemainQrDialog? = null

    fun createRemainArDialog(type: String) {
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable!!)
            mRunnable = null
        }

        mRunnable = Runnable {
            if (remainQrDialog == null) {
                remainQrDialog = RemainQrDialog(this, "") {
                    val dataObject = LogDataRequest()
                    when (type) {
                        "momo" -> {
                            dataObject.screen = Config.SCREEN_ID.MOMO_CHECKOUT.name

                        }

                        "moca" -> {
                            dataObject.screen = Config.SCREEN_ID.MOCA_CHECKOUT.name
                        }

                        "vnpay" -> {
                            dataObject.screen = Config.SCREEN_ID.VNPAY_CHECKOUT.name

                        }
                    }
                    dataObject.popUpName =
                        getString(R.string.text_pop_up_remind_2_minutes)//"Pop-up remind 2 phút ở QR"
                    val data = Gson().toJson(dataObject).toString()
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_YES_BUTTON_POP_UP_v2,
                        data
                    )
                    remainQrDialog = null
                }
            }
        }

        mHandler.postDelayed(mRunnable!!, 120000)
    }

    fun gotoCodWarrantyActivity() {
        val intent = Intent(this, WarrantyActivity::class.java)
        startActivity(intent)
    }

    fun gotoMediaProduct(product: Product, position: Int) {
        var mIntent = Intent(this, MediaActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.PRODUCT_MODEL, Gson().toJson(product))
        mIntent.putExtra("POSITION", position)

        startActivity(mIntent)
    }

    enum class Tab {
        SEARCH, CART, ACCOUNT
    }

    private var mActivityHeader: View? = null
    private var mActivityType: Tab? = null

    @SuppressLint("StringFormatMatches")
    fun initChildHeader(header: View, type: Tab? = null) {
        mActivityHeader = header
        mActivityType = type
        val d = getDatabaseHandler()!!.getLProductList()
        header.txt_quantity.text = d.size.toString()
        header.txt_header_total.text = getTotalMoney(d)
        header.txt_phone.visibility = View.VISIBLE
        header.txt_phone.transformationMethod = null
        if (getCheckCustomerResponse() != null) {
            if (getCheckCustomerResponse()!!.data.phone.isNotEmpty()) {
                header.txt_phone.text = getCheckCustomerResponse()!!.data.phone
            } else {
                header.txt_phone.visibility = View.GONE
            }
        } else {
            header.txt_phone.text = getString(R.string.dang_nhap)
        }

        if (Config.megaMenu != null && Config.megaMenu!!.order_total > 0) {
            header.text_order.text = getString(
                R.string.text_order_date,
                Config.megaMenu!!.order_total, Config.megaMenu!!.order_collection.collection_name,
                Config.megaMenu!!.date_total_order
            )
        } else {
            header.text_order.visibility = View.GONE
        }

        ///////////// Set on Click /////////////

        header.bn_cart.setOnClickListener {
            if (type == null || type != Tab.CART) {
                if (customerInfo != null)
                    clearActivity {
                        gotoCart()
                    }
                else gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
            }
        }


        header.bn_account.setOnClickListener {
            if (type == null || type != Tab.ACCOUNT) {
                if (customerInfo != null)
                    clearActivity {
                        gotoAccount(false)
                    }
                else gotoAuthentication(REQUEST_CUSTOMER_RESULT_CODE)
            }

        }

        header.bn_search.setOnClickListener {
            if (type == null || type != Tab.SEARCH)
                clearActivity {
                    gotoSearchActivity()
                }
        }

        header.bn_header_back.setOnClickListener {
            finish()
        }

        /////////////////////// setOnFocusChangeListener ////////////////////
        header.bn_account.setOnFocusChangeListener { view, hasFocus ->
            header.txt_phone.isSelected = hasFocus
            header.txt_account.isSelected = hasFocus
            if (hasFocus) {
                header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )
            } else {
                header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_gray_C4C4C4
                    )
                )
            }
        }

        header.bn_cart.setOnFocusChangeListener { view, hasFocus ->
            header.txt_header_total.isSelected = hasFocus
            header.txt_cart.isSelected = hasFocus
            header.txt_quantity.isSelected = hasFocus

            if (hasFocus) {
                header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_white)
            } else {
                header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_gray)
            }
        }

        header.bn_search.setOnFocusChangeListener { view, hasFocus ->
            header.txt_search.isSelected = hasFocus
            header.txt_search_product.isSelected = hasFocus

            if (hasFocus) {
                header.ic_bn_search.setImageResource(R.mipmap.ic_search_white)
            } else {
                header.ic_bn_search.setImageResource(R.mipmap.ic_search_gray)
            }
        }
    }

    private var mFragmentHeader: View? = null
    fun initChildHeaderForFragment(header: View) {
        mFragmentHeader = header
        val d = getDatabaseHandler()!!.getLProductList()
        header.txt_quantity.text = d.size.toString()
        header.txt_header_total.text = getTotalMoney(d)
        header.txt_phone.visibility = View.VISIBLE
        header.txt_phone.transformationMethod = null
        if (getCheckCustomerResponse() != null) {
            if (getCheckCustomerResponse()!!.data.phone.isNotEmpty()) {
                header.txt_phone.text = getCheckCustomerResponse()!!.data.phone
            } else {
                header.txt_phone.visibility = View.GONE
            }
        } else {
            header.txt_phone.text = getString(R.string.dang_nhap)
        }

        ///////////// Set on Click /////////////

        header.bn_cart.setOnClickListener {
            clearActivity {
                gotoCart()
            }
        }


        header.bn_account.setOnClickListener {
            clearActivity {
                gotoAccount(false)
            }
        }

        header.bn_search.setOnClickListener {
            clearActivity {
                gotoSearchActivity()
            }
        }

        header.bn_header_back.setOnClickListener {
            onBackPressed()
        }

        /////////////////////// setOnFocusChangeListener ////////////////////
        header.bn_account.setOnFocusChangeListener { view, hasFocus ->
            header.txt_phone.isSelected = hasFocus
            header.txt_account.isSelected = hasFocus
            if (hasFocus) {
                header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_white
                    )
                )
            } else {
                header.ic_account_top.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.title_gray_C4C4C4
                    )
                )
            }
        }

        header.bn_cart.setOnFocusChangeListener { view, hasFocus ->
            header.txt_header_total.isSelected = hasFocus
            header.txt_cart.isSelected = hasFocus
            header.txt_quantity.isSelected = hasFocus

            if (hasFocus) {
                header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_white)
            } else {
                header.image_cart.setImageResource(R.mipmap.ic_style_1_cart_gray)
            }
        }

        header.bn_search.setOnFocusChangeListener { view, hasFocus ->
            header.txt_search.isSelected = hasFocus
            header.txt_search_product.isSelected = hasFocus

            if (hasFocus) {
                header.ic_bn_search.setImageResource(R.mipmap.ic_search_white)
            } else {
                header.ic_bn_search.setImageResource(R.mipmap.ic_search_gray)
            }
        }
    }

    fun reloadTotalMoney(header: View) {
        val d = getDatabaseHandler()!!.getLProductList()

        header.txt_quantity.text = d.size.toString()
        header.txt_header_total.text = getTotalMoney(d)
    }


    fun getDatabaseHandler(): DatabaseHandler? {
        return databaseHandler
    }

    fun gotoLiveStreamProduct(livestreamID: String) {
        val mIntent = Intent(this, LiveStreamTransitionActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.LIVESTREAM, livestreamID)
        startActivity(mIntent)
    }

    fun gotoDetailPromotion(promotion: Promotion) {
        val mIntent = Intent(this, PromotionDetailActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.PROMOTION_MODEL, Gson().toJson(promotion))
        startActivity(mIntent)
    }

    fun gotoDetailPromotion(uid: String) {
        val mIntent = Intent(this, PromotionDetailActivity::class.java)
        mIntent.putExtra("UID", uid)
        startActivity(mIntent)
    }

    fun gotoLiveStreamPortraitProduct(livestreamID: String) {
        var mIntent = Intent(this, LiveStreamPortraitActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.LIVESTREAM, livestreamID)
        startActivity(mIntent)
    }

    fun gotoCollection(
        uid: String,
        name: String = "",
        sectionName: String? = null,
        sectionIndex: String? = null,
        sectionTye: String? = null,
        brandShopID: String = "",
        brandShopName: String = ""
    ) {
        val intent =
            Intent(this, CollectionDetailActivity::class.java)
        intent.putExtra("COLLECTION_PARENT_CODE", uid)
        intent.putExtra("COLLECTION_PARENT_NAME", name)
        intent.putExtra("SECTION_INDEX", sectionIndex)
        intent.putExtra("SECTION_NAME", sectionName)
        intent.putExtra("SECTION_TYPE", sectionTye)
        intent.putExtra("BRAND_SHOP_ID", brandShopID)
        intent.putExtra("BRAND_SHOP_NAME", brandShopName)
        startActivity(intent)
    }

    fun gotoCategory(
        uid: String,
        name: String = "",
        sectionName: String? = null,
        sectionIndex: String? = null,
        sectionTye: String? = null,
        brandShopID: String = "",
        brandShopName: String = ""
    ) {
        val intent =
            Intent(this, CategoryDetailActivity::class.java)
        intent.putExtra("COLLECTION_PARENT_CODE", uid)
        intent.putExtra("COLLECTION_PARENT_NAME", name)
        intent.putExtra("SECTION_INDEX", sectionIndex)
        intent.putExtra("SECTION_NAME", sectionName)
        intent.putExtra("SECTION_TYPE", sectionTye)
        intent.putExtra("BRAND_SHOP_ID", brandShopID)
        intent.putExtra("BRAND_SHOP_NAME", brandShopName)
        startActivity(intent)
    }

    fun gotoCheckoutStep2Activity(
        isCod: Boolean,
        price: Double,
        deliveryDay: String,
        voucher_id: String,
        code: String,
        voucher_value: String,
        list: ArrayList<Product>? = null,
        isNewUser: Boolean = false
    ) {
        val intent = Intent(this, Step2Activity::class.java)
        intent.putExtra("PRICE", price)
        intent.putExtra("DISABLECOD", isCod)
        intent.putExtra("VOUCHER_UID", voucher_id)
        intent.putExtra("DELIVERY_DAY", deliveryDay)
        intent.putExtra("VOUCHER_CODE", code)
        intent.putExtra("VOUCHER_VALUE", voucher_value)
        if (list != null)
            intent.putExtra("list", Gson().toJson(list))

        intent.putExtra("isNewUser", isNewUser)

        startActivity(intent)
    }


    /**
     * @param type
     * 1 -> success
     * 2 -> failed
     * 3 -> pay pending
     * 4 -> order pending
     */

    fun gotoCheckoutStep3Activity(
        orderId: String,
        type: Int,
        isClearCart: Boolean,
        isShowStep: Boolean = true
    ) {
        val intent =
            Intent(this, Step3Activity::class.java)
        intent.putExtra("OID", orderId)
        intent.putExtra("TYPE", type)
        intent.putExtra("ISSHOWSTEP", isShowStep)
        intent.putExtra("isClearCart", isClearCart)

        startActivity(intent)
    }

    fun gotoVoucher(list: ArrayList<Voucher>, products: ArrayList<Product>? = null) {
        val intent = Intent(this, VoucherV2Activity::class.java)
        intent.putExtra("VOUCHERS", list)
        if (products != null)
            intent.putExtra("PRODUCTS", Gson().toJson(products))

        startActivityForResult(intent, 100)
    }

    fun gotoEwallet(
        vaucher_code: String,
        vaucher_uid: String,
        vaucher_value: String,
        method: String,
        price: String,
        day: String,
        requestType: Int,
        list: ArrayList<Product>? = null,
        isNewUser: Boolean = false
    ) {
        val intent = Intent(this, EwalletActivity::class.java)
        intent.putExtra("VOUCHER_CODE", vaucher_code)
        intent.putExtra("VOUCHER_VALUE", vaucher_value)
        intent.putExtra("VOUCHER_UID", vaucher_uid)
        intent.putExtra("METHOD", method)
        intent.putExtra("DELIVERY_DAY", day)
        intent.putExtra("REQUEST_TYPE", requestType)
        intent.putExtra("PRICE", price)
        intent.putExtra("isNewUser", isNewUser)

        if (list != null)
            intent.putExtra("list", Gson().toJson(list))

        startActivity(intent)
    }

    fun comeHome() {
        if (CartActivity.cartActivity != null) {
            CartActivity.cartActivity!!.finish()
        }

        if (AccountActivity.getMaiActivity() != null) {
            AccountActivity.getMaiActivity()!!.finish()
        }

        if (SearchActivity.getMaiActivity() != null) {
            SearchActivity.getMaiActivity()!!.finish()
        }
    }

    fun gotoBrandShopDetail(uid: String) {
        val mIntent = Intent(this, BrandShopDetailActivity::class.java)
        mIntent.putExtra(QuickstartPreferences.PRODUCT_BRAND_SHOP_ID, uid)
        startActivity(mIntent)
    }

    fun getBrandShop(): ArrayList<BrandShop>? {
        if (Config.homeData != null) {
            for (i in Config.homeData!!.data.section) {
                if (i.sectionValue == "brand_shop" && i.brand_shop.size > 0)
                    return i.brand_shop
            }
        }

        return null
    }

    fun getTotalMoney(list: List<Product>): String {
        var totalMoney = 0.0
        for (i in list.indices) {
            totalMoney += (list[i].price * list[i].order_quantity)
        }
        return Functions.formatMoney(totalMoney)
    }

    fun clearActivity(call: () -> Unit) {
        if (ProductDetailActivity.productDetailActivity != null)
            ProductDetailActivity.productDetailActivity?.finish()
        if (OrderActivity.orderActivity != null)
            OrderActivity.orderActivity?.finish()
        if (Step2Activity.step2Activity != null)
            Step2Activity.step2Activity?.finish()
        if (CartActivity.cartActivity != null)
            CartActivity.cartActivity?.finish()
        if (MediaActivity.getActivity() != null)
            MediaActivity.getActivity()?.finish()
        if (InvoiceDetailActivity.activity != null)
            InvoiceDetailActivity.activity?.finish()
        if (CategoryDetailActivity.getActivity() != null)
            CategoryDetailActivity.getActivity()!!.finish()
        if (BrandShopDetailActivity.getActivity() != null)
            BrandShopDetailActivity.getActivity()!!.finish()
        if (CollectionDetailActivity.getActivity() != null)
            CollectionDetailActivity.getActivity()!!.finish()
        if (LiveStreamActivity.getActivity() != null)
            LiveStreamActivity.getActivity()!!.finish()
        if (LiveStreamLandscapeActivity.getActivity() != null)
            LiveStreamLandscapeActivity.getActivity()!!.finish()
        if (LiveStreamPortraitActivity.getActivity() != null)
            LiveStreamPortraitActivity.getActivity()!!.finish()

        call.invoke()
        finish()
    }

    fun getPreferenceHelper() = preference

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) { // Works for Activity
            // Get called every-time when application went to background.
            finish()
        } else if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) { // Works for FragmentActivty
            finish()
        }
    }

    fun getDefaultLanguageCode(): String? {
        val locale = Locale.getDefault()
        val language = StringBuilder(locale.language)
        val country = locale.country
        if (!TextUtils.isEmpty(country)) {
            language.append("-")
            language.append(country)
        }
        return language.toString()
    }

    fun setLayoutId(id: Int) {
        this.layoutToLoadId = id
    }

    fun layoutToLoadId() = this.layoutToLoadId

    open fun focusDummyView() {}
    open fun hideDetailProduct() {}
}

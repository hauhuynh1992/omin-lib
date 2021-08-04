package com.bda.omnilibrary.ui.voucherV2

import android.content.Context
import com.bda.omnilibrary.R
import com.bda.omnilibrary.api.APIManager
import com.bda.omnilibrary.database.DatabaseHandler
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class VoucherV2Presenter(
    view: VoucherV2Contract.View,
    context: Context,
    vouchers: ArrayList<Voucher>,
    products: ArrayList<Product>?
) : VoucherV2Contract.Presenter {
    private var mView: VoucherV2Contract.View = view
    private var mContext = context
    private var mSubscription: Disposable? = null
    private var mHelper: PreferencesHelper
    private var databaseHandler: DatabaseHandler? = null
    private var userInfo: CheckCustomerResponse? = null
    private var addressData: ContactInfo? = null
    private var voucherList: ArrayList<Voucher>? = null
    private var list: ArrayList<Product>

    init {
        this.mHelper = PreferencesHelper(mContext)
        this.databaseHandler = DatabaseHandler(context)

        list = products ?: Functions.removeProductBySupplierCondition(databaseHandler!!.getLProductList())

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
        /*if (mHelper.userInfoTemp != null) {
            addressData = Gson().fromJson(
                mHelper.userInfoTemp,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }*/
        fetchProfile(userInfo!!.data.uid)

        loadVoucherData(vouchers)
    }

    override fun loadVaucher(vaucher: String) {

        mSubscription = APIManager.getInstance().getApi()
            .getApplyVoucherForCart(
                mappingCartToRequest(
                    list,
                    vaucher
                )!!
            )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

    private fun onRetrieveDataSuccess(model: BestVoucherForCartResponse?) {
        if (model != null && model.status == 200) {
            if (model.data!!.applied_value > 0) {
                mView.sendVaucherSuccess(model)
            } else {
                mView.sendVaucherFalsed(model.message)
            }
        } else {
            mView.sendVaucherFalsed(mContext.getString(R.string.error_voucher_1))
        }
    }


    private fun onRetrieveDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        mView.sendVaucherFalsed(mContext.getString(R.string.error_voucher_1))
    }

    private fun fetchProfile(uid: String) {
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {

        val add = Functions.getDefaultAddress(model)

        if (add != null) {
            this.addressData = add

        }
    }

    private fun onRetrieveProfileDataFailed(message: Throwable) {
        onRetrieveDataFailed(message)
    }


    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        voucher: String
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (userInfo != null && addressData != null && items.size > 0) {
            return PaymentRequest(
                platform = "fptplaybox",
                payType = "momo",
                voucher_code = voucher,
                voucher_uid = "",
                cid = userInfo!!.data.uid,
                phone = addressData!!.phone_number,
                address = addressData!!.address.address_des,
                province = addressData!!.address.customer_province.uid,
                district = addressData!!.address.customer_district.uid,
                items = items,
                addressType = addressData!!.address.address_type,
                name = addressData!!.customer_name,
                requestDeliveryTime = "",
                note = "",
                created_phone_number = "",
                created_province = "",
                created_district = "",
                created_customer_name = "",
                created_address_des = "",
                create_address_type = 1
            )
        }
        return null
    }

    private fun loadVoucherData(vouchers: ArrayList<Voucher>) {
        voucherList = vouchers

        // todo remove exp time
        if (voucherList != null && voucherList!!.size > 0) {
            for (i in voucherList!!.size - 1 downTo 0 step 1) {
                if (!Functions.isExpTimestamp(voucherList!![i].stop_at)) {
                    voucherList!!.removeAt(i)
                }
            }
        }

        mView.sendVouchersFromDatabase(voucherList!!)

        //mView.sendTotalMoney(getTotalMoney())
    }

    /*private fun getTotalMoney(): String {
        var totalMoney: Double = 0.0
        for (i in 0 until list.size) {
            totalMoney += (list[i].price * list[i].order_quantity)
        }

        return mContext.getString(R.string.total_cart_money) + " " + Functions.formatMoney(
            totalMoney
        )
    }*/

}
package com.bda.omnilibrary.ui.PaymentMethod

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
import retrofit2.HttpException

class Step2Presenter(view: Step2Contract.View, context: Context) :
    Step2Contract.Presenter {
    private var mView: Step2Contract.View = view
    private var mSubscription: Disposable? = null
    private var mContext: Context = context
    private var mHelper: PreferencesHelper
    private var request: PaymentRequest? = null
    private var databaseHandler: DatabaseHandler?
    private var userInfo: CheckCustomerResponse? = null
    private var addressData: ContactInfo? = null
    private var requestType: String = "work_day"

    private var list: ArrayList<Product>? = null

    private var voucher = ""
    private var voucherId = ""

    init {
        this.mHelper = PreferencesHelper(mContext)
        this.databaseHandler = DatabaseHandler(context)

        if (mHelper.userInfo != null) {
            userInfo = Gson().fromJson(
                mHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
    }

    /////////////// public function ///////////////

    override fun loadPresenter(
        vaucher: String,
        voucher_uid: String,
        requestType: Int,
        list: ArrayList<Product>?
    ) {

        this.voucher = vaucher
        this.voucherId = voucher_uid
        this.list = list

        if (requestType == 1) {
            this.requestType = "work_day"
        } else {
            this.requestType = "all_day"
        }

        fetchProfile()
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }

        databaseHandler = null
    }


    /////////////// private handle retrieve data ///////////////

    private fun onRetrieveDataSuccess(model: MomoPaymentResponce?) {
        if (model!!.statusCode == 200) {
            mView.sendSuccess(model.order_id)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
        }
    }

    private fun onRetrieveDataFailed(message: Throwable) {
        if (message is HttpException) {
            when {
                message.code() == 400 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment) + "400")
                }
                message.code() == 500 -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment) + "500")
                }
                else -> {
                    mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
                }
            }

        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_cod_payment))
        }

    }

    private fun fetchProfile() {
        mSubscription = APIManager.getInstance().getApi()
            .getCustomerProfile(userInfo!!.data.uid)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { }
            .subscribeOn(Schedulers.newThread())
            .subscribe(this::onRetrieveProfileDataSuccess, this::onRetrieveProfileDataFailed)
    }

    private fun onRetrieveProfileDataSuccess(model: CustomerProfileResponse) {
        val add = Functions.getDefaultAddress(model)

        if (add != null) {
            this.addressData = add

            submitOrder(voucher, voucherId, model)
        }
    }

    private fun onRetrieveProfileDataFailed(@Suppress("UNUSED_PARAMETER") message: Throwable) {
        //mView.sendProfileFalsed(message.message.toString())
    }

    private fun submitOrder(voucher: String, voucher_uid: String, model: CustomerProfileResponse) {
        // request
        val data: ArrayList<Product> = if (list != null) {
            list!!

        } else {
            Functions.removeProductBySupplierCondition(databaseHandler!!.getLProductList())

        }
        request = mappingCartToRequest(data, voucher, voucher_uid, model)

        if (request != null) {
            mSubscription = APIManager.getInstance().getApi()
                .postResponce(request!!)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { }
                .doOnTerminate { }
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::onRetrieveDataSuccess, this::onRetrieveDataFailed)
        } else {
            mView.sendFalsed(mContext.getString(R.string.error_in_momo_payment))
        }
    }

    private fun mappingCartToRequest(
        products: ArrayList<Product>,
        voucher: String,
        voucher_uid: String,
        model: CustomerProfileResponse
    ): PaymentRequest? {
        val items = ArrayList<PaymentRequest.Item>()

        if (products.size > 0) {
            for (item in products) {
                items.add(PaymentRequest.Item(item.uid, item.order_quantity))
            }
        }

        if (userInfo != null && addressData != null && items.size > 0) {
            var districtCode = ""
            var provinceCode = ""
            var addressType = 0
            var addressDes = ""

            model.address?.let { address ->
                addressType = address.address_type ?: 1
                addressDes = address.address_des ?: ""
                address.district?.let { district ->
                    districtCode = district.uid ?: ""
                }
                address.province?.let { province ->
                    provinceCode = province.uid ?: ""
                }
            }

            return PaymentRequest(
                platform = "fptplaybox",
                payType = "cod",
                cid = userInfo!!.data.uid,
                voucher_code = voucher, voucher_uid = voucher_uid,
                phone = addressData!!.phone_number,
                address = addressData!!.address.address_des,
                province = addressData!!.address.customer_province.uid,
                district = addressData!!.address.customer_district.uid,
                items = items,
                addressType = addressData!!.address.address_type,
                name = addressData!!.customer_name,
                requestDeliveryTime = requestType,
                note = "",
                create_address_type = addressType,
                created_address_des = addressDes,
                created_customer_name = model.name ?: "",
                created_district = districtCode,
                created_province = provinceCode,
                created_phone_number = model.phone ?: ""
            )
        }
        return null
    }
}
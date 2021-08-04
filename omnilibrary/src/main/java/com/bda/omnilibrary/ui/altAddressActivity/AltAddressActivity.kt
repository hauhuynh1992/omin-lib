package com.bda.omnilibrary.ui.altAddressActivity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.leanback.widget.VerticalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.AltAddressAdapter
import com.bda.omnilibrary.dialog.DeleteAltAddressDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_alt_addressctivity.*
import kotlinx.android.synthetic.main.item_activity_header.view.*

class AltAddressActivity : BaseActivity(), AltAddressContract.View {

    private lateinit var persenter: AltAddressContract.Presenter
    private lateinit var pref: PreferencesHelper
    private var mode = 0

    private lateinit var profileAdatper: AltAddressAdapter
    lateinit var rvList: VerticalGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alt_addressctivity)
        initChildHeader(image_header)
        initial()
    }

    private fun initial() {
        if(getCheckCustomerResponse()!=null) {
            persenter = AltAddressPresenter(this, this)
            rvList = findViewById(R.id.vg)
            pref = PreferencesHelper(this)
            persenter.fetchDeliveryAddress(getCheckCustomerResponse()!!.data.uid)
            mode = intent.getIntExtra("Mode", 0)
        }else{
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        persenter.disposeAPI()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun sendSuccess(profile: CustomerProfileResponse) {
        val delivery = ArrayList<ContactInfo>()
        profile.alt_info?.let {
            delivery.addAll(it)
            delivery.sortByDescending { it.is_default_address }
        }
        profileAdatper = AltAddressAdapter(this, profile, delivery,
            onChooseDelivery = { uid, alt ->
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                dataObject.addressId = alt.address.uid
                dataObject.recipientName = alt.customer_name
                dataObject.recipientPhone = alt.phone_number
                dataObject.recipientAddress = alt.address.address_des
                dataObject.recipientDistrict = alt.address.customer_district.name
                dataObject.recipientCity = alt.address.customer_province.name
                if (alt.address.address_type == 1) {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
                } else {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
                }
                dataObject.isDefault = alt.is_default_address.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_SELECT_RECIPIENT_v2,
                    data
                )
                persenter.postDefaultAddress(
                    DefaultAddressRequest(
                        customer_id = uid,
                        address_id = alt.uid
                    )
                )
            },
            onAddDelivery = {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_CREATE_RECIPIENT_BUTTON_v2,
                    data
                )
                gotoUserInfomation(2)
            },
            onDeleteDelivery = { uid, alt ->
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                dataObject.addressId = alt.address.uid
                dataObject.recipientName = alt.customer_name
                dataObject.recipientPhone = alt.phone_number
                dataObject.recipientAddress = alt.address.address_des
                dataObject.recipientDistrict = alt.address.customer_district.name
                dataObject.recipientCity = alt.address.customer_province.name
                if (alt.address.address_type == 1) {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
                } else {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
                }
                dataObject.isDefault = alt.is_default_address.toString()
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_REMOVE_RECIPIENT_BUTTON_v2,
                    data
                )
                DeleteAltAddressDialog(this, getString(R.string.text_ban_muon_xoa_dia_chi_nay)/*"Bạn có muốn xoá địa chỉ này?"*/, {
                    if (Functions.checkInternet(this)) {
                        logTrackingVersion2(
                            QuickstartPreferences.CLICK_REMOVE_RECIPIENT_CONFIRM_v2,
                            Gson().toJson(dataObject).toString()
                        )
                        persenter.removeAddress(uid, alt.uid)
                    } else {
                        Functions.showMessage(this, getString(R.string.no_internet))
                    }
                }, {
                    logTrackingVersion2(
                        QuickstartPreferences.CLICK_REMOVE_RECIPIENT_CANCEL_v2,
                        data
                    )
                })
            }
        )
        rvList.adapter = profileAdatper


        Handler().postDelayed({

            @Suppress("SENSELESS_COMPARISON")
            if (::profileAdatper.isInitialized && profileAdatper != null
                && profileAdatper.exportBnAddView != null
            ) profileAdatper.exportBnAddView!!.requestFocus()

        }, 100)
    }

    override fun sendRemoveAltAddressSuccess(altId: String) {
        profileAdatper.removeAltId(altId)
    }

    override fun sendFalsed(message: Int) {
        Functions.showMessage(this, resources.getString(message))
    }

    override fun sendDafaultAddressSuccess(response: DefaultAddressResponse) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun sendDafaultAddressFalsed(message: String) {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                @Suppress("SENSELESS_COMPARISON")
                if (::profileAdatper.isInitialized && profileAdatper != null
                    && profileAdatper.exportBnAddView != null && profileAdatper.exportBnAddView!!.hasFocus()
                ) {
                    Handler().postDelayed({
                        image_header.bn_search.requestFocus()
                    }, 0)
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }
}

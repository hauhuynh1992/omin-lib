package com.bda.omnilibrary.ui.accountAcitity.editDeliveryAddressFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.DeleteAltAddressDialog
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.dialog.address.InputAddressDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.profileFragment.ProfileFragment
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_NAME_CODE
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_PHONE_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.fragment_edit_delivery_address.*
import kotlinx.android.synthetic.main.item_activity_header.*
import kotlinx.android.synthetic.main.item_payment_type.*


class EditDeliveryAddressFragment : Fragment(), EditDeliveryAddressContact.View {
    private lateinit var presenter: EditDeliveryAddressPresenter
    private var province: Region? = null
    private var district: Region? = null
    private var isDefaultAddress: Boolean = true
    private var selectAddressType: Int = 1

    private lateinit var uid: String
    private var mAltInfo: ContactInfo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EditDeliveryAddressPresenter(this, requireContext())
        try {
            arguments?.let {
                uid = it.getString("uid").toString()
                mAltInfo = it.getParcelable("contactInfo")
            }
        } catch (e: Exception) {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_delivery_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EditDeliveryAddressFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNNECESSARY_SAFE_CALL")
    private fun initial() {
        Handler().postDelayed({
            edt_name_delivery.requestFocus()
        }, 100)

        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice_name_delivery.visibility = View.VISIBLE

            bn_voice_phone_delivery.visibility = View.VISIBLE
        } else {
            bn_voice_name_delivery.visibility = View.GONE
            bn_voice_phone_delivery.visibility = View.GONE
        }

        edt_name_delivery.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity).layout_btn_account.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity).bn_cart.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone_delivery.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity).layout_btn_account.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_name_delivery.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        rl_update.setOnClickListener {
            if (mAltInfo != null) {
                val altInfo = ContactInfo()
                altInfo.phone_number = edt_phone_delivery.text.toString()
                altInfo.customer_name = edt_name_delivery.text.toString()
                altInfo.is_default_address = isDefaultAddress
                altInfo.uid = mAltInfo!!.uid
                altInfo.address = Address(
                    uid = mAltInfo!!.address.uid,
                    _address_des = edt_delivery_address.text.toString(),
                    _address_type = selectAddressType,
                )
                district?.let {
                    altInfo.address.customer_district = it
                }
                province?.let {
                    altInfo.address.customer_province = it
                }
                val request = UpdateCustomerAltRequest(
                    uid = uid,
                    alt_info = altInfo
                )

                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                dataObject.recipientName = altInfo?.customer_name
                dataObject.recipientPhone = altInfo?.phone_number
                dataObject.recipientAddress = altInfo?.address?.address_des
                dataObject.recipientDistrict = altInfo?.address?.customer_district?.name
                dataObject.recipientCity = altInfo?.address?.customer_province?.name
                if (altInfo?.address.address_type == 1) {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
                } else {
                    dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
                }
                dataObject.isDefault = altInfo?.is_default_address.toString()
                val data = Gson().toJson(dataObject).toString()
                (context as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SUBMIT_RECIPIENT_ADDRESS_v2,
                    data
                )

                request.validate(activity!!).also { result ->
                    if (result.first) {
                        presenter.updateCustomAltInfo(request)
                    } else {
                        Functions.showMessage(activity!!, result.second.toString())
                    }
                }
            } else {
                val altInfo = ContactInfo()
                altInfo.is_default_address = isDefaultAddress
                altInfo.customer_name = edt_name_delivery.text.toString()
                altInfo.phone_number = edt_phone_delivery.text.toString()
                altInfo.address = Address(
                    _address_des = edt_delivery_address.text.toString(),
                    _address_type = selectAddressType
                )
                district?.let {
                    altInfo.address.customer_district = it
                }
                province?.let {
                    altInfo.address.customer_province = it
                }
                val request = UpdateCustomerAltRequest(
                    uid = uid,
                    alt_info = altInfo
                )
                request.validate(activity!!).also { result ->
                    if (result.first) {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                        dataObject.addressId = altInfo?.address?.uid
                        dataObject.recipientName = altInfo?.customer_name
                        dataObject.recipientPhone = altInfo?.phone_number
                        dataObject.recipientAddress = altInfo?.address?.address_des
                        dataObject.recipientDistrict = altInfo?.address?.customer_district?.name
                        dataObject.recipientCity = altInfo?.address?.customer_province?.name
                        if (altInfo?.address.address_type == 1) {
                            dataObject.recipientAddressType =
                                QuickstartPreferences.ADDRESS_TYPE_HOME
                        } else {
                            dataObject.recipientAddressType =
                                QuickstartPreferences.ADDRESS_TYPE_COMPANY
                        }
                        dataObject.isDefault = altInfo?.is_default_address.toString()
                        val data = Gson().toJson(dataObject).toString()
                        (context as AccountActivity)?.logTrackingVersion2(
                            QuickstartPreferences.CLICK_SUBMIT_NEW_RECIPIENT_v2,
                            data
                        )
                        presenter.addCustomAltInfo(request)
                    } else {
                        Functions.showMessage(activity!!, result.second.toString())
                    }
                }
            }
        }


        rl_update.setOnFocusChangeListener { _, hasFocus ->
            tv_update.isSelected = hasFocus
        }

        rl_delete.setOnFocusChangeListener { _, hasFocus ->
            tv_delete_delivery.isSelected = hasFocus
        }

        rl_delete.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
            dataObject.addressId = mAltInfo?.address?.uid
            dataObject.recipientName = mAltInfo?.customer_name
            dataObject.recipientPhone = mAltInfo?.phone_number
            dataObject.recipientAddress = mAltInfo?.address?.address_des
            dataObject.recipientDistrict = mAltInfo?.address?.customer_district?.name
            dataObject.recipientCity = mAltInfo?.address?.customer_province?.name
            if (mAltInfo?.address?.address_type == 1) {
                dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
            } else {
                dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
            }
            dataObject.isDefault = mAltInfo?.is_default_address.toString()
            val data = Gson().toJson(dataObject).toString()
            (context as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_REMOVE_RECIPIENT_BUTTON_v2,
                data
            )
            DeleteAltAddressDialog(requireActivity(),
                getString(R.string.text_ban_muon_xoa_dia_chi_nay)/*"Bạn có muốn xoá địa chỉ này?"*/,
                {
                    if (Functions.checkInternet(requireActivity())) {
                        (context as AccountActivity)?.logTrackingVersion2(
                            QuickstartPreferences.CLICK_REMOVE_RECIPIENT_CONFIRM_v2,
                            data
                        )
                        presenter.removeAddress(uid, mAltInfo!!.uid)
                    } else {
                        Functions.showMessage(requireActivity(), getString(R.string.no_internet))
                    }
                },
                {
                    (context as AccountActivity)?.logTrackingVersion2(
                        QuickstartPreferences.CLICK_REMOVE_RECIPIENT_CANCEL_v2,
                        data
                    )
                })
        }
        if (mAltInfo != null) {
            title.text =
                getString(R.string.text_chinh_sua_dia_chi_giao_hang)//"CHỈNH SỬA ĐỊA CHỈ GIAO HÀNG"
            edt_name_delivery.setText(mAltInfo!!.customer_name)
            edt_phone_delivery.setText(mAltInfo!!.phone_number)
            isDefaultAddress = mAltInfo!!.is_default_address
            mAltInfo!!.address?.let {
                province = it.customer_province
                district = it.customer_district
                edt_delivery_address.text = it.address_des
                if (it.address_type == 1) {
                    rab_house.isChecked = true
                    rab_company.isChecked = false
                } else {
                    rab_house.isChecked = false
                    rab_company.isChecked = true
                }
                cb_default.isChecked = mAltInfo!!.is_default_address
                tv_update.text = getString(R.string.text_cap_nhat)//"Cập nhật"
            }
            rl_delete.visibility = View.VISIBLE


        } else {
            title.text = getString(R.string.text_them_dia_chi_gia_hang)//"THÊM ĐỊA CHỈ GIAO HÀNG"
            rab_house.isChecked = false
            rab_company.isChecked = false
            cb_default.isChecked = true
            tv_update.text = getString(R.string.text_them)//"Thêm"
            rl_delete.visibility = View.GONE

            rl_default.isEnabled = false
            rl_default.isFocusable = false
            rl_default.isFocusableInTouchMode = false
            rl_default.alpha = 0.3f

            cb_default.isFocusable = false
            cb_default.isFocusableInTouchMode = false

            rl_house.nextFocusDownId = rl_update.id
            rl_company.nextFocusDownId = rl_update.id

            rl_update.nextFocusUpId = rl_house.id
        }

        rl_company.setOnClickListener {
            selectAddressType = 2
            rab_house.isChecked = false
            rab_company.isChecked = true

            val dataObject = LogDataRequest()
            dataObject.value = getString(R.string.text_co_quan_cong_ty)//"Cơ quan / Công ty"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_RECIPIENT_ADDRESS_TYPE_v2,
                data
            )
        }
        rl_company.setOnFocusChangeListener { _, hasFocus ->
            rab_company.isSelected = hasFocus
        }
        rl_house.setOnClickListener {
            selectAddressType = 1
            rab_house.isChecked = true
            rab_company.isChecked = false

            val dataObject = LogDataRequest()
            dataObject.value = getString(R.string.text_nha_rieng_chung_cu)//"Nhà riêng / Chung cư"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_RECIPIENT_ADDRESS_TYPE_v2,
                data
            )
        }
        rl_house.setOnFocusChangeListener { _, hasFocus ->
            rab_house.isSelected = hasFocus
        }
        rl_default.setOnClickListener {
            isDefaultAddress = !cb_default.isChecked
            cb_default.isChecked = !cb_default.isChecked

            val dataObject = LogDataRequest()
            if (isDefaultAddress) {
                dataObject.value = "Default"
            } else {
                dataObject.value = "Non Default"
            }
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_DEFAULT_ADDRESS_OPTION_v2,
                data
            )
        }
        rl_default.setOnFocusChangeListener { _, hasFocus ->
            cb_default.isSelected = hasFocus
        }
        edt_name_delivery.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            dataObject.inputName = "Name"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_NAME_TYPE,
                edt_name_delivery.text.toString(),
                edt_name_delivery, edt_phone_delivery
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_delivery_address.setOnClickListener {
            (activity as AccountActivity).requestPermission()
        }

        edt_phone_delivery.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            dataObject.inputName = "Phone"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone_delivery.text.toString(),
                edt_phone_delivery, edt_delivery_address
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_name_delivery.setOnFocusChangeListener { view, b ->
            if (b) {
                edt_name_delivery.setSelection(edt_name_delivery.text.length)
                view_voice_remove_name.visibility = View.VISIBLE
                view_voice_remove_phone.visibility = View.GONE
            } else {
                if (!bn_voice_name_delivery.hasFocus() && !bn_delete_name_delivery.hasFocus()) {
                    view_voice_remove_name.visibility = View.GONE
                }
            }
        }

        edt_phone_delivery.setOnFocusChangeListener { view, b ->
            if (b) {
                edt_phone_delivery.setSelection(edt_phone_delivery.text.length)
                view_voice_remove_phone.visibility = View.VISIBLE
                view_voice_remove_name.visibility = View.GONE
            } else {
                if (!bn_voice_phone_delivery.hasFocus() && !bn_delete_phone_delivery.hasFocus()) {
                    view_voice_remove_phone.visibility = View.GONE
                }
            }
        }

        bn_voice_name_delivery.setOnClickListener {
            (activity as AccountActivity).apply {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                dataObject.inputType = "Voice"
                dataObject.inputName = "Name"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                    data
                )
                gotoDiscoveryVoice(REQUEST_VOICE_NAME_CODE)
            }
        }

        bn_delete_name_delivery.setOnClickListener {
            edt_name_delivery.setText("")

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                data
            )
        }

        bn_voice_phone_delivery.setOnClickListener {
            (activity as AccountActivity).apply {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                dataObject.inputType = "Voice"
                dataObject.inputName = "Phone"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                    data
                )
                gotoDiscoveryVoice(REQUEST_VOICE_PHONE_CODE)
            }

        }

        bn_delete_phone_delivery.setOnClickListener {
            edt_phone_delivery.setText("")

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                data
            )
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
        if (result != null && result.isNotBlank()) {
            when (requestCode) {
                REQUEST_VOICE_PHONE_CODE -> {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                    dataObject.inputName = "Phone"
                    dataObject.inputType = "Voice"
                    dataObject.result = result.trim().replace(" ", "")
                    (activity as AccountActivity).logTrackingVersion2(
                        QuickstartPreferences.VOICE_RESULT_v2,
                        Gson().toJson(dataObject).toString()
                    )
                    edt_phone_delivery.setText(result.trim().replace(" ", ""))
                }
                REQUEST_VOICE_NAME_CODE -> {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.EDIT_RECIPIENT.name
                    dataObject.inputName = "Name"
                    dataObject.inputType = "Voice"
                    dataObject.result = result.trim().toUpperCase()
                    (activity as AccountActivity).logTrackingVersion2(
                        QuickstartPreferences.VOICE_RESULT_v2,
                        Gson().toJson(dataObject).toString()
                    )
                    edt_name_delivery.setText(result.trim().toUpperCase())
                }
            }
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    fun showInputAddressDialog() {
        val dialog = InputAddressDialog(
            mAltInfo?.address?.customer_district?.uid,
            mAltInfo?.address?.customer_province?.uid
        )
        dialog.show(childFragmentManager, dialog.tag)
        dialog.setSelectProvinceListener(object : InputAddressDialog.SelectProvinceListener {


            override fun onCompleteAddress(
                address_des: String,
                provinceCode: Region,
                districtCode: Region,
            ) {
                edt_delivery_address.text = address_des
                province = provinceCode
                district = districtCode
            }
        })
    }

    override fun sendSuccess() {
        activity?.let {
            (it as AccountActivity).loadFragment(
                ProfileFragment.newInstance(),
                R.id.frameLayout,
                false
            )
            it.reloadProfile()
        }
    }

    override fun sendFalsed(message: String) {
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }
}

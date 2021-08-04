package com.bda.omnilibrary.ui.accountAcitity.editProfileFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.dialog.address.InputAddressDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.*
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.profileFragment.ProfileFragment
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_NAME_CODE
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_PHONE_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.DateUtils
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.item_activity_header.*

class EditProfileFragment : Fragment(), EditProfileContact.View {
    private lateinit var presenter: EditProfilePresenter
    private var selectGender: Int = 0
    private var selectAddressType: Int = 1
    private var province: Region? = null
    private var district: Region? = null

    private lateinit var profile: CheckCustomerResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = EditProfilePresenter(this, requireContext())
        arguments?.let {
            profile = it.getParcelable("profile")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            EditProfileFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    @SuppressLint("SetTextI18n")
    private fun initial() {
        Handler().postDelayed({
            edt_name_profile.requestFocus()
        }, 100)

        if (arrayListOf("box2019", "box2020","omnishopeu","box2021").contains(Config.platform)) {
            bn_voice_name.visibility = View.VISIBLE
            bn_voice_phone.visibility = View.VISIBLE
        } else {
            bn_voice_name.visibility = View.GONE
            bn_voice_phone.visibility = View.GONE
        }

        edt_name_profile.setText(profile.data.name)
        profile.data.phone?.let {
            edt_phone_profile.setText(it)
        }
        profile.data.phone?.let {
            edt_phone_profile.setText(it)
        }
        edt_email.setText(profile.data.email)
        profile.data.address?.let { address ->
            address.address_des?.let {
                edt_address_profile.text = it
            }
        }

        edt_name_profile.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity)?.layout_btn_account.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity)?.bn_cart.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone_profile.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity)?.layout_btn_account.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_name_profile.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_email.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AccountActivity)?.layout_btn_account.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_phone_profile.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        when (profile.data.gender) {
            1 -> {
                tv_gender.text = getString(R.string.text_nu)//"Nữ"
                selectGender = 1
                cb_female.isChecked = true
            }
            0 -> {
                tv_gender.text = getString(R.string.text_nam)//"Nam"
                selectGender = 0
                cb_male.isChecked = true
            }
            else -> {
                tv_gender.text = getString(R.string.text_khac)//"Khác"
                selectGender = 2
                cb_other.isChecked = true
            }
        }

        profile.data.address?.let { address ->
            when (address.address_type) {
                1 -> {
                    rab_house.isChecked = true
                    rab_company.isChecked = false
                    selectAddressType = 1
                }
                else -> {
                    rab_house.isChecked = false
                    rab_company.isChecked = true
                    selectAddressType = 2
                }
            }
        }

        profile.data.dateOfBirth?.let {
            if(!it.isNullOrBlank()){
                tv_birth_day.text = DateUtils.longTimeString(it.toLong(), "dd/MM/yyyy")
            }
        }

        bn_update_profile.setOnClickListener {
            val date = DateUtils.convertStringToDate(tv_birth_day.text.toString(), "dd/MM/yyyy")
            val address = Address()
            address.address_des = edt_address_profile.text.toString()
            address.address_type = selectAddressType
            province?.let {
                address.customer_province = it
            }
            district?.let {
                address.customer_district = it
            }

            val request = UpdateCustomerProfileRequest(
                uid = profile.data.uid.toString(),
                phone = edt_phone_profile.text.toString(),
                name = edt_name_profile.text.toString(),
                email = edt_email.text.toString(),
                gender = selectGender,
                address = address
            )
            if (date != null) {
                request.dateOfBirth = date.time
            }

            presenter.updateCustomProfile(request)
            (activity as AccountActivity)?.apply {
                val dataObject = LogDataRequest()
                dataObject.name = edt_name_profile.text.toString()
                dataObject.phone = edt_phone_profile.text.toString()
                dataObject.email = edt_email.text.toString()
                when (selectGender) {
                    1 -> {
                        dataObject.sex = getString(R.string.text_nu)//"Nữ"
                    }
                    0 -> {
                        dataObject.sex = getString(R.string.text_nam)//"Nam"
                    }
                    else -> {
                        dataObject.sex = getString(R.string.text_khac)//"Khác"
                    }
                }

                dataObject.address = edt_address_profile.text.toString()
                district?.let {
                    dataObject.district = it.name
                }
                province?.let {
                    dataObject.city = it.name
                }
                when (selectAddressType) {
                    1 -> {
                        dataObject.addressType = getString(R.string.text_nha_rieng_chung_cu)//"Nhà riêng / Chung cư"
                    }
                    2 -> {
                        dataObject.addressType = getString(R.string.text_co_quan_cong_ty)//"Cơ quan / Công ty"
                    }
                }
                if (date != null) {
                    dataObject.birthday = date.time.toString()
                }
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_SUBMIT_ADDRESS_BUTTON_v2,
                    data
                )
            }
        }

        edt_name_profile.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )

            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_NAME_TYPE,
                edt_name_profile.text.toString(),
                edt_name_profile, edt_phone_profile
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_phone_profile.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone_profile.text.toString(),
                edt_phone_profile, edt_email
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_email.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Email"
            dataObject.inputType = "KEYBOARD_EMAIL_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_email.text.toString(),
                edt_email, tv_gender
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }
        edt_date.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Date"
            dataObject.inputType = "KEYBOARD_NUMBER_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_date.text.toString(),
                edt_date, edt_month
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_month.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Month"
            dataObject.inputType = "KEYBOARD_NUMBER_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_month.text.toString(),
                edt_month, edt_year
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }
        edt_year.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Year"
            dataObject.inputType = "KEYBOARD_NUMBER_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_year.text.toString(),
                edt_year
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        tv_gender.setOnClickListener {
            toggleGenderView(false)
            when (selectGender) {
                0 -> layout_male.requestFocus()
                1 -> layout_female.requestFocus()
                else -> layout_orther.requestFocus()
            }
            (activity as AccountActivity)?.let {
                val dataObject = LogDataRequest()
                val data = Gson().toJson(dataObject).toString()
                it.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEX_FIELD_v2,
                    data
                )
            }

        }

        layout_male.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!layout_orther.hasFocus() && !layout_female.hasFocus()) {
                    tv_gender.visibility = View.VISIBLE
                    view_select_gender.visibility = View.GONE
                }
            }
        }

        layout_female.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!layout_orther.hasFocus() && !layout_male.hasFocus()) {
                    tv_gender.visibility = View.VISIBLE
                    view_select_gender.visibility = View.GONE
                }
            }
        }

        layout_orther.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!layout_female.hasFocus() && !layout_male.hasFocus()) {
                    tv_gender.visibility = View.VISIBLE
                    view_select_gender.visibility = View.GONE
                }
            }
        }


        tv_birth_day.setOnClickListener {
            tv_birth_day.visibility = View.GONE
            view_update_birth_day.visibility = View.VISIBLE
            edt_date.requestFocus()

            (activity as AccountActivity)?.let {
                val dataObject = LogDataRequest()
                profile.data.dateOfBirth?.let {
                    if(!it.isNullOrBlank()){
                        dataObject.date = DateUtils.longTimeString(it.toLong(), "dd")
                        dataObject.month = DateUtils.longTimeString(it.toLong(), "MM")
                        dataObject.year = DateUtils.longTimeString(it.toLong(), "yyyy")
                    }
                }
                val data = Gson().toJson(dataObject).toString()
                it.logTrackingVersion2(QuickstartPreferences.CLICK_BIRTHDAY_FIELD_v2, data)
            }
        }

        edt_name_profile.setOnFocusChangeListener { _, b ->
            if (b) {
                edt_name_profile.setSelection(edt_name_profile.text.length)
                view_voice_remove_name.visibility = View.VISIBLE
                view_voice_remove_phone.visibility = View.GONE
                view_voice_remove_email.visibility = View.GONE
            } else {
                if (!bn_voice_name.hasFocus() && !bn_delete_name.hasFocus()) {
                    view_voice_remove_name.visibility = View.GONE
                }
            }
        }

        edt_phone_profile.setOnFocusChangeListener { _, b ->
            if (b) {
                edt_phone_profile.setSelection(edt_phone_profile.text.length)
                view_voice_remove_phone.visibility = View.VISIBLE
                view_voice_remove_name.visibility = View.GONE
                view_voice_remove_email.visibility = View.GONE
            } else {
                if (!bn_voice_phone.hasFocus() && !bn_delete_phone.hasFocus()) {
                    view_voice_remove_phone.visibility = View.GONE
                }
            }
        }

        edt_email.setOnFocusChangeListener { _, b ->
            if (b) {
                edt_email.setSelection(edt_email.text.length)
                view_voice_remove_email.visibility = View.VISIBLE
                view_voice_remove_phone.visibility = View.GONE
                view_voice_remove_name.visibility = View.GONE
            } else {
                if (!bn_delete_email.hasFocus()) {
                    view_voice_remove_email.visibility = View.GONE
                }
            }
        }

        edt_date.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!edt_month.hasFocus() && !edt_year.hasFocus() && !tv_birth_day_remove.hasFocus()) {
                    showBirthDay()
                }
            }
        }

        edt_month.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!edt_date.hasFocus() && !edt_year.hasFocus() && !tv_birth_day_remove.hasFocus()) {
                    showBirthDay()
                }
            }
        }

        edt_year.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!edt_month.hasFocus() && !edt_date.hasFocus() && !tv_birth_day_remove.hasFocus()) {
                    showBirthDay()
                }
            }
        }

        tv_birth_day_remove.setOnFocusChangeListener { _, b ->
            if (!b) {
                if (!edt_month.hasFocus() && !edt_date.hasFocus() && !edt_year.hasFocus()) {
                    showBirthDay()
                }
            }
        }

        edt_address_profile.setOnClickListener {
            (activity as AccountActivity)?.apply {
                requestPermission()
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_ADDRESS_FIELD_v2,
                    data
                )
            }
        }


        tv_birth_day_remove.setOnClickListener {
            edt_date.setText("")
            edt_month.setText("")
            edt_year.setText("")

            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "BirthDay"
            dataObject.inputType = "KEYBOARD_NUMBER_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                data
            )
        }

        rl_company.setOnClickListener {
            selectAddressType = 2
            rab_house.isChecked = false
            rab_company.isChecked = true

            (activity as AccountActivity)?.apply {
                val dataObject = LogDataRequest()
                dataObject.value = getString(R.string.text_co_quan_cong_ty)//"Cơ quan / Công ty"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_ADDRESS_TYPE_v2,
                    data
                )
            }
        }
        rl_company.setOnFocusChangeListener { _, hasFocus ->
            rab_company.isSelected = hasFocus
        }

        rl_house_profile.setOnClickListener {
            selectAddressType = 1
            rab_house.isChecked = true
            rab_company.isChecked = false
            (activity as AccountActivity)?.apply {
                val dataObject = LogDataRequest()
                dataObject.value = getString(R.string.text_nha_rieng_chung_cu)//"Nhà riêng / Chung cư"
                val data = Gson().toJson(dataObject).toString()
                logTrackingVersion2(
                    QuickstartPreferences.CLICK_ADDRESS_TYPE_v2,
                    data
                )
            }

        }
        rl_house_profile.setOnFocusChangeListener { _, hasFocus ->
            rab_house.isSelected = hasFocus
        }

        bn_delete_name.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Name"
            dataObject.inputType = "KEYBOARD_NAME_TYPE"
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
            edt_name_profile.setText("")
        }
        bn_delete_phone.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Phone"
            dataObject.inputType = "KEYBOARD_PHONE_TYPE"
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                Gson().toJson(dataObject).toString()
            )
            edt_phone_profile.setText("")
        }
        bn_delete_email.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
            dataObject.inputName = "Email"
            dataObject.inputType = "KEYBOARD_EMAIL_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                data
            )
            edt_email.setText("")
        }
        bn_voice_name.setOnClickListener {
            (activity as AccountActivity)?.apply {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
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
        bn_voice_phone.setOnClickListener {
            (activity as AccountActivity).apply {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
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

        layout_female.setOnClickListener {
            selectGender = 1
            tv_gender.text = getString(R.string.text_nu)//"Nữ"
            cb_female.isChecked = true
            cb_male.isChecked = false
            cb_other.isChecked = false
            toggleGenderView(true)

            (activity as AccountActivity)?.let {
                val dataObject = LogDataRequest()
                dataObject.value = getString(R.string.text_nu)//"Nữ"
                val data = Gson().toJson(dataObject).toString()
                it.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEX_VALUE_v2, data
                )
            }
        }

        layout_male.setOnClickListener {
            selectGender = 0
            tv_gender.text = getString(R.string.text_nam)//"Nam"
            cb_female.isChecked = false
            cb_male.isChecked = true
            cb_other.isChecked = false
            toggleGenderView(true)

            (activity as AccountActivity)?.let {
                val dataObject = LogDataRequest()
                dataObject.value = getString(R.string.text_nam)//"Nam"
                val data = Gson().toJson(dataObject).toString()
                it.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEX_VALUE_v2, data
                )
            }
        }


        layout_orther.setOnClickListener {
            selectGender = 2
            tv_gender.text = getString(R.string.text_khac)//"Khác"
            cb_female.isChecked = false
            cb_male.isChecked = false
            cb_other.isChecked = true
            toggleGenderView(true)

            (activity as AccountActivity)?.let {
                val dataObject = LogDataRequest()
                dataObject.value = getString(R.string.text_khac)//"Khác"
                val data = Gson().toJson(dataObject).toString()
                it.logTrackingVersion2(
                    QuickstartPreferences.CLICK_SEX_VALUE_v2, data
                )
            }
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
        if (result != null && result.isNotBlank()) {
            when (requestCode) {
                REQUEST_VOICE_NAME_CODE -> {
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
                    dataObject.inputName = "Name"
                    dataObject.inputType = "Voice"
                    dataObject.result = result

                    @Suppress("UNNECESSARY_SAFE_CALL")
                    (activity as AccountActivity)?.logTrackingVersion2(
                        QuickstartPreferences.VOICE_RESULT_v2,
                        Gson().toJson(dataObject).toString()
                    )
                    edt_name_profile.setText(result.toUpperCase())
                }
                REQUEST_VOICE_PHONE_CODE -> {
                    if (Functions.checkPhoneNumber(result.trim().replace(" ", ""))) {
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.EDIT_PROFILE.name
                        dataObject.inputName = "Phone"
                        dataObject.inputType = "Voice"
                        dataObject.result = result.trim().replace(" ", "")
                        @Suppress("UNNECESSARY_SAFE_CALL")
                        (activity as AccountActivity)?.logTrackingVersion2(
                            QuickstartPreferences.VOICE_RESULT_v2,
                            Gson().toJson(dataObject).toString()
                        )
                        edt_phone_profile.setText(result.trim().replace(" ", ""))

                    } else {
                        Functions.showMessage(
                            requireActivity(),
                            getString(R.string.text_error_phone)
                        )
                    }
                }
            }
        }

    }

    @Suppress("UselessCallOnNotNull")
    @SuppressLint("SetTextI18n")
    fun showBirthDay() {
        val dd = edt_date.text.toString()
        val mm = edt_month.text.toString()
        val yyyy = edt_year.text.toString()
        if (!dd.isNullOrBlank() &&
            !mm.isNullOrBlank() &&
            !yyyy.isNullOrBlank()
        ) {
            tv_birth_day.text = edt_date.text.toString() +
                    "/" +
                    edt_month.text.toString() +
                    "/" +
                    edt_year.text.toString()
        }
        tv_birth_day.visibility = View.VISIBLE
        view_update_birth_day.visibility = View.GONE
    }

    fun showInputAddressDialog() {
        var districtId = ""
        var provinceId = ""

        profile.data.address?.let { address ->
            address.customer_district?.let { mDistrict ->
                districtId = mDistrict.uid.toString()

            }
            address.customer_province?.let { mProvince ->
                provinceId = mProvince.uid.toString()
            }
        }

        val dialog =
            InputAddressDialog(districtId, provinceId)
        dialog.show(childFragmentManager, dialog.tag)
        dialog.setSelectProvinceListener(object : InputAddressDialog.SelectProvinceListener {
            @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
            override fun onCompleteAddress(
                address_des: String,
                provinceCode: Region,
                districtCode: Region
            ) {
                edt_address_profile.text = address_des
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAPI()
    }

    fun toggleGenderView(isShow: Boolean) {
        if (isShow) {
            tv_gender.visibility = View.VISIBLE
            view_select_gender.visibility = View.GONE
            tv_gender.requestFocus()
        } else {
            tv_gender.visibility = View.GONE
            view_select_gender.visibility = View.VISIBLE
        }
    }
}

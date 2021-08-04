package com.bda.omnilibrary.dialog.address

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.leanback.widget.HorizontalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.SelectProvinceAdapter
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.ProvinceDistrictModel
import com.bda.omnilibrary.model.Region
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_ADDRESS_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_select_province.*

class InputAddressDialog(currentDistrict: String? = null, currentProvince: String? = null) :
    DialogFragment(),
    LocationFirstContact.View {
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var districtAdapter: SelectProvinceAdapter
    private lateinit var provinceAdapter: SelectProvinceAdapter
    private lateinit var locationFirstPresenter: LocationFirstContact.Presenter
    private lateinit var mListener: SelectProvinceListener
    private lateinit var rvDistrict: HorizontalGridView
    private lateinit var rvProvince: HorizontalGridView
    private var mCurrentDitrict = currentDistrict
    private var mCurrentProvince = currentProvince
    private var province: Region? = null
    private var district: Region? = null
    private var address: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_select_province, container).apply {
            this@InputAddressDialog.activity?.let {
                districtAdapter =
                    SelectProvinceAdapter(
                        it,
                        arrayListOf(),
                        mCurrentDitrict
                    ) { mDistrictId, posistion ->
                        district = mDistrictId

                        var dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                        dataObject.value = district?.name
                        val data = Gson().toJson(dataObject).toString()
                        (activity as AccountActivity)?.logTrackingVersion2(
                            QuickstartPreferences.SELECT_DISTRICT_VALUE_v2,
                            data
                        )
                        bn_next_district.requestFocus()
                    }
                provinceAdapter =
                    SelectProvinceAdapter(
                        it,
                        arrayListOf(),
                        mCurrentProvince
                    ) { mProvinceId, posistion ->
                        province = mProvinceId
                        var dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                        dataObject.value = province?.name
                        val data = Gson().toJson(dataObject).toString()
                        (activity as AccountActivity)?.logTrackingVersion2(
                            QuickstartPreferences.SELECT_CITY_VALUE_v2,
                            data
                        )
                        bn_next_province.requestFocus()
                    }
                locationFirstPresenter = LocationFirstPresenter(this@InputAddressDialog, it)

                rvDistrict = findViewById<HorizontalGridView>(R.id.rv_district).apply {
                    setNumRows(5)
                    adapter = districtAdapter
                }

                rvProvince = findViewById<HorizontalGridView>(R.id.rv_province).apply {
                    /*layoutManager =
                            GridLayoutManager(context, 5, LinearLayoutManager.HORIZONTAL, false)*/
                    setNumRows(5)
                    adapter = provinceAdapter
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        preferencesHelper = PreferencesHelper(activity!!)
        locationFirstPresenter.loadProvince()
        locationFirstPresenter.loadLocation()

        if (arrayListOf("box2019", "box2020", "omnishopeu","box2021").contains(Config.platform)) {
            bn_voice_name.visibility = View.VISIBLE
        } else {
            bn_voice_name.visibility = View.GONE
        }
        bn_back_province.setOnClickListener {
            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.milestone =
                getString(R.string.text_chon_thanh_pho_tinh)//"Chọn Thành Phố/Tỉnh"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_GO_BACK_BUTTON_v2,
                data
            )
            dismiss()
        }

        bn_back_district.setOnClickListener {
            layout_province.visibility = View.VISIBLE
            layout_district.visibility = View.GONE
            layout_address.visibility = View.GONE

            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.milestone = getString(R.string.text_chon_quan_huyen)//"Chọn Quận/Huyện"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_GO_BACK_BUTTON_v2,
                data
            )
        }

        bn_back_address.setOnClickListener {
            layout_province.visibility = View.GONE
            layout_district.visibility = View.VISIBLE
            layout_address.visibility = View.GONE

            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.milestone = getString(R.string.text_dia_chi)//"Địa chỉ"
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_GO_BACK_BUTTON_v2,
                data
            )
        }

        bn_next_province.setOnClickListener {
            if (province == null) {
                Functions.showMessage(
                    activity!!,
                    getString(R.string.text_ban_chua_chon_tinh_thanh_pho)/*"Bạn chưa chọn Tỉnh/Thành phố"*/
                )
            } else {
                layout_province.visibility = View.GONE
                layout_district.visibility = View.VISIBLE
                layout_address.visibility = View.GONE
                locationFirstPresenter.loadDistrict(province!!.uid)

                var dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                dataObject.milestone =
                    getString(R.string.text_chon_thanh_pho_tinh)//"Chọn Thành Phố/Tỉnh"
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_GO_NEXT_BUTTON_v2,
                    data
                )
            }
        }

        bn_next_district.setOnClickListener {
            if (district == null) {
                Functions.showMessage(
                    activity!!,
                    getString(R.string.text_ban_chua_chon_quan_huyen)/*"Bạn chưa chọn Quận/Huyện"*/
                )
            } else {
                layout_province.visibility = View.GONE
                layout_district.visibility = View.GONE
                layout_address.visibility = View.VISIBLE
                /*ic_status.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity!!,
                        R.mipmap.ic_select_address.takeIf { LibConfig.APP_ORIGIN != Config.PARTNER.OMNISHOPEU.toString() }?:R.mipmap.ic_select_address_en
                    )
                )*/
                step.setImageResource(R.mipmap.milestone_step_3)
                val c = ContextCompat.getColor(context!!, R.color.color_41AE96)
                step_2.setTextColor(c)
                step_3.setTextColor(c)

                title.text =
                    getString(R.string.text_nhap_dia_chi_so_nha_ten_duong_phuong)//"NHẬP ĐỊA CHỈ: SỐ NHÀ, TÊN ĐƯỜNG, PHƯỜNG"
                edt_address.setText(address)
                edt_address.setSelection(edt_address.text.length)
                locationFirstPresenter.loadLocation()

                var dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                dataObject.milestone = getString(R.string.text_chon_quan_huyen)//"Chọn Quận/Huyện"
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_GO_NEXT_BUTTON_v2,
                    data
                )
            }
        }
        bn_complete.setOnClickListener {
            if (province != null && district != null && !edt_address.text.toString()
                    .isNullOrEmpty()
            ) {
                var dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                dataObject.milestone = getString(R.string.text_dia_chi)//"Địa chỉ"
                dataObject.recipientAddress = edt_address.text.toString().trim()
                dataObject.recipientCity = province?.name
                dataObject.recipientDistrict = district?.name
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_COMPLETE_BUTTON_v2,
                    data
                )
                mListener.onCompleteAddress(
                    edt_address.text.toString(),
                    province!!,
                    district!!
                )
                dismiss()
            } else {
                Functions.showMessage(
                    activity!!,
                    getString(R.string.text_ban_chua_nhap_dia_chi)/*"Bạn chưa nhập địa chỉ"*/
                )
            }
        }

        bn_complete.setOnFocusChangeListener { view, b ->
            tv_bn_complete.isSelected = b
        }

        bn_back_address.setOnFocusChangeListener { view, b ->
            tv_back_address.isSelected = b
        }

        bn_next_district.setOnFocusChangeListener { view, b ->
            tv_next_district.isSelected = b
        }

        bn_back_district.setOnFocusChangeListener { view, b ->
            tv_back_district.isSelected = b
        }

        bn_next_province.setOnFocusChangeListener { view, b ->
            tv_next_province.isSelected = b
        }

        bn_back_province.setOnFocusChangeListener { view, b ->
            tv_back_province.isSelected = b
        }

        bn_voice_name.setOnClickListener {
            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.inputType = "Voice"
            dataObject.inputName = "Address"
            val data = Gson().toJson(dataObject).toString()
            (activity as BaseActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                data
            )
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(requireActivity(), DiscoveryVoiceActivity::class.java)
                intent.putExtra(
                    DiscoveryVoiceActivity.REQUEST_VOICE_RESULT,
                    REQUEST_VOICE_ADDRESS_CODE
                )
                startActivityForResult(
                    intent, REQUEST_VOICE_ADDRESS_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    ),
                    0
                )
            }

        }

        bn_delete_name.setOnClickListener {
            edt_address.setText("")
            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.inputName = "Address"
            dataObject.inputType = "KEYBOARD_ADDRESS_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as BaseActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                data
            )
        }

        edt_address.setOnClickListener {
            var dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
            dataObject.inputName = "Address"
            dataObject.inputType = "KEYBOARD_ADDRESS_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as BaseActivity)?.logTrackingVersion2(
                QuickstartPreferences.CLICK_INPUT_FIELD_v2,
                data
            )
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_address.text.toString(),
                edt_address
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }
        edt_address.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                edt_address.setSelection(edt_address.text.length)
            }
        }

        edt_address.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.action == KeyEvent.ACTION_DOWN) {

                return@OnKeyListener true
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.action == KeyEvent.ACTION_DOWN) {
                if (bn_voice_name.visibility == View.VISIBLE) {
                    bn_voice_name.requestFocus()
                } else {
                    bn_delete_name.requestFocus()
                }
                return@OnKeyListener true
            }
            false
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VOICE_ADDRESS_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null && result.isNotBlank()) {
                    var dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.EDIT_ADDRESS_DIALOG.name
                    dataObject.inputName = "Address"
                    dataObject.inputType = "Voice"
                    dataObject.result = result?.get(0).toString().trim().toUpperCase()
                    val data = Gson().toJson(dataObject).toString()
                    (activity as BaseActivity)?.logTrackingVersion2(
                        QuickstartPreferences.VOICE_RESULT_v2,
                        data
                    )
                    edt_address.setText(result.trim())
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            // Set gravity of dialog
            dialog.setCanceledOnTouchOutside(true)
            val window = dialog.window
            val wlp = window!!.attributes
            wlp.gravity = Gravity.CENTER
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.attributes = wlp
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            val lp = window.attributes
            lp.dimAmount = 0f
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            dialog.window!!.attributes = lp
        }
    }

    override fun sendUIFail(erroMessage: String) {
        Toast.makeText(activity!!, erroMessage, Toast.LENGTH_SHORT).show()
    }

    override fun sendUIProvinceSussess(model: ProvinceDistrictModel) {
        if (model.data.size > 0) {
            layout_province.visibility = View.VISIBLE
            layout_district.visibility = View.GONE
            layout_address.visibility = View.GONE

            title.text =
                getString(R.string.text_vui_long_chon_tp_tinh)//"Vui lòng CHỌN THÀNH PHỐ / TỈNH"

            provinceAdapter.setData(model.data)
        }
    }

    override fun sendUIDistrictSussess(model: ProvinceDistrictModel) {
        if (model.data.size > 0) {
            layout_province.visibility = View.GONE
            layout_address.visibility = View.GONE
            layout_district.visibility = View.VISIBLE

            step.setImageResource(R.mipmap.milestone_step_2)
            val c = ContextCompat.getColor(context!!, R.color.color_41AE96)
            step_2.setTextColor(c)

            title.text =
                getString(R.string.text_vui_long_chon_quan_huyen)//"Vui lòng CHỌN QUẬN / HUYỆN"
            districtAdapter.setData(model.data)
        }
    }

    override fun sendUILocationSuccess(mAddress: String) {
        address = mAddress
    }

    override fun requestPermission() {
    }


    interface SelectProvinceListener {
        fun onCompleteAddress(address_des: String, provinceUid: Region, districtUid: Region)
    }

    fun setSelectProvinceListener(listener: SelectProvinceListener) {
        mListener = listener
    }
}
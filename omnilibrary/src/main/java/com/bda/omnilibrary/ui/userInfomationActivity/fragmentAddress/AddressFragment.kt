package com.bda.omnilibrary.ui.userInfomationActivity.fragmentAddress


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.userInfomationActivity.UserInformationActivity
import com.bda.omnilibrary.ui.voiceActivity.DiscoveryVoiceActivity.Companion.REQUEST_VOICE_ADDRESS_CODE
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_address.bn_next
import kotlinx.android.synthetic.main.fragment_address.ic_status
import kotlinx.android.synthetic.main.fragment_address.image_header
import kotlinx.android.synthetic.main.fragment_address.rl_bn_next
import kotlinx.android.synthetic.main.fragment_district.*
import kotlinx.android.synthetic.main.fragment_province.*
import kotlinx.android.synthetic.main.milestone_3_step.view.*
import kotlinx.android.synthetic.main.milestone_3_step.view.step
import kotlinx.android.synthetic.main.milestone_4_step.view.*

private val ARG_NEW_USER = "isNewUser"

class AddressFragment : Fragment() {
    private var addressType = 1
    private var isNewUser: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isNewUser = it.getBoolean(ARG_NEW_USER, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }


    companion object {

        @JvmStatic
        fun newInstance(isNewUser: Boolean = false) =
            AddressFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_NEW_USER, isNewUser)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as BaseActivity).initChildHeaderForFragment(image_header)
    }

    private fun initial() {

        edt_address.setText((context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.address_des.trim())
        edt_address.setSelection(edt_address.text.length)
        addressType =
            (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.address_type

        if (arrayListOf("box2019", "box2020", "omnishopeu","box2021").contains(Config.platform)) {
            bn_voice.visibility = View.VISIBLE
        } else {
            bn_voice.visibility = View.GONE
        }


        rl_bn_next.setOnClickListener {
            if (edt_address.text.isBlank()) {
                Functions.showError(context!!, edt_address)
            } else {

                (context as UserInformationActivity).userInfoTemp.data.address.address_des =
                    edt_address.text.toString().trim()
                (context as UserInformationActivity).userInfoTemp.data.address.address_type =
                    addressType
                (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.address_des =
                    edt_address.text.toString().trim()
                (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.address_type =
                    addressType
                (context as UserInformationActivity).updateUser()

                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                dataObject.milestone = getString(R.string.text_nhap_dia_chi)//"Nhập địa chỉ"
                dataObject.recipientAddress = edt_address.text.toString().trim()
                dataObject.recipientCity =
                    (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.customer_province.name
                dataObject.recipientDistrict =
                    (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.customer_district.name
                val data = Gson().toJson(dataObject).toString()
                (activity as UserInformationActivity).logTrackingVersion2(
                    QuickstartPreferences.CLICK_COMPLETE_BUTTON_v2,
                    data
                )

            }
        }
        rl_bn_next.setOnFocusChangeListener { _, hasFocus ->
            Handler().postDelayed({
                bn_next?.isSelected = hasFocus
            }, 0)
        }
        bn_delete.setOnClickListener {
            activity?.let {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                dataObject.inputName = "Address"
                dataObject.inputType = "KEYBOARD_ADDRESS_TYPE"
                val data = Gson().toJson(dataObject).toString()
                (activity as BaseActivity).logTrackingVersion2(
                    QuickstartPreferences.CLICK_CLEAR_BUTTON_v2,
                    data
                )
            }
            edt_address.setText("")
        }

        bn_voice.setOnClickListener {
            activity?.let {
                (it as UserInformationActivity).let { mActivity ->
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.inputName = "Address"
                    dataObject.inputType = "Voice"
                    val data = Gson().toJson(dataObject).toString()
                    mActivity.logTrackingVersion2(
                        QuickstartPreferences.CLICK_VOICE_BUTTON_v2,
                        data
                    )
                }
            }
            (activity as UserInformationActivity).gotoDiscoveryVoice(REQUEST_VOICE_ADDRESS_CODE)
        }

        edt_address.setOnClickListener {
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
            dataObject.inputName = "Address"
            dataObject.inputType = "KEYBOARD_ADDRESS_TYPE"
            val data = Gson().toJson(dataObject).toString()
            (activity as BaseActivity).logTrackingVersion2(
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
                Handler().postDelayed({
                    if (bn_voice.visibility == View.VISIBLE) {
                        bn_voice.requestFocus()
                    } else {
                        bn_delete.requestFocus()
                    }
                }, 0)
                return@OnKeyListener true
            }
            false
        })
        edt_address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                val params = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                params.width = resources.getDimension(R.dimen._280sdp).toInt()
                if (edt_address.lineCount > 1) {
                    params.height = WRAP_CONTENT
                    edt_address.setPadding(
                        resources.getDimension(R.dimen._25sdp).toInt(),
                        resources.getDimension(R.dimen._10sdp).toInt(),
                        resources.getDimension(R.dimen._25sdp).toInt(),
                        resources.getDimension(R.dimen._18sdp).toInt()
                    )
                    val p = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )

                    p.setMargins(
                        resources.getDimension(R.dimen._minus8sdp).toInt(),
                        resources.getDimension(R.dimen._18sdp).toInt(),
                        0,
                        0
                    )
                    p.addRule(RelativeLayout.RIGHT_OF, R.id.edt_address)
                    layout_containt_button.layoutParams = p
                } else {
                    params.height = resources.getDimension(R.dimen._47sdp).toInt()
                    edt_address.setPadding(
                        resources.getDimension(R.dimen._25sdp).toInt(),
                        0,
                        resources.getDimension(R.dimen._25sdp).toInt(),
                        resources.getDimension(R.dimen._5sdp).toInt()
                    )
                    val p = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    p.setMargins(
                        resources.getDimension(R.dimen._minus8sdp).toInt(),
                        resources.getDimension(R.dimen._10sdp).toInt(),
                        0,
                        0
                    )
                    p.addRule(RelativeLayout.RIGHT_OF, R.id.edt_address)
                    layout_containt_button.layoutParams = p

                }
                edt_address.layoutParams = params
            }

        })

        ic_status.step.setImageResource(R.mipmap.milestone_step_3)

        isNewUser?.let {
            if (it) {
                ic_status.visibility = View.GONE
                ic_new_user.visibility = View.VISIBLE
                ic_new_user.step.setImageResource(R.mipmap.milestone_4_step_2)
                ic_new_user.step_2.setTextColor(ContextCompat.getColor(context!!, R.color.color_41AE96))
            }
        }

    }

    override fun onResume() {
        super.onResume()
        rl_bn_next.requestFocus()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_VOICE_ADDRESS_CODE -> {
                val result = data?.getStringExtra(RecognizerIntent.EXTRA_RESULTS)
                Log.d("AAA", result.toString())
                if (result != null && result.isNotBlank()) {
                    (activity as UserInformationActivity).let { mActivity ->
                        val dataObject = LogDataRequest()
                        dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                        dataObject.inputName = "Address"
                        dataObject.inputType = "Voice"
                        dataObject.result = result
                        mActivity.logTrackingVersion2(
                            QuickstartPreferences.VOICE_RESULT_v2,
                            Gson().toJson(dataObject).toString()
                        )
                    }
                    edt_address.setText(result.trim())
                }
            }
        }
    }
}

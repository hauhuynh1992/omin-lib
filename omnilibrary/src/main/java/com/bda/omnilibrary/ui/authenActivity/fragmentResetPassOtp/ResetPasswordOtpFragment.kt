package com.bda.omnilibrary.ui.authenActivity.fragmentResetPassOtp

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneContact
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneFragment
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhonePresenter
import kotlinx.android.synthetic.main.fragment_reset_pass_input_otp.*
import kotlinx.android.synthetic.main.item_activity_header.*

class ResetPasswordOtpFragment : Fragment(), ResetPasswordPhoneContact.View {

    private lateinit var presenter: ResetPasswordPhonePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = ResetPasswordPhonePresenter(this)
        return inflater.inflate(R.layout.fragment_reset_pass_input_otp, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tv_otp.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AuthenticationActivity).bn_search.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        tv_new_pass.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    tv_otp.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        tv_confirm_new_pass.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    tv_new_pass.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        tv_otp.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                tv_otp.text.toString(),
                tv_otp, tv_new_pass
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        tv_new_pass.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                tv_new_pass.text.toString(),
                tv_new_pass, tv_confirm_new_pass
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        tv_confirm_new_pass.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                tv_confirm_new_pass.text.toString(),
                tv_confirm_new_pass, null
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        bn_complete.setOnClickListener {
//            val request = LoginByPhoneRequest(
//                phoneNumber = edt_phone.text.toString(),
//                password = edt_phone.text.toString()
//            )
//            request.validate().also { result ->
//                if (result.first) {
//                    presenter.resetPasswordInputPhone(request)
//                } else {
//                    Functions.showMessage(activity!!, result.second.toString())
//                }
//            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ResetPasswordPhoneFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError(error: String) {
    }

    override fun showInputPhoneSuccess(customerId: String, customerName: String) {
        // TODO
    }
}
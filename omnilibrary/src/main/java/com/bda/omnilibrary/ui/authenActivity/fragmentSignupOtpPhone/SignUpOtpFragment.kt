package com.bda.omnilibrary.ui.authenActivity.fragmentSignupOtpPhone

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
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneFragment
import kotlinx.android.synthetic.main.fragment_signup_otp.*
import kotlinx.android.synthetic.main.item_activity_header.*

class SignUpOtpFragment : Fragment(), SignUpOtpContact.View {

    private lateinit var presenter: SignUpOtpPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = SignUpOtpPresenter(this)
        return inflater.inflate(R.layout.fragment_signup_otp, container, false)
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

        tv_otp.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                tv_otp.text.toString(),
                tv_otp, null
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        bn_confirm.setOnClickListener {
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

    override fun showSignUpNamePhoneSuccess(customerId: String, customerName: String) {
    }
}
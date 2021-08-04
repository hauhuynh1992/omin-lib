package com.bda.omnilibrary.ui.authenActivity.fragmentLoginPhone

import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.model.LoginByPhoneRequest
import com.bda.omnilibrary.ui.authenActivity.AuthenticationActivity
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneFragment
import com.bda.omnilibrary.util.Functions
import kotlinx.android.synthetic.main.fragment_login_phone.*
import kotlinx.android.synthetic.main.item_activity_header.*

class LoginPhoneFragment : Fragment(), LoginPhoneContact.View {

    private lateinit var presenter: LoginPhonePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter = LoginPhonePresenter(this)
        return inflater.inflate(R.layout.fragment_login_phone, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        bn_forgot_pass.setOnClickListener {
            (context as AuthenticationActivity).loadFragment(
                ResetPasswordPhoneFragment.newInstance(),
                R.id.frameLayout, true
            )
        }

        edt_password.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    edt_phone__.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone__.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP && event.action == KeyEvent.ACTION_DOWN) {
                Handler().postDelayed({
                    (activity as AuthenticationActivity).bn_search.requestFocus()
                }, 0)
                return@OnKeyListener true
            }
            false
        })

        edt_phone__.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_PHONE_TYPE,
                edt_phone__.text.toString(),
                edt_phone__, edt_password
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        edt_password.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_password.text.toString(),
                edt_password, null
            )
            keyboardDialog.show(childFragmentManager, keyboardDialog.tag)
        }

        bn_login.setOnClickListener {
            val request = LoginByPhoneRequest(
                phoneNumber = edt_phone__.text.toString(),
                password = edt_password.text.toString()
            )
            request.validate().also { result ->
                if (result.first) {
                    presenter.loginWithPhone(request)
                } else {
                    Functions.showMessage(activity!!, result.second.toString())
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LoginPhoneFragment().apply {
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

    override fun showLoginByPhoneSuccess(customerId: String, customerName: String) {
        // TODO
    }
}

package com.bda.omnilibrary.ui.authenActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.ui.authenActivity.fragmentLoginPhone.LoginPhoneFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentRequireLogin.RequireLoginFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentResetPassOtp.ResetPasswordOtpFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentSignupNamePhone.SignUpNamePhoneFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentSignupOtpPhone.SignUpOtpFragment
import com.bda.omnilibrary.ui.authenActivity.fragmentSignupPassword.SignUpPasswordFragment
import com.bda.omnilibrary.ui.authenActivity.resetPasswordPhoneFragment.ResetPasswordPhoneFragment
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson


class AuthenticationActivity : BaseActivity(),
    AuthenticationContract.View {

    private var REQUEST_CODE = -1
    lateinit var presenter: AuthenticationContract.Presenter

    //Fragment instance
    private var loginFragment: LoginPhoneFragment? = null
    private var requireFragment: RequireLoginFragment? = null
    private var resetPasswordPhoneFragment: ResetPasswordPhoneFragment? = null
    private var resetPasswordOtpFragment: ResetPasswordOtpFragment? = null
    private var signUpNamePhoneFragment: SignUpNamePhoneFragment? = null
    private var signUpOtpFragment: SignUpOtpFragment? = null
    private var signUpPasswordFragment: SignUpPasswordFragment? = null

    companion object {
        val REQUEST_CUSTOMER_RESULT_CODE = 1789
        val REQUEST_VOICE_INPUT_ADDRESS_COPDE = 1789
        var RESPONSE_CUSTOMER_RESULT = "response_customer_code"
        var REQUEST_CUSTOMER_RESULT = "result_customer_code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        REQUEST_CODE = intent.getIntExtra(REQUEST_CUSTOMER_RESULT, -1)
        if (REQUEST_CODE > -1) {
            loadFragment(
                RequireLoginFragment.newInstance(),
                R.id.frameLayout, true
            )
            presenter = AuthenticationPresenter(this, this)
            setResult(REQUEST_CODE)
        } else {
            setResult(REQUEST_CODE)
            finish()
        }

    }

    fun getCustomer(uid: String) {
        presenter.loadCustomer(uid)
    }

    private fun showResult(data: String) {
        val intent = Intent()
        intent.putExtra(RESPONSE_CUSTOMER_RESULT, data)
        setResult(REQUEST_CODE, intent)
        finish()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun loadFragment(fragment: Fragment, layout: Int, isAnimation: Boolean) {
        when (fragment) {
            is LoginPhoneFragment -> {
                loginFragment = fragment
            }
            is RequireLoginFragment -> {
                requireFragment = fragment
            }
            is ResetPasswordPhoneFragment -> {
                resetPasswordPhoneFragment = fragment
            }
            is ResetPasswordOtpFragment -> {
                resetPasswordOtpFragment = fragment
            }
            is SignUpNamePhoneFragment -> {
                signUpNamePhoneFragment = fragment
            }
            is SignUpOtpFragment -> {
                signUpOtpFragment = fragment
            }
            is SignUpPasswordFragment -> {
                signUpPasswordFragment = fragment
            }
        }
        super.loadFragment(fragment, layout, true)
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        when {
            loginFragment != null && loginFragment!!.isVisible -> {
            }
            resetPasswordPhoneFragment != null && resetPasswordPhoneFragment!!.isVisible -> {
            }
            resetPasswordOtpFragment != null && resetPasswordOtpFragment!!.isVisible -> {
            }
            signUpNamePhoneFragment != null && signUpNamePhoneFragment!!.isVisible -> {
            }
            signUpOtpFragment != null && signUpOtpFragment!!.isVisible -> {
            }
            signUpPasswordFragment != null && signUpPasswordFragment!!.isVisible -> {
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun sendSuccess(checkCustomerResponse: CheckCustomerResponse) {
        showResult(Gson().toJson(checkCustomerResponse))
    }

    override fun sendError(error: String) {
        Functions.showMessage(this, error)
        finish()
    }

}
package com.bda.omnilibrary.ui.userInfomationActivity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import com.bda.omnilibrary.R
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.mainActivity.MainActivity
import com.bda.omnilibrary.ui.userInfomationActivity.fragmentPhone.PhoneFragment
import com.bda.omnilibrary.util.Functions
import kotlinx.android.synthetic.main.activity_userinformation.*


class UserInformationActivity : BaseActivity(),
    UserInformationContract.View {
    lateinit var presenter: UserInformationContract.Presenter
    lateinit var userInfoTemp: CheckCustomerResponse
    private lateinit var preferencesHelper: PreferencesHelper
    private var REQUEST_PERMISSIONS_REQUEST_CODE = 0
    private var mode = 0
    private var isNewUser = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinformation)
        mode = intent.getIntExtra("mode", 0)
        isNewUser = intent.getBooleanExtra("isNewUser", false)

        preferencesHelper = PreferencesHelper(this)
        userInfoTemp = CheckCustomerResponse()
        userInfoTemp.data.alt_info = ArrayList()
        userInfoTemp.data.alt_info.add(ContactInfo())

        presenter = UserInformationPresenter(this, this)
        presenter.loadPresenter()
    }

    override fun loadFragment() {
        /*if (isNewUser) {
            loadFragment(QuickPayFragment.newInstance(), R.id.frameLayout, true)
        } else {
            loadFragment(PhoneFragment.newInstance(), R.id.frameLayout, true)
        }*/

        loadFragment(PhoneFragment.newInstance(), R.id.frameLayout, true)
    }

    override fun sendUpdateCustomer(updateCustomerResponce: CheckCustomerResponse) {
        progressBar5.visibility = View.VISIBLE
        when (mode) {
            1 -> {
                gotoAccount(false)
                finish()
            }
            2 -> {
                gotoInvoiceDetail(null, isNewUser, 0.0, "")
                finish()
            }
        }
    }

    override fun sendUpdateCustomerFail(error: String) {
        Functions.showMessage(this, error)
    }

    override fun sendError(error: String) {
        Functions.showMessage(this, error)
    }

    override fun sendLocationSuccess(address: String) {
        userInfoTemp.data.address.address_des = address
        userInfoTemp.data.alt_info[0].address.address_des = address
    }


    fun updateUser() {
        if (Functions.checkInternet(this)) {
            presenter.updateAddress(
                userInfoTemp
            )
        } else {
            Functions.showMessage(this, getString(R.string.no_internet))
        }
    }

    override fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@UserInformationActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    override fun sendProfileSuccess(defaultName: String, defaultPhone: String) {
        userInfoTemp.data.name = defaultName
        userInfoTemp.data.alt_info[0].customer_name = defaultName


        userInfoTemp.data.phone = defaultPhone
        userInfoTemp.data.alt_info[0].phone_number = defaultPhone
    }

    override fun sendProfileFalsed(message: String) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            preferencesHelper.firstTimeAskingInUsetInfo(false)
            if (checkPermissions()) {
                presenter.loadLocation()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in getFManager().fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }


    override fun onResume() {
        super.onResume()
        MainActivity.getMaiActivity()?.pauseMusicBackground()
    }
}
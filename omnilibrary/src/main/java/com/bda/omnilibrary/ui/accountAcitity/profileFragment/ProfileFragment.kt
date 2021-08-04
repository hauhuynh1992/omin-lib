package com.bda.omnilibrary.ui.accountAcitity.profileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.leanback.widget.VerticalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.ProfileAdapter
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.ContactInfo
import com.bda.omnilibrary.model.CustomerProfileResponse
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.editDeliveryAddressFragment.EditDeliveryAddressFragment
import com.bda.omnilibrary.ui.accountAcitity.editProfileFragment.EditProfileFragment
import com.bda.omnilibrary.util.Config
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_account.*

class ProfileFragment : Fragment(), ProfileContact.View {
    private lateinit var profileAdatper: ProfileAdapter
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var checkCustomerResponse: CheckCustomerResponse


    lateinit var rvList: VerticalGridView
    private lateinit var presenter: ProfilePresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        preferencesHelper = PreferencesHelper(requireContext())
        if (preferencesHelper.userInfo != null) {
            checkCustomerResponse = Gson().fromJson(
                preferencesHelper.userInfo,
                object : TypeToken<CheckCustomerResponse>() {}.type
            )
        }
        presenter = ProfilePresenter(this, activity!!)
        rvList = view.findViewById(R.id.vg)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun initial() {
        presenter.fetchProfile(checkCustomerResponse.data.uid)
    }

    override fun onResume() {
        super.onResume()
        initial()
    }

    override fun sendSuccess(profile: CheckCustomerResponse) {
        val delivery = ArrayList<ContactInfo>()
        profile.data.alt_info?.let { it ->
            delivery.addAll(it)
            delivery.sortByDescending { it.is_default_address }
        }
        profileAdatper = ProfileAdapter(requireActivity(), profile, delivery,
            onChangeDelivery = { data ->
                activity?.let {
                    val fragment = EditDeliveryAddressFragment.newInstance()
                    val bundle = bundleOf(
                        "uid" to checkCustomerResponse.data.uid,
                        "contactInfo" to data
                    )
                    fragment.arguments = bundle
                    (it as AccountActivity).loadFragment(
                        fragment,
                        R.id.frameLayout,
                        false
                    )
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
                    dataObject.addressId = data.address.uid
                    dataObject.recipientName = data.customer_name
                    dataObject.recipientPhone = data.phone_number
                    dataObject.recipientAddress = data.address.address_des
                    dataObject.recipientDistrict = data.address.customer_district.name
                    dataObject.recipientCity = data.address.customer_province.name
                    if (data.address.address_type == 1) {
                        dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_HOME
                    } else {
                        dataObject.recipientAddressType = QuickstartPreferences.ADDRESS_TYPE_COMPANY
                    }
                    dataObject.isDefault = data.is_default_address.toString()
                    it.logTrackingVersion2(
                        QuickstartPreferences.CLICK_EDIT_RECIPIENT_BUTTON_v2,
                        Gson().toJson(dataObject).toString()
                    )
                }
            },
            onAddDelivery = {
                activity?.let {
                    val fragment = EditDeliveryAddressFragment.newInstance()
                    val bundle = bundleOf(
                        "uid" to checkCustomerResponse.data.uid
                    )
                    fragment.arguments = bundle
                    (it as AccountActivity).loadFragment(
                        fragment,
                        R.id.frameLayout,
                        false
                    )
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
                    val data = Gson().toJson(dataObject).toString()
                    it.logTrackingVersion2(
                        QuickstartPreferences.CLICK_CREATE_RECIPIENT_BUTTON_v2,
                        data
                    )
                }
            },
            onChangeUserInfo = {
                activity?.let {
                    val fragment = EditProfileFragment.newInstance()
                    val bundle = bundleOf(
                        "profile" to profile
                    )
                    fragment.arguments = bundle
                    (it as AccountActivity).loadFragment(
                        fragment,
                        R.id.frameLayout,
                        false
                    )
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
                    val data = Gson().toJson(dataObject).toString()
                    it.logTrackingVersion2(QuickstartPreferences.CLICK_EDIT_PROFILE_BUTTON_v2, data)
                }
            }
        )
        rvList.adapter = profileAdatper
    }

    override fun sendFalsed(message: String) {

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
}

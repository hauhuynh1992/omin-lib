package com.bda.omnilibrary.ui.userInfomationActivity.fragmentDistrict


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.SelectProvinceAdapter
import com.bda.omnilibrary.helper.PreferencesHelper
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.model.ProvinceDistrictModel
import com.bda.omnilibrary.model.Region
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.userInfomationActivity.LocationFirstContact
import com.bda.omnilibrary.ui.userInfomationActivity.LocationFirstPresenter
import com.bda.omnilibrary.ui.userInfomationActivity.UserInformationActivity
import com.bda.omnilibrary.ui.userInfomationActivity.fragmentAddress.AddressFragment
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_address.*
import kotlinx.android.synthetic.main.fragment_district.*
import kotlinx.android.synthetic.main.fragment_district.bn_next
import kotlinx.android.synthetic.main.fragment_district.ic_status
import kotlinx.android.synthetic.main.fragment_district.image_header
import kotlinx.android.synthetic.main.fragment_district.rl_bn_next
import kotlinx.android.synthetic.main.fragment_district.rv_province
import kotlinx.android.synthetic.main.fragment_province.*
import kotlinx.android.synthetic.main.milestone_3_step.view.*
import kotlinx.android.synthetic.main.milestone_3_step.view.step
import kotlinx.android.synthetic.main.milestone_4_step.view.*

private const val ARG_PROVINCE_ID = "province_id"
private val ARG_NEW_USER = "isNewUser"

class DistrictFragment : Fragment(), LocationFirstContact.View {
    private var provinceId: String? = null
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var locationFirstPresenter: LocationFirstContact.Presenter
    private lateinit var listDistrict: ArrayList<ProvinceDistrictModel.Data>
    private lateinit var mAdapter: SelectProvinceAdapter
    private var mDistrict: Region? = null
    private lateinit var rvProvince: RecyclerView
    private var selectPosition = 0
    private var isNewUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            provinceId = it.getString(ARG_PROVINCE_ID)
            isNewUser = it.getBoolean(ARG_NEW_USER, false)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_district, container, false).apply {
            this@DistrictFragment.activity?.let {
                mAdapter = SelectProvinceAdapter(it, arrayListOf(), "") { item, posistion ->
                    mDistrict = item
                    selectPosition = posistion
                    (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.customer_district =
                        item

                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.value = mDistrict?.name
                    val data = Gson().toJson(dataObject).toString()
                    (activity as UserInformationActivity).logTrackingVersion2(
                        QuickstartPreferences.SELECT_DISTRICT_VALUE_v2,
                        data
                    )
                    rl_bn_next.requestFocus()
                }

                rvProvince = findViewById<RecyclerView>(R.id.rv_province).apply {
                    layoutManager =
                        GridLayoutManager(context, 5, LinearLayoutManager.HORIZONTAL, false)
                    adapter = mAdapter
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(province_id: String, isNewUser: Boolean = false) =
            DistrictFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PROVINCE_ID, province_id)
                    putBoolean(ARG_NEW_USER, isNewUser)
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as BaseActivity).initChildHeaderForFragment(image_header)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        preferencesHelper = PreferencesHelper(context!!)
        locationFirstPresenter =
            LocationFirstPresenter(
                this,
                context!!
            )
        Handler().postDelayed({
            locationFirstPresenter.loadDistrict(provinceId!!)
        }, 200)

        rl_bn_next.setOnClickListener {
            if (mDistrict != null) {
                (context as UserInformationActivity).loadFragment(
                    AddressFragment.newInstance(isNewUser),
                    R.id.frameLayout, true
                )
            } else {
                Functions.showMessage(activity!!, getString(R.string.need_district))
            }
        }
        rl_bn_next.setOnFocusChangeListener { _, hasFocus ->
            Handler().postDelayed({
                bn_next?.isSelected = hasFocus
            }, 0)
        }

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

    private fun loadData(listProvince: ArrayList<ProvinceDistrictModel.Data>) {
        mAdapter.setData(listProvince)
        if (mDistrict != null) {
            mAdapter.clickPosition = selectPosition
            mAdapter.notifyDataSetChanged()
        }
        rv_province.requestFocus()
    }


    override fun sendUISussess(model: ProvinceDistrictModel) {
        listDistrict = model.data
        loadData(listDistrict)
    }

    override fun onResume() {
        super.onResume()
        rl_bn_next.requestFocus()
    }

    override fun sendUIFail(erroMessage: String) {
        Functions.showMessage(context!!, erroMessage)
    }

    override fun sendUIProvinceSussess(model: ProvinceDistrictModel) {

    }

    override fun sendUIProvinceFail(erroMessage: String) {

    }

}
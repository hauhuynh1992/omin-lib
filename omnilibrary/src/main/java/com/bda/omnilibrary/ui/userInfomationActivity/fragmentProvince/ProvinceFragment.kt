package com.bda.omnilibrary.ui.userInfomationActivity.fragmentProvince


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.BuildConfig
import com.bda.omnilibrary.LibConfig
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
import com.bda.omnilibrary.ui.userInfomationActivity.fragmentDistrict.DistrictFragment
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_district.*
import kotlinx.android.synthetic.main.fragment_province.*
import kotlinx.android.synthetic.main.fragment_province.bn_next
import kotlinx.android.synthetic.main.fragment_province.ic_status
import kotlinx.android.synthetic.main.fragment_province.image_header
import kotlinx.android.synthetic.main.fragment_province.rl_bn_next
import kotlinx.android.synthetic.main.fragment_province.rv_province
import kotlinx.android.synthetic.main.milestone_3_step.view.*

private val ARG_NEW_USER = "isNewUser"

class ProvinceFragment : Fragment(), LocationFirstContact.View {

    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var mAdapter: SelectProvinceAdapter
    lateinit var rvProvince: RecyclerView
    private lateinit var locationFirstPresenter: LocationFirstContact.Presenter
    private var province: Region? = null
    private var selectPosistion = 0
    private var isNewUser: Boolean = false

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_province, container, false).apply {
            this@ProvinceFragment.activity?.let {
                mAdapter = SelectProvinceAdapter(it, arrayListOf(), "") { item, posistion ->
                    province = item
                    selectPosistion = posistion
                    (context as UserInformationActivity).userInfoTemp.data.alt_info[0].address.customer_province =
                        item
                    val dataObject = LogDataRequest()
                    dataObject.screen = Config.SCREEN_ID.CHANGE_RECIPIENT.name
                    dataObject.value = province?.name
                    val data = Gson().toJson(dataObject).toString()
                    (activity as UserInformationActivity).logTrackingVersion2(
                        QuickstartPreferences.SELECT_CITY_VALUE_v2,
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
        fun newInstance(isNewUser: Boolean = false) =
            ProvinceFragment().apply {
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
        preferencesHelper = PreferencesHelper(activity!!)
        Handler().postDelayed({
            locationFirstPresenter =
                LocationFirstPresenter(
                    this,
                    activity!!
                )
            locationFirstPresenter.loadProvince()
        }, 200)
        rl_bn_next.setOnClickListener {
            if (province == null) {
                Functions.showMessage(activity!!, getString(R.string.text_ban_chua_chon_tinh_thanh_pho)/*"Bạn chưa chọn Tỉnh/Thành phố"*/)
            } else {
                (context as UserInformationActivity).loadFragment(
                    DistrictFragment.newInstance(province!!.uid, isNewUser),
                    R.id.frameLayout, true
                )
            }

        }

        rv_province.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                Handler().postDelayed({
                    rl_bn_next.requestFocus()
                }, 0)

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
                if (LibConfig.APP_ORIGIN == Config.PARTNER.OMNISHOPEU.toString()) {

                } else {
                    //ic_status.setImageResource(R.mipmap.image_new_user_step_2)
                }

            }
        }
    }

    private fun loadData(listProvince: ArrayList<ProvinceDistrictModel.Data>) {
        if (listProvince.size > 0) {
            mAdapter.setData(listProvince)
            if (province != null) {
                mAdapter.clickPosition = selectPosistion
                mAdapter.notifyDataSetChanged()
            }
            rv_province.requestFocus()
        }
    }

    override fun sendUISussess(model: ProvinceDistrictModel) {

    }

    override fun sendUIFail(erroMessage: String) {

    }

    override fun onResume() {
        super.onResume()
        rl_bn_next.requestFocus()
    }

    override fun sendUIProvinceSussess(model: ProvinceDistrictModel) {
        preferencesHelper.setListProvince(Gson().toJson(model.data))
        loadData(model.data)
    }

    override fun sendUIProvinceFail(erroMessage: String) {
        Functions.showMessage(activity!!, erroMessage)
    }
}

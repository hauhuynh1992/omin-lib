package com.bda.omnilibrary.ui.accountAcitity.orderFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.HorizontalGridView
import androidx.leanback.widget.OnChildViewHolderSelectedListener
import androidx.leanback.widget.VerticalGridView
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.DeliveryFilterAdapter
import com.bda.omnilibrary.adapter.MenuOrderAdapter
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.model.ListOrderResponceV3
import com.bda.omnilibrary.model.LogDataRequest
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_order.*

class OrderFragment : Fragment(), OrderContact.View {
    private lateinit var presenter: OrderPresenter
    lateinit var deliveryFilterAdapter: DeliveryFilterAdapter
    lateinit var menuAdapter: MenuOrderAdapter
    lateinit var rvList: VerticalGridView
    lateinit var rvMenu: HorizontalGridView
    var isLoading = false
    private var isCanLoadMore = true
    private val THRESHOLD = 20
    private var page = 0
    private var status: String = "all"
    private var lastPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        activity?.let {
            deliveryFilterAdapter = DeliveryFilterAdapter(it as BaseActivity, arrayListOf())
        }

        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_tat_ca_don_hang)/*"Tất cả đơn hàng"*/)
        menuItem.add(getString(R.string.text_dang_xu_ly)/*"Đang xử lý"*/)
        menuItem.add(getString(R.string.text_dang_van_chuyen)/*"Đang vận chuyển"*/)
        menuItem.add(getString(R.string.text_da_giao_hang)/*"Đã giao hàng"*/)
        menuItem.add(getString(R.string.text_da_huy)/*"Đã huỷ"*/)
        menuItem.add(getString(R.string.text_dang_hoan_tra)/*"Đang hoàn trả"*/)
        menuItem.add(getString(R.string.text_hoan_tien)/*"Hoàn tiền"*/)

        menuAdapter = MenuOrderAdapter(activity!!, menuItem) { position ->

            page = 0
            menuAdapter.notifyDataSetChanged()
            deliveryFilterAdapter.clearAll()
            when (position) {
                0 -> {
                    status = "all"
                    presenter.getOrderByStatus(status, page)
                }
                1 -> {
                    status = "processing"
                    presenter.getOrderByStatus(status, page)
                }
                2 -> {
                    status = "shipping"
                    presenter.getOrderByStatus(status, page)
                }
                3 -> {
                    status = "delivered"
                    presenter.getOrderByStatus(status, page)
                }
                4 -> {
                    status = "cancel"
                    presenter.getOrderByStatus(status, page)
                }
                5 -> {
                    status = "returning"
                    presenter.getOrderByStatus(status, page)
                }
                6 -> {
                    status = "refunded"
                    presenter.getOrderByStatus(status, page)
                }

            }
            val dataObject = LogDataRequest()
            dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
            dataObject.tabName = status
            val data = Gson().toJson(dataObject).toString()
            (activity as AccountActivity)?.logTrackingVersion2(
                QuickstartPreferences.LOAD_SUB_TAB_v2,
                data
            )
        }

        rvList = view.findViewById<VerticalGridView>(R.id.rv_delivery).apply {
            adapter = deliveryFilterAdapter
        }
        rvMenu = view.findViewById<HorizontalGridView>(R.id.rv_menu).apply {
            adapter = menuAdapter
        }

        deliveryFilterAdapter.setOnItemClickListener(object :
            DeliveryFilterAdapter.OnItemClickListener {
            override fun onItemClick(data: ListOrderResponceV3.Data, position: Int) {
                lastPosition = position
                if (Functions.checkInternet(activity!!)) {
                    val dataObject = LogDataRequest()
                    dataObject.tabName = status
                    dataObject.orderId = data.uid
                    dataObject.value = data.totalPay.toString()
                    dataObject.orderStatus = Functions.convertOrderStatus(data.orderStatus)
                    // todo change
                    //dataObject.totalItem = data.items.size.toString()
                    dataObject.voucherCode = data.voucher_code
                    dataObject.voucherValue = data.voucher_value.toString()
                    val dataTracking = Gson().toJson(dataObject).toString()
                    (activity as AccountActivity)?.logTrackingVersion2(
                        QuickstartPreferences.CLICK_ORDER_v2,
                        dataTracking
                    )
                    /*val intent = Intent(activity, Step1Activity::class.java)
                    intent.putExtra("order", Gson().toJson(data))
                    startActivityForResult(intent, GOTO_DETAIL_INVOICE_REQUEST)*/
                    (activity as BaseActivity).gotoInvoiceDetail(data)
                } else {
                    Functions.showMessage(activity!!, getString(R.string.no_internet))
                }
            }

            override fun onShoppingContinueClick() {
                val dataObject = LogDataRequest()
                dataObject.screen = Config.SCREEN_ID.ACCOUNT.name
                dataObject.tabName = status
                val data = Gson().toJson(dataObject).toString()
                (activity as AccountActivity)?.logTrackingVersion2(
                    QuickstartPreferences.CLICK_CONTINUE_SHOPPING_BUTTON_v2,
                    data
                )
                requireActivity().finish()
            }

        })

        rvList.addOnChildViewHolderSelectedListener(object :
            OnChildViewHolderSelectedListener() {
            override fun onChildViewHolderSelected(
                parent: RecyclerView,
                child: RecyclerView.ViewHolder,
                position: Int,
                subposition: Int
            ) {
                super.onChildViewHolderSelected(parent, child, position, subposition)
                if (position != RecyclerView.NO_POSITION) {
                    parent.layoutManager?.let { layoutManager ->
                        if ((position > layoutManager.itemCount - 1 - THRESHOLD) && isCanLoadMore) {
                            if (!isLoading) {
                                presenter.getOrderByStatus(status, page)
                                isLoading = true
                            }

                        }
                    }
                }
            }
        })

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GOTO_DETAIL_INVOICE_REQUEST -> {
                    val orderId = data!!.getStringExtra("ORDER_ID")
                    deliveryFilterAdapter.deleteOrderById(orderId.toString())
                }
            }
        }
    }

    companion object {
        val GOTO_DETAIL_INVOICE_REQUEST = 123

        @JvmStatic
        fun newInstance() =
            OrderFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }


    override fun sendSuccess(orders: ArrayList<ListOrderResponceV3.Data>) {
        isCanLoadMore = orders.size != 0
        deliveryFilterAdapter.addAll(orders)
        isLoading = false
        page++
    }

    override fun sendFalsed(message: Int) {
    }

    override fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rvList.visibility = View.GONE
    }

    override fun hideLoading() {
        progressBar.visibility = View.GONE
        rvList.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        presenter = OrderPresenter(this, activity!!)
    }

    fun scrollToLastPosition() {
        Handler().postDelayed({
            if (lastPosition != -1) {
                rvList.layoutManager?.scrollToPosition(lastPosition)
            }
        }, 0)
    }
}

package com.bda.omnilibrary.ui.accountAcitity.notificationFragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.MenuOrderAdapter
import com.bda.omnilibrary.adapter.NotificationFromShoppingTVAdapter
import com.bda.omnilibrary.adapter.NotificationPromotionAdapter
import com.bda.omnilibrary.adapter.NotificationUpdateOrderAdapter
import com.bda.omnilibrary.dialog.WebviewDialog
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.reviewOrder.ReviewOrderFragment
import kotlinx.android.synthetic.main.fragment_account.*

class NotificationFragment : Fragment(),
    NotificationContact.View,
    NotificationUpdateOrderAdapter.UpdateOrderListener,
    NotificationFromShoppingTVAdapter.ItemShoppingTVListener,
    NotificationPromotionAdapter.ItemPromotionListener {
    private lateinit var presenter: NotificationPresenter
    lateinit var adapterUpdateOrder: NotificationUpdateOrderAdapter
    lateinit var adapterPromotion: NotificationPromotionAdapter
    lateinit var adapterFromShoppingTV: NotificationFromShoppingTVAdapter
    lateinit var rvList: RecyclerView
    lateinit var menuAdapter: MenuOrderAdapter
    lateinit var rvMenu: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        rvList = view.findViewById<RecyclerView>(R.id.rv_notification).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        rvMenu = view.findViewById<RecyclerView>(R.id.rv_menu).apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NotificationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_cap_nhat_don_hang)/*"Cập nhật đơn hàng"*/)
        menuItem.add(getString(R.string.text_khuyen_mai)/*"Khuyến mãi"*/)
        menuItem.add(getString(R.string.text_tu_omnishop)/*"Từ OmniShop"*/)
        menuAdapter = MenuOrderAdapter(activity!!, menuItem) { position ->
            when (position) {
                0 -> {
                    fetchUpdateOrder()
                }
                1 -> {
                    fetchPromotion()
                }
                2 -> {
                    fetchFromShoppingTV()
                }
            }
        }
        rvMenu.adapter = menuAdapter
    }

    override fun sendSuccess(orders: ListOrderResponce) {
        progressBar.visibility = View.GONE
        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_tat_ca_don_hang)/*"Tất cả đơn hàng"*/)
        menuItem.add(getString(R.string.text_cho_thanh_toan)/*"Chờ thanh toán"*/)
        menuItem.add(getString(R.string.text_dang_xu_ly)/*"Đang xử lý"*/)
        menuItem.add(getString(R.string.text_dang_van_chuyen)/*"Đang vận chuyển"*/)
        menuItem.add(getString(R.string.text_da_giao_hang)/*"Đã giao hàng"*/)
        menuItem.add(getString(R.string.text_da_huy)/*"Đã huỷ"*/)
        adapterUpdateOrder = NotificationUpdateOrderAdapter(activity!!, menuItem)
        adapterUpdateOrder.setUpdateOrderListener(this)
        rvList.adapter = adapterUpdateOrder

    }

    override fun sendFalsed(message: Int) {
    }

    override fun onResume() {
        super.onResume()
        presenter = NotificationPresenter(this, activity!!)
        Handler().postDelayed({
            presenter.loadPresenter()
        }, 300)

    }

    override fun onReviewOrderClicked(position: Int) {
        (activity as AccountActivity).loadFragment(
            ReviewOrderFragment.newInstance(),
            R.id.frameLayout,
            false
        )
    }

    override fun onConfirmOrderClicked(position: Int) {
    }

    override fun onItemPromotionClicked(position: Int) {
        WebviewDialog(activity!!, "file:///android_asset/privacy.html")
    }

    override fun onItemFromShoppingTVClicked(position: Int) {
        WebviewDialog(activity!!, "file:///android_asset/privacy.html")
    }

    private fun fetchUpdateOrder() {
        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_tat_ca_don_hang)/*"Tất cả đơn hàng"*/)
        menuItem.add(getString(R.string.text_cho_thanh_toan)/*"Chờ thanh toán"*/)
        menuItem.add(getString(R.string.text_dang_xu_ly)/*"Đang xử lý"*/)
        menuItem.add(getString(R.string.text_dang_van_chuyen)/*"Đang vận chuyển"*/)
        menuItem.add(getString(R.string.text_da_giao_hang)/*"Đã giao hàng"*/)
        menuItem.add(getString(R.string.text_da_huy)/*"Đã huỷ"*/)
        adapterUpdateOrder = NotificationUpdateOrderAdapter(activity!!, menuItem)
        rvList.adapter = adapterUpdateOrder
        adapterUpdateOrder.setUpdateOrderListener(this)
        adapterUpdateOrder.notifyDataSetChanged()
    }

    private fun fetchPromotion() {
        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_tat_ca_don_hang)/*"Tất cả đơn hàng"*/)
        menuItem.add(getString(R.string.text_cho_thanh_toan)/*"Chờ thanh toán"*/)
        menuItem.add(getString(R.string.text_dang_xu_ly)/*"Đang xử lý"*/)
        menuItem.add(getString(R.string.text_dang_van_chuyen)/*"Đang vận chuyển"*/)
        menuItem.add(getString(R.string.text_da_giao_hang)/*"Đã giao hàng"*/)
        menuItem.add(getString(R.string.text_da_huy)/*"Đã huỷ"*/)
        adapterPromotion = NotificationPromotionAdapter(activity!!, menuItem)
        adapterPromotion.setItemListener(this)
        rvList.adapter = adapterPromotion
        adapterPromotion.notifyDataSetChanged()
    }

    private fun fetchFromShoppingTV() {
        val menuItem = ArrayList<String>()
        menuItem.add(getString(R.string.text_tat_ca_don_hang)/*"Tất cả đơn hàng"*/)
        menuItem.add(getString(R.string.text_cho_thanh_toan)/*"Chờ thanh toán"*/)
        menuItem.add(getString(R.string.text_dang_xu_ly)/*"Đang xử lý"*/)
        menuItem.add(getString(R.string.text_dang_van_chuyen)/*"Đang vận chuyển"*/)
        menuItem.add(getString(R.string.text_da_giao_hang)/*"Đã giao hàng"*/)
        menuItem.add(getString(R.string.text_da_huy)/*"Đã huỷ"*/)
        adapterFromShoppingTV = NotificationFromShoppingTVAdapter(activity!!, menuItem)
        adapterFromShoppingTV.setItemListener(this)
        rvList.adapter = adapterFromShoppingTV
        adapterFromShoppingTV.notifyDataSetChanged()
    }
}
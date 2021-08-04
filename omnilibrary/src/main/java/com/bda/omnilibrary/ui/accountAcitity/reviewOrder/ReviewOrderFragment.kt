package com.bda.omnilibrary.ui.accountAcitity.reviewOrder

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.widget.VerticalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.ReviewOrderAdapter
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.accountAcitity.reviewProduct.ReviewProductFragment

class ReviewOrderFragment : Fragment(), ReviewOrderContact.View {
    private lateinit var presenter: ReviewOrderPresenter
    private lateinit var listOrderResponceData: ListOrderResponce
    private lateinit var adapterReviewOrder: ReviewOrderAdapter
    private lateinit var rvList: VerticalGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_order, container, false)
        adapterReviewOrder = ReviewOrderAdapter(activity!!, arrayListOf())
        rvList = view.findViewById<VerticalGridView>(R.id.rv_review_order)
        rvList.adapter = adapterReviewOrder
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ReviewOrderFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun sendSuccess(orders: ListOrderResponce) {
        this.listOrderResponceData = orders

        var tema = ArrayList<String>()
        tema.add("AAAA")
        tema.add("AAAA")
        tema.add("AAAA")
        tema.add("AAAA")
        tema.add("AAAA")

        if (tema.size > 0) {
            adapterReviewOrder.setData(tema)
            rvList.visibility = View.VISIBLE
            Handler().postDelayed({
                rvList.requestFocus()
            }, 100)
            adapterReviewOrder.setOnItemClickListener(object :
                ReviewOrderAdapter.OnItemClickListener {
                override fun onViewDetailClick(position: Int) {
                }

                override fun onReOrderItemClick(position: Int) {

                }

                override fun onReviewProductClick(position: Int) {
                    (activity as AccountActivity).loadFragment(
                        ReviewProductFragment.newInstance(),
                        R.id.frameLayout,
                        false
                    )
                }
            })
        } else {
            rvList.visibility = View.GONE
        }
    }


    override fun sendFalsed(message: Int) {
    }

    override fun onResume() {
        super.onResume()
        presenter = ReviewOrderPresenter(this, activity!!)
        Handler().postDelayed({
            presenter.loadPresenter()
        }, 300)

    }
}

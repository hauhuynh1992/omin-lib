package com.bda.omnilibrary.ui.accountAcitity.buyLaterFragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.leanback.widget.VerticalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.WishListAdapter
import com.bda.omnilibrary.model.ListOrderResponce
import kotlinx.android.synthetic.main.fragment_buy_later.*

@Suppress("unused")
class BuyLaterFragment : Fragment(), BuyLaterContact.View {
    private lateinit var presenter: BuyLaterPresenter
    private lateinit var listOrderResponceData: ListOrderResponce
    private lateinit var adapter: WishListAdapter
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_buy_later, container, false)
        rvList = view.findViewById<VerticalGridView>(R.id.rv_later_list)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BuyLaterFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun sendSuccess(orders: ListOrderResponce) {
        this.listOrderResponceData = orders

        val tema = ArrayList<String>()
        if (tema.size == 0) {
            rv_empty.visibility = View.VISIBLE
            rvList.visibility = View.GONE
            Handler().postDelayed({
                bn_continue_shopping.requestFocus()
            }, 0)

            bn_continue_shopping.setOnFocusChangeListener { view, isFocus ->
                if (isFocus) {
                    activity?.let {
                        val textColorId = ContextCompat.getColorStateList(
                            it,
                            R.color.color_white
                        )
                        bn_continue_shopping.setTextColor(textColorId)
                    }
                } else {
                    activity?.let {
                        val textColorId = ContextCompat.getColorStateList(
                            it,
                            R.color.title_black
                        )
                        bn_continue_shopping.setTextColor(textColorId)
                    }
                }
            }
        } else {
            rv_empty.visibility = View.GONE
            rvList.visibility = View.VISIBLE
//            adapter = WishListAdapter(activity!!, tema)
//            adapter.setOnItemClickListener(object :
//                WishListAdapter.OnItemClickListener {
//                override fun onViewDetailClick(position: Int) {
//                }
//
//                override fun onRemoveItemClick(position: Int) {
//                }
//
//                override fun onAddToCartClick(position: Int) {
//                }
//
//            })
//            rvList.adapter = adapter
        }


    }

    override fun sendFalsed(message: Int) {
    }

    override fun onResume() {
        super.onResume()
        presenter = BuyLaterPresenter(this, activity!!)
        Handler().postDelayed({
            presenter.loadPresenter()
        }, 300)

    }

//    override fun onViewDetailClick(position: Int) {
//    }
//
//    override fun onRemoveItemClick(position: Int) {
//    }
//
//    override fun onAddToCartClick(position: Int) {
//    }


}

package com.bda.omnilibrary.ui.liveStreamActivity.favourite

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.LiveStreamBaseFragment
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_favourite_live_stream.*

class FavouriteFragment : LiveStreamBaseFragment(), FavouriteContract.View {
    private lateinit var product: Product
    private lateinit var presenter: FavouriteContract.Presenter
    private lateinit var mAdapter: ItemFavouriteAdapter
    private lateinit var products: ArrayList<Product>
    private var timer: CountDownTimer? = null
    private var listener: FavouriteListener? = null
    private var isFavourite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = Gson().fromJson(
                it.getString(STR_PRODUCT),
                Product::class.java
            )

            isFavourite = it.getBoolean(STR_LOVE)
        }

        presenter = FavouritePresenter(this, context!!)
        if (!isFavourite) {
            presenter.postAddFavourite(product.uid)
        }
        //presenter.getListFavourite(0)
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite_live_stream, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isFavourite) {
            mAdapter = ItemFavouriteAdapter(activity as BaseActivity, arrayListOf(product))
            voucher_list?.apply {
                adapter = mAdapter
            }

            timer = (activity as BaseActivity).startTimer({
                (activity as BaseActivity).focusDummyView()
                (activity as BaseActivity).getFManager().popBackStack()
            }) {
                text_bn_detail?.text = getString(R.string.hiding_after_moment, it)
            }
        }

        bn_go_favourite.setOnClickListener {
            (activity as BaseActivity).gotoAccount(false)
        }

        bn_go_favourite.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_go_favourite, 1.05f)
                text_bn_go_favourite.setNewTextColor(R.color.color_white)
            } else {
                Functions.animateScaleDown(bn_go_favourite)
                text_bn_go_favourite.setNewTextColor(R.color.color_484848)
            }
        }

        bn_detail.setOnClickListener {
            // showGotoFavourite()
        }

        /*bn_detail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Functions.animateScaleUp(bn_detail, 1.05f)
                text_bn_detail.setNewTextColor(R.color.color_484848)
            } else {
                Functions.animateScaleDown(bn_detail)
                text_bn_detail.setNewTextColor(R.color.text_color_gray)
            }
        }*/
    }

    private fun showGotoFavourite() {
        mAdapter.updateData(products)
        bn_detail?.visibility = View.GONE
        bn_go_favourite?.visibility = View.VISIBLE
        (activity as BaseActivity).focusDummyView()

        requestFocus()
    }

    override fun onResume() {
        super.onResume()
        requestFocus()
    }

    override fun requestFocus() {
        Handler().postDelayed({
            if (bn_detail != null && bn_detail.visibility == View.VISIBLE)
                bn_detail?.requestFocus()
            else if (bn_go_favourite != null && bn_go_favourite.visibility == View.VISIBLE)
                bn_go_favourite?.requestFocus()
        }, 100)
    }

    override fun isAbleToHideByClickLeft() = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GOTO_DETAIL_INVOICE_REQUEST -> {
                    val orderId = data!!.getStringExtra("ORDER_ID")
                    //deliveryFilterAdapter.deleteOrderById(orderId)
                }
            }
        }
    }

    companion object {
        val GOTO_DETAIL_INVOICE_REQUEST = 123
        private val STR_PRODUCT = "product"
        private val STR_LOVE = "love"

        @JvmStatic
        fun newInstance(product: Product, isFavourite: Boolean) =
            FavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(STR_PRODUCT, Gson().toJson(product))
                    putBoolean(STR_LOVE, isFavourite)
                }
            }
    }

    override fun sendFalsed(message: String) {
        Functions.showMessage(activity!!, message)
        (activity as BaseActivity).focusDummyView()
        listener?.onFavourite(false)
        (activity as BaseActivity).getFManager().popBackStack()

    }

    override fun onDestroy() {
        presenter?.disposeAPI()
        super.onDestroy()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (timer != null)
            timer!!.cancel()
    }

    override fun sendListFavouriteSuccess(products: ArrayList<Product>) {
        this.products = products
        moveNewestOnTop()

        //todo
    }

    override fun sendAddFavouriteSuccess() {
        listener?.onFavourite(true)

        mAdapter = ItemFavouriteAdapter(activity as BaseActivity, arrayListOf(product))
        voucher_list?.apply {
            adapter = mAdapter
        }

        timer = (activity as BaseActivity).startTimer({
            if (activity != null) {
                (activity as BaseActivity).focusDummyView()
                (activity as BaseActivity).getFManager().popBackStack()
            }
        }) {
            text_bn_detail?.text = getString(R.string.hiding_after_moment, it)
        }
    }

    fun setListener(listener: FavouriteListener) {
        this.listener = listener
    }

    private fun moveNewestOnTop() {
        for (i in 0 until products.size) {
            if (products[i].uid == product.uid) {
                products.removeAt(i)
                break
            }
        }

        products.add(0, product)
    }

    interface FavouriteListener {
        fun onFavourite(isLike: Boolean)
    }

}
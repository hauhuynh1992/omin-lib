package com.bda.omnilibrary.ui.liveStreamActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.FragmentManager
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.livestream.ItemLiveStreamAdapterV3
import com.bda.omnilibrary.adapter.livestream.OthersVideoAdapter
import com.bda.omnilibrary.adapter.livestream.TabLiveStreamAdapter
import com.bda.omnilibrary.helper.QuickstartPreferences
import com.bda.omnilibrary.model.CheckCustomerResponse
import com.bda.omnilibrary.model.LiveStream
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.ui.liveStreamActivity.AddedListToCart.AddedListToCartFragment
import com.bda.omnilibrary.ui.liveStreamActivity.addedProductInCart.AddedProductInCartFragment
import com.bda.omnilibrary.ui.liveStreamActivity.chooseVoucher.ChooseVoucherFragment
import com.bda.omnilibrary.ui.liveStreamActivity.detailLiveStreamProduct.DetailLiveStreamProductFragment
import com.bda.omnilibrary.ui.liveStreamActivity.playbackExo.VideoConsumptionWithExoPlayerFragment
import com.bda.omnilibrary.ui.liveStreamActivity.successOrder.SuccessFragment
import com.bda.omnilibrary.util.Config
import com.bda.omnilibrary.util.Functions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_live_stream_portrait.*
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class LiveStreamPortraitActivity : BaseActivity(), LiveStreamContract.View,
    MotionLayout.TransitionListener {
    private var userInfo: CheckCustomerResponse? = null
    private lateinit var presenter: LiveStreamContract.Presenter

    private var live: List<LiveStream>? = null

    private var mLiveStream: LiveStream? = null
    private var url = ""

    private val listTab = arrayListOf(/*"info", */"product", "video"/*, "voucher", "comment"*/)
    private var isPlayAnimation = false
    private var currentTab = 0
    private var currentSlectedProduct: Product? = null

    private var othersVideoAdapter: OthersVideoAdapter? = null
    private var itemLiveStreamAdapterV3: ItemLiveStreamAdapterV3? = null
    private var ssaiTimeout: Disposable? = null

    private var productTab: TabLiveStreamAdapter? = null
    private var videoFragment: VideoConsumptionWithExoPlayerFragment? = null

    companion object {
        private var weakActivity: WeakReference<LiveStreamPortraitActivity>? = null

        fun getActivity(): LiveStreamPortraitActivity? {
            return if (weakActivity != null) {
                weakActivity?.get()
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_stream_portrait)
        setLayoutId(R.id.layout3)

        weakActivity = WeakReference(this@LiveStreamPortraitActivity)

        //mLiveStream = intent?.getStringExtra(QuickstartPreferences.LIVESTREAM)

        intent?.let {
            mLiveStream = Gson().fromJson(
                it.getStringExtra(QuickstartPreferences.LIVESTREAM),
                LiveStream::class.java
            )
        }

        if (mLiveStream == null) {
            finish()
        }

        userInfo = Gson().fromJson(
            getPreferenceHelper()!!.userInfo,
            object : TypeToken<CheckCustomerResponse>() {}.type
        )
        presenter = LiveStreamPresenter(this, this)
        live = getLive()
        initial()
    }

    private fun initial() {
        loadVideo()

        itemLiveStreamAdapterV3 = ItemLiveStreamAdapterV3(this, mLiveStream!!.products)

        dummy_view.setOnClickListener {
            // todo nothing
        }

        name_livestram.text = mLiveStream?.display_name

        itemLiveStreamAdapterV3!!.setOnCallbackListener(object :
            ItemLiveStreamAdapterV3.OnCallBackListener {
            override fun onItemClick(product: LiveStream.StreamProduct) {

                if (!isPlayAnimation) {
                    product_list_view.setNumColumns(2)
                    itemLiveStreamAdapterV3?.numColumn = 2
                    currentSlectedProduct = product.product
                    m_motion_layout?.let {
                        it.setTransition(R.id.transition_detail_portrait)
                        it.setTransitionListener(this@LiveStreamPortraitActivity)
                        it.transitionToEnd()
                    }

                    if (mLiveStream!!.status == 2)
                        seekToProductTime(product.time)
                }
            }
        })

        othersVideoAdapter = OthersVideoAdapter(this, live!!).apply {
            setOnCallbackListener(object : OthersVideoAdapter.OnCallBackListener {
                override fun onItemClick(live: LiveStream) {
                    gotoLiveStreamProduct(live.uid)
                    finish()
                }
            })

            numColumn = 4
        }

        // tab above list product
        productTab = TabLiveStreamAdapter(this, listTab).apply {
            setOnCallbackListener(object : TabLiveStreamAdapter.OnCallBackListener {
                override fun onItemClick(p: Int) {
                    if (currentTab != p) {
                        currentTab = p
                        when (listTab[p]) {
                            "product" -> {
                                product_list_view?.apply {
                                    setNumColumns(4)
                                    itemLiveStreamAdapterV3?.numColumn = 4
                                    adapter = itemLiveStreamAdapterV3
                                }

                                Handler().postDelayed({
                                    product_list_view?.requestFocus()
                                }, 200)
                            }

                            "video" -> {
                                product_list_view?.apply {
                                    setNumColumns(4)
                                    itemLiveStreamAdapterV3?.numColumn = 4
                                    adapter = othersVideoAdapter
                                }

                                Handler().postDelayed({
                                    product_list_view?.requestFocus()
                                }, 200)
                            }
                        }
                    } else {
                        Handler().postDelayed({
                            product_list_view?.requestFocus()
                        }, 200)
                    }
                }
            })

            setCurrentIndex(0)
        }

        list_tab?.apply {
            adapter = productTab
        }

        product_list_view?.apply {
            setNumColumns(4)
            itemLiveStreamAdapterV3?.numColumn = 4
            adapter = itemLiveStreamAdapterV3
        }
        //todo change

        //ImageUtils.loadImage(this, image_supplier, "")
        //detail_supplier_name.setText("")
        //supplier_number_count.setText("")


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getLive(): List<LiveStream> {

        // todo oooooooooooooooo
        for (i in Config.homeData!!.data.section) {
            if (i.sectionValue == "livestream") {
                return i.livestream.filterIndexed { _, liveStream ->
                    liveStream.uid != this@LiveStreamPortraitActivity.mLiveStream!!.uid
                }
            }
        }
        return arrayListOf()
    }


    private fun getCurrentFragment() = getFManager().findFragmentById(layoutToLoadId())

    private fun loadVideo() {
        if (mLiveStream!!.status == 1) {
            if (mLiveStream!!.channel.size > 0) {
                url = mLiveStream!!.channel[0].link
            }
        } else {
            if (mLiveStream!!.video_transcode.isNotEmpty()) {
                url = mLiveStream!!.video_transcode
            }
        }

        val ft = supportFragmentManager.beginTransaction()
        videoFragment = VideoConsumptionWithExoPlayerFragment.newInstance(url)

        ft.add(R.id.cartContainer, videoFragment!!, VideoConsumptionWithExoPlayerFragment.TAG)
        ft.commit()
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN
            && event.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !videoFragment!!.isControlsOverlayVisible
        ) {

            if (!isPlayAnimation && layout2.translationX > 0) {
                m_motion_layout?.let {
                    it.setTransition(R.id.transition_product_portrait)
                    it.setTransitionListener(this)
                    it.transitionToEnd()
                }
                return true
            }
        }

        return super.dispatchKeyEvent(event)
    }

    override fun onBackPressed() {
        when {
            getFManager().backStackEntryCount > 1 -> {
                focusDummyView()

                val f = getCurrentFragment()

                // todo change, dumb
                if (f is AddedListToCartFragment || f is AddedProductInCartFragment)
                    getFManager().popBackStack()
                else if (f is SuccessFragment) {
                    getFManager().popBackStack()
                    getFManager().popBackStack()
                }

                getFManager().popBackStack()

            }

            layout3.visibility == View.VISIBLE -> {
                hideDetailProduct()
            }

            layout2.visibility == View.VISIBLE -> {
                if (!isPlayAnimation)
                    m_motion_layout?.let {
                        it.setTransition(R.id.transition_product_portrait_reverse)
                        it.setTransitionListener(this)
                        it.transitionToEnd()
                    }
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val f = getCurrentFragment()
        timer()

        if (event!!.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (layout2.visibility != View.VISIBLE) {
                        //todo
                    }
                }

                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    if (layout3.visibility == View.VISIBLE && f != null
                        && f is DetailLiveStreamProductFragment
                        && (f as LiveStreamBaseFragment).isAbleToHideByClickLeft()
                        && !isPlayAnimation
                    ) {
                        hideDetailProduct()
                    } else if (itemLiveStreamAdapterV3 != null
                        && itemLiveStreamAdapterV3!!.isLeftFocus
                    ) {
                        return true
                    } else if (othersVideoAdapter != null
                        && othersVideoAdapter!!.isLeftFocus
                    ) {
                        return true
                    } else if (productTab != null && productTab!!.isFirst) {
                        return true
                    }
                }

                KeyEvent.KEYCODE_DPAD_UP -> {

                    if (product_list_view.hasFocus() && ((itemLiveStreamAdapterV3 != null && itemLiveStreamAdapterV3!!.isTopFocus)
                                || (othersVideoAdapter != null && othersVideoAdapter!!.isTopFocus))
                    ) {
                        Handler().postDelayed({
                            list_tab.requestFocus()
                        }, 0)
                    } else if (list_tab != null && list_tab!!.hasFocus()) {
                        return true
                    }
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {

                }
            }
        }

        if (f != null && f is ChooseVoucherFragment && layout3.visibility == View.VISIBLE) {
            return if (f.myOnkeyDown(keyCode, event))
                true
            else
                return super.onKeyDown(keyCode, event)
        } else if (f != null && f is DetailLiveStreamProductFragment && layout3.visibility == View.VISIBLE) {
            return if (f.myOnkeyDown(keyCode, event))
                true
            else
                return super.onKeyDown(keyCode, event)
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun focusDummyView() {
        Handler().postDelayed({
            dummy_view?.requestFocus()
        }, 0)
    }

    override fun hideDetailProduct() {
        if (!isPlayAnimation) {
            m_motion_layout?.let {
                it.setTransition(R.id.transition_detail_portrait)
                it.addTransitionListener(this)
                it.transitionToStart()
            }

            isLoadFirstFragment = false
            getFManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            product_list_view?.setNumColumns(4)
            itemLiveStreamAdapterV3?.numColumn = 4

            Handler().postDelayed({
                product_list_view?.requestFocus()
            }, 100)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in getFManager().fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun sendSuccess(liveStream: LiveStream) {
    }

    override fun sendFalsed(error: String) {
        Functions.showMessage(this, error)
    }

    override fun sendAddressSuccess(data: CheckCustomerResponse) {
    }

    override fun sendAddressFailed(message: String) {
    }

    override fun finishThis(order_Id: String) {
    }

    override fun sendFailedOrder(message: String) {
    }

    override fun seekToProductTime(time: Int) {
        videoFragment?.seekTo((time * 1000).toLong())
    }

    private fun clearTransitionLayout() {
        m_motion_layout?.setTransition(-1, -1)
    }

    private fun timer() {
        if (ssaiTimeout != null) {
            ssaiTimeout?.dispose()
        }

        if (layout2.visibility != View.VISIBLE)
            ssaiTimeout = Completable.timer(
                6,
                TimeUnit.SECONDS,
                AndroidSchedulers.mainThread()
            ).subscribe {
                // todo changeeeee
            }
    }

    /////////////// MotionLayout.TransitionListener ////////////////
    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
        isPlayAnimation = true
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
        isPlayAnimation = true
    }

    // trick
    private var isLoadFirstFragment = false
    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        isPlayAnimation = false

        when {

            (p1 == R.id.end_detail_portrait && currentSlectedProduct != null && !isLoadFirstFragment) -> {
                isLoadFirstFragment = true
                loadFragment(
                    DetailLiveStreamProductFragment.newInstance(currentSlectedProduct!!),
                    R.id.layout3,
                    true
                )
            }

            p1 == R.id.end_portrait -> {
                hint.visibility = View.GONE
                product_list_view.setNumColumns(4)
                itemLiveStreamAdapterV3?.numColumn = 4
                Handler().postDelayed({
                    product_list_view?.requestFocus()
                }, 0)
            }

            p1 == R.id.start_portrait -> {
                hint.visibility = View.VISIBLE
            }

            p1 == R.id.start_detail_portrait -> {
                Handler().postDelayed({
                    product_list_view?.requestFocus()
                }, 0)
            }
        }
    }
    /////////////// MotionLayout.TransitionListener ////////////////

}
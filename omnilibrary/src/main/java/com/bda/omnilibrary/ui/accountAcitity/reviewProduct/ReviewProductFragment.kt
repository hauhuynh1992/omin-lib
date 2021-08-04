package com.bda.omnilibrary.ui.accountAcitity.reviewProduct

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.leanback.widget.HorizontalGridView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.MenuOrderAdapter
import com.bda.omnilibrary.model.ListOrderResponce
import com.bda.omnilibrary.ui.accountAcitity.AccountActivity
import com.bda.omnilibrary.ui.commentActivity.CommentActivity
import kotlinx.android.synthetic.main.fragment_review_product.*

class ReviewProductFragment : Fragment(), ReviewProductContact.View {
    private lateinit var presenter: ReviewProductPresenter
    private lateinit var bnReview: Button
    private lateinit var rv_comments: HorizontalGridView
    private lateinit var commentAdapters: MenuOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_product, container, false)
        bnReview = view.findViewById(R.id.bn_review)
        rv_comments = view.findViewById(R.id.rv_comments)
        bnReview.setOnClickListener {
            @Suppress("UNNECESSARY_SAFE_CALL")
            (activity as AccountActivity)?.let {
                startActivityForResult(
                    Intent(it, CommentActivity::class.java),
                    REQUEST_COMMENT_CODE
                )
            }
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_COMMENT_CODE && data != null) {
                var listComments = ArrayList<String>()
                try {
                    listComments = data.getStringArrayListExtra("result") as ArrayList<String>
                } catch (e: Exception) {
                    Log.d("AAA", e.message.toString())
                }
                if (listComments.size > 0) {
                    bnReview.visibility = View.GONE
                    rv_comments.visibility = View.VISIBLE
                    commentAdapters = MenuOrderAdapter(
                        requireActivity(),
                        listComments
                    ) {

                    }
                    rv_comments.adapter = commentAdapters
                }

            }
        }
    }

    companion object {
        const val REQUEST_COMMENT_CODE = 8080

        @JvmStatic
        fun newInstance() =
            ReviewProductFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun sendSuccess(orders: ListOrderResponce) {
    }


    override fun sendFalsed(message: Int) {
    }

    override fun onResume() {
        super.onResume()
        presenter = ReviewProductPresenter(this, activity!!)
        presenter.loadPresenter()
        Handler().postDelayed({
            rating.requestFocus()
        }, 300)

    }
}

package com.bda.omnilibrary.ui.commentActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import androidx.core.view.get
import androidx.leanback.widget.HorizontalGridView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.adapter.CommentAdapter
import com.bda.omnilibrary.dialog.KeyboardDialog
import com.bda.omnilibrary.model.Comment
import com.bda.omnilibrary.ui.baseActivity.BaseActivity
import com.bda.omnilibrary.util.Functions
import kotlinx.android.synthetic.main.activity_comment.*


class CommentActivity : BaseActivity(), CommentContract.View {

    private lateinit var presenter: CommentPresenter
    private lateinit var rvList: RecyclerView
    private lateinit var adapterComment: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        presenter = CommentPresenter(this, this)
        val comments = ArrayList<Comment>()
        comments.add(Comment("Sử dụng tốt", false))
        comments.add(Comment("Giao hàng nhanh chóng", false))
        comments.add(Comment("Đóng gói cẩn thận", false))
        comments.add(Comment("Không hài lòng", false))
        comments.add(Comment("Chất lượng kém", false))
        comments.add(Comment("Giao hàng nhanh chóng", false))
        comments.add(Comment("Không hài lòng", false))
        comments.add(Comment("Chất lượng kém", false))
        adapterComment = CommentAdapter(this, comments) { text ->
        }
        rvList = findViewById<HorizontalGridView>(R.id.rv_comment).apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = adapterComment
        }

        bn_voice.setOnClickListener {
//            gotoDiscoveryVoice(REQUEST_VOICE_COMMENT_CODE)
        }

        bn_delete.setOnClickListener {
            edt_comment.setText("")
        }

        bn_review.setOnClickListener {
            val listComments = adapterComment.getSelectComment() as ArrayList<String>
            if (edt_comment.text.toString().isNotBlank()) {
                listComments.add(edt_comment.text.toString())
            }
            if (listComments.size < 0) {
                Functions.showMessage(this, getString(R.string.text_vui_long_danh_gia_sp)/*"Vui lòng cho đánh giá sản phẩm"*/)
            } else {
                val returnIntent = Intent()
                returnIntent.putStringArrayListExtra("result", listComments)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

        edt_comment.setOnClickListener {
            val keyboardDialog = KeyboardDialog(
                KeyboardDialog.KEYBOARD_ADDRESS_TYPE,
                edt_comment.text.toString(),
                edt_comment
            )
            keyboardDialog.show(supportFragmentManager, keyboardDialog.tag)
        }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({
            rvList[0].requestFocus()
        }, 300)


    }

    override fun sendQrCodeBase64(encode: String) {
    }

    override fun sendSuccess() {
    }

    override fun sendFalsed(message: String) {
    }

    override fun finishActivity() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            when (requestCode) {
                REQUEST_VOICE_COMMENT_CODE -> {
                    edt_comment.setText(result?.get(0).toString().trim())
                }
            }
        }
    }

    companion object {
        const val REQUEST_VOICE_COMMENT_CODE = 145
    }
}

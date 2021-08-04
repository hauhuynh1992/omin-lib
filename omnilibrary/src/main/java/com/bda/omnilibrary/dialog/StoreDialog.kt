package com.bda.omnilibrary.dialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.bda.omnilibrary.R
import com.bda.omnilibrary.model.Product
import com.bda.omnilibrary.util.ImageUtils
import com.bda.omnilibrary.views.SfButton
import com.bda.omnilibrary.views.SfTextView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_store.view.*

class StoreDialog(
    val activity: Activity,
    val product: Product
) {
    private var dialog: AlertDialog? = null

    private val image_category: AppCompatImageView
    private val profile_image: CircleImageView

    private val name: SfTextView
    private val text_content: SfTextView

    private val bn_back: SfButton

    private var mView = activity.layoutInflater.inflate(R.layout.dialog_store, null) as ViewGroup


    init {
        image_category = mView.image_category
        profile_image = mView.profile_image

        ImageUtils.loadImage(activity, image_category, product.imageCover, ImageUtils.TYPE_VIDEO)
        ImageUtils.loadImage(activity, profile_image, product.imageCover, ImageUtils.TYPE_PRIVIEW_SMALL)

        name = mView.name
        text_content = mView.text_content

        bn_back = mView.bn_back

        dialog = AlertDialog.Builder(activity)
            /*.setPositiveButton("Ok") { dialog, which -> }
            .setNegativeButton("Cannot") { dialog, which -> }*/
            .setOnCancelListener { }
            .create().apply {
                setView(mView)
                setCanceledOnTouchOutside(false)
                show()

                window?.setBackgroundDrawable(ColorDrawable(mView.resources.getColor(R.color.default_background_color)))
                window?.setGravity(Gravity.CENTER)
                window?.setLayout(
                    activity.resources.getDimensionPixelSize(R.dimen._219sdp),
                    activity.resources.getDimensionPixelSize(R.dimen._222sdp)
                )
            }

        dialog!!.setCancelable(false)

        bn_back.setOnClickListener {
            dismiss()
        }
    }

    fun dismiss() {
        dialog?.dismiss()
    }
}
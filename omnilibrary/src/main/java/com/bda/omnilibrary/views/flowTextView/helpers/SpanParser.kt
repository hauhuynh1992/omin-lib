package com.bda.omnilibrary.views.flowTextView.helpers

import android.graphics.Typeface
import android.text.Spannable
import android.text.TextPaint
import android.text.style.StyleSpan
import android.text.style.URLSpan
import com.bda.omnilibrary.views.flowTextView.FlowTextView
import com.bda.omnilibrary.views.flowTextView.models.HtmlLink
import com.bda.omnilibrary.views.flowTextView.models.HtmlObject


class SpanParser(private val mFlowTextView: FlowTextView, private val mPaintHelper: PaintHelper) {

    private val mLinks: ArrayList<HtmlLink> = ArrayList()
    private var mTextLength: Int = 0
    private lateinit var mSpannable: Spannable
    private val sorterMap: HashMap<Int, HtmlObject> = HashMap()

    fun parseSpans(
        objects: List<HtmlObject>,
        spans: List<Any>,
        lineStart: Int,
        lineEnd: Int,
        baseXOffset: Float,
    ): Float {
        sorterMap.clear()
        val charFlagSize = lineEnd - lineStart
        val charFlags = BooleanArray(charFlagSize)

        var tempString: String
        var spanStart: Int
        var spanEnd: Int
        var charCounter: Int

        for (span in spans) {
            spanStart = mSpannable.getSpanStart(span)
            spanEnd = mSpannable.getSpanEnd(span)
            if (spanStart < lineStart) spanStart = lineStart
            if (spanEnd > lineEnd) spanEnd = lineEnd
            charCounter = spanStart
            while (charCounter < spanEnd) {
                // mark these characters as rendered
                val charFlagIndex = charCounter - lineStart
                charFlags[charFlagIndex] = true
                charCounter++
            }
            tempString = extractText(spanStart, spanEnd)
            sorterMap[spanStart] = parseSpan(span, tempString, spanStart, spanEnd)
        }

        return 0f
    }

    private fun parseSpan(span: Any, content: String, start: Int, end: Int): HtmlObject {
        return when (span) {
            is URLSpan -> {
                getHtmlLink(span, content, start, end, 0f)
            }
            is StyleSpan -> {
                getStyledObject(span, content, start, end, 0f)
            }
            else -> {
                getHtmlObject(content, start, end, 0f)
            }
        }
    }

    private fun getStyledObject(
        span: StyleSpan,
        content: String,
        start: Int,
        end: Int,
        thisXOffset: Float,
    ): HtmlObject {
        val paint: TextPaint = mPaintHelper.getPaintFromHeap()
        paint.typeface = Typeface.defaultFromStyle(span.style)
        paint.textSize = mFlowTextView.getTextSize()
        paint.color = mFlowTextView.getColor()
        span.updateDrawState(paint)
        span.updateMeasureState(paint)
        val obj = HtmlObject(content, start, end, thisXOffset, paint)
        obj.recycle = true
        return obj
    }

    private fun getHtmlObject(
        content: String,
        start: Int,
        end: Int,
        thisXOffset: Float,
    ): HtmlObject {
        return HtmlObject(content, start, end, thisXOffset, mFlowTextView.getTextPaint())
    }

    fun reset() {
        mLinks.clear()
    }

    private fun getHtmlLink(
        span: URLSpan,
        content: String,
        start: Int,
        end: Int,
        thisXOffset: Float,
    ): HtmlLink {
        val obj = HtmlLink(content, start, end, thisXOffset, mFlowTextView.getLinkPaint()!!, span.url)
        mLinks.add(obj)
        return obj
    }

    fun addLink(thisLink: HtmlLink, yOffset: Float, width: Float, height: Float) {
        thisLink.yOffset = yOffset - 20
        thisLink.width = width
        thisLink.height = height + 20
        mLinks.add(thisLink)
    }

    private fun extractText(start: Int, end: Int): String {
        var start = start
        var end = end
        if (start < 0) start = 0
        if (end > mTextLength - 1) end = mTextLength - 1
        return mSpannable.subSequence(start, end).toString()
    }

    private fun isArrayFull(array: BooleanArray): Boolean {
        for (arrayIndex in array.indices) {
            if (!array[arrayIndex]) return false
        }
        return true
    }

    // GETTERS AND SETTERS
    fun getLinks(): List<HtmlLink?> {
        return mLinks
    }

    fun getSpannable(): Spannable {
        return mSpannable
    }

    fun setSpannable(mSpannable: Spannable) {
        this.mSpannable = mSpannable
        mTextLength = mSpannable.length
    }
}
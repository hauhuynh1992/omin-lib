package com.bda.omnilibrary.views.flowTextView

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.bda.omnilibrary.R
import com.bda.omnilibrary.views.flowTextView.helpers.ClickHandler
import com.bda.omnilibrary.views.flowTextView.helpers.CollisionHelper
import com.bda.omnilibrary.views.flowTextView.helpers.PaintHelper
import com.bda.omnilibrary.views.flowTextView.helpers.SpanParser
import com.bda.omnilibrary.views.flowTextView.models.HtmlLink
import com.bda.omnilibrary.views.flowTextView.models.HtmlObject
import com.bda.omnilibrary.views.flowTextView.models.Line
import com.bda.omnilibrary.views.flowTextView.models.Obstacle
import kotlin.math.roundToInt


class FlowTextView : RelativeLayout {

    // FIELDS
    private val mPaintHelper = PaintHelper()
    private val mSpanParser = SpanParser(this, mPaintHelper)
    private val mClickHandler = ClickHandler(mSpanParser)
    private var mColor: Int = Color.BLACK
    private var pageHeight = 0
    private var mTextPaint: TextPaint? = null
    private var mLinkPaint: TextPaint? = null
    private var mTextsize = resources.displayMetrics.scaledDensity * 10.0f
    private var mTextColor: Int = Color.BLACK
    private var typeFace: Typeface? = null
    private var mDesiredHeight = 100 // height of the whole view

    private var needsMeasure = true
    private val obstacles: ArrayList<Obstacle> = ArrayList()
    private var mText: CharSequence = ""
    private var mIsHtml = false

    private var mSpacingMult = 0f
    private var mSpacingAdd = 0f

    private var mMaximum = Int.MAX_VALUE

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context,
        attrs,
        defStyle) {
        init(context, attrs, defStyle)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {

        if (attrs != null) {
            readAttrs(context, attrs, defStyle)
        }

        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.density = resources.displayMetrics.density
        mTextPaint!!.textSize = mTextsize
        mTextPaint!!.color = mTextColor
        mLinkPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mLinkPaint!!.density = resources.displayMetrics.density
        mLinkPaint!!.textSize = mTextsize
        mLinkPaint!!.color = Color.BLUE
        mLinkPaint!!.isUnderlineText = true
        this.setBackgroundColor(Color.TRANSPARENT)

    }

    @SuppressLint("CustomViewStyleable")
    private fun readAttrs(context: Context, attrs: AttributeSet, defStyle: Int) {
        val ta = context.obtainStyledAttributes(attrs,
            R.styleable.flowText,
            defStyle,
            0)
        try {
            mSpacingAdd =
                ta.getDimensionPixelSize(R.styleable.flowText_lineSpacingExtra, 0)
                    .toFloat() // 0 is the index in the array, 0 is the default
            mSpacingMult = ta.getFloat(R.styleable.flowText_lineSpacingMultiplier,
                1.0f) // 1 is the index in the array, 1.0f is the default
            mTextsize =
                ta.getDimension(R.styleable.flowText_textSize,
                    mTextsize) // 2 is the index in the array of the textSize attribute
            mTextColor =
                ta.getColor(R.styleable.flowText_textColor,
                    Color.BLUE) // 3 is the index of the array of the textColor attribute
            mMaximum = ta.getInteger(R.styleable.flowText_maxLines, mMaximum)
        } finally {
            ta.recycle()
        }
    }

    var lineObjects: ArrayList<HtmlObject> = ArrayList()
    var htmlLine = HtmlObject("", 0, 0, 0f, null)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val mViewWidth = this.width.toFloat()
        obstacles.clear() // clear old data, boxes stores an array of "obstacles" that we need to paint the text around

        val lowestYCoord: Int =
            findBoxesAndReturnLowestObstacleYCoord() // find the "obstacles" within the view and get the lowest obstacle coordinate at the same time

        val blocks =
            mText.toString().split("\n").toTypedArray() // split the text into its natural blocks


        // set up some counter and helper variables we will us to traverse through the string to be rendered

        // set up some counter and helper variables we will us to traverse through the string to be rendered
        var charOffsetStart = 0 // tells us where we are in the original string

        var charOffsetEnd = 0 // tells us where we are in the original string

        var lineIndex = 0
        var xOffset: Float // left margin off a given line

        var maxWidth: Float // how far to the right it can stretch

        var yOffset = 0f
        var thisLineStr: String // the current line we are trying to render

        var chunkSize: Int
        val lineHeight: Int =
            getLineHeight() // get the height in pixels of a line for our current TextPaint

        //val paddingTop = paddingTop

        lineObjects.clear() // this will get populated with special html objects we need to render

        var spans: List<Any>

        mSpanParser.reset()

        for (element in blocks)  // at the highest level we iterate through each 'block' of text
        {
            var thisBlock = element
            if (thisBlock.isEmpty()) { //is a line break
                lineIndex++ // we need a new line
                charOffsetEnd += 2
                charOffsetStart = charOffsetEnd
            } else { // is some actual text
                while (thisBlock.isNotEmpty() && lineIndex < mMaximum) { // churn through the block spitting it out onto seperate lines until there is nothing left to render
                    lineIndex++ // we need a new line
                    yOffset =
                        paddingTop + lineIndex * lineHeight - (getLineHeight() + mTextPaint!!.fontMetrics.ascent) // calculate our new y position based on number of lines * line height
                    val thisLine: Line = CollisionHelper.calculateLineSpaceForGivenYOffset(yOffset,
                        lineHeight,
                        mViewWidth,
                        obstacles) // calculate a theoretical "line" space that we have to paint into based on the "obstacles" that exist at this yOffset and this line height - collision detection essentially
                    xOffset = thisLine.leftBound
                    maxWidth = thisLine.rightBound - thisLine.leftBound
                    var actualWidth: Float

                    // now we have a line of known maximum width that we can render to, figure out how many characters we can use to get that width taking into account html funkyness
                    do {
                        chunkSize = getChunk(thisBlock, maxWidth)
                        val thisCharOffset = charOffsetEnd + chunkSize
                        thisLineStr = if (chunkSize > 1) {
                            thisBlock.substring(0, chunkSize)
                        } else {
                            ""
                        }
                        lineObjects.clear()
                        if (mIsHtml) {
                            spans = (mText as Spanned).getSpans(charOffsetStart, thisCharOffset,
                                Any::class.java).toList()
                            actualWidth = if (spans.isNotEmpty()) {
                                mSpanParser.parseSpans(lineObjects,
                                    spans,
                                    charOffsetStart,
                                    thisCharOffset,
                                    xOffset)
                            } else {
                                maxWidth // if no spans then the actual width will be <= maxwidth anyway
                            }
                        } else {
                            actualWidth =
                                maxWidth // if not html then the actual width will be <= maxwidth anyway
                        }
                        if (actualWidth > maxWidth) {
                            maxWidth -= 5f // if we end up looping - start slicing chars off till we get a suitable size
                        }
                    } while (actualWidth > maxWidth)

                    // chunk is ok
                    charOffsetEnd += chunkSize
                    if (lineObjects.size <= 0) { // no funky objects found, add the whole chunk as one object
                        htmlLine.content = thisLineStr
                        htmlLine.start = 0
                        htmlLine.end = 0
                        htmlLine.xOffset = xOffset
                        htmlLine.paint = mTextPaint
                        lineObjects.add(htmlLine)
                    }
                    for (i in 0 until lineObjects.size) {
                        val thisHtmlObject = lineObjects[i]
                        if (i == lineObjects.size - 1 && lineIndex == mMaximum && thisBlock.substring(
                                chunkSize,
                                thisBlock.length).isNotEmpty()
                        ) {
                            thisHtmlObject.content =
                                thisHtmlObject.content.replaceRange(thisHtmlObject.content.length - 3,
                                    thisHtmlObject.content.length,
                                    "...")
                        }

                        if (thisHtmlObject is HtmlLink) {
                            val thisLink = thisHtmlObject
                            val thisLinkWidth = thisLink.paint!!.measureText(thisHtmlObject.content)
                            mSpanParser.addLink(thisLink,
                                yOffset,
                                thisLinkWidth,
                                lineHeight.toFloat())
                        }
                        paintObject(canvas!!,
                            thisHtmlObject.content,
                            thisHtmlObject.xOffset,
                            yOffset,
                            thisHtmlObject.paint!!)
                        if (thisHtmlObject.recycle) {
                            mPaintHelper.recyclePaint(thisHtmlObject.paint!!)
                        }
                    }
                    if (chunkSize >= 1) {
                        thisBlock = thisBlock.substring(chunkSize, thisBlock.length)
                    }
                    charOffsetStart = charOffsetEnd
                }
            }
        }

        yOffset += (lineHeight / 2).toFloat()

        val child: View? = getChildAt(childCount - 1)
        if (child?.tag != null && child.tag.toString()
                .equals("hideable", ignoreCase = true)
        ) {
            if (yOffset > pageHeight) {
                if (yOffset < obstacles[obstacles.size - 1].topLefty - getLineHeight()) {
                    child.visibility = View.GONE
                } else {
                    child.visibility = View.VISIBLE
                }
            } else {
                child.visibility = View.GONE
            }
        }

        mDesiredHeight = Math.max(lowestYCoord, yOffset.toInt())
        if (needsMeasure) {
            needsMeasure = false
            requestLayout()
        }

    }

    private fun findBoxesAndReturnLowestObstacleYCoord(): Int {
        var lowestYCoord = 0
        val childCount = this.childCount
        var layoutParams: LayoutParams
        for (i in 0 until childCount) {
            val child: View = getChildAt(i)
            if (child.visibility !== View.GONE) {
                layoutParams = child.layoutParams as LayoutParams
                val obstacle = Obstacle(0, 0, 0, 0)
                obstacle.topLeftx = child.left - layoutParams.leftMargin
                //val top: Int = child.top
                obstacle.topLefty = child.top
                obstacle.bottomRightx =
                    obstacle.topLeftx + layoutParams.leftMargin + child.width + layoutParams.rightMargin // padding should probably be included as well

                // todo hardcode magic number
                obstacle.bottomRighty =
                    obstacle.topLefty + (child.height * 2) / 3 + layoutParams.bottomMargin // padding should probably be included as well
                obstacles.add(obstacle)
                if (obstacle.bottomRighty > lowestYCoord) lowestYCoord = obstacle.bottomRighty
            }
        }

        return lowestYCoord
    }

    private fun getChunk(text: String, maxWidth: Float): Int {
        val length = mTextPaint!!.breakText(text, true, maxWidth, null)
        // if it's 0 or less, we can't fit any more chars on this line
        // if it's >= text length, everything fits, we're done
        // if the break character is a space, we're set
        if (length <= 0 || length >= text.length || text[length - 1] == ' ') {
            return length
        } else if (text.length > length && text[length] == ' ') {
            return length + 1 // or if the following char is a space then return this length - it is fine
        }

        // otherwise, count back until we hit a space and return that as the break length
        var tempLength = length - 1
        while (text[tempLength] != ' ') {
            tempLength--
            if (tempLength <= 0) return length // if we count all the way back to 0 then this line cannot be broken, just return the original break length
        }
        return tempLength + 1 // return the nicer break length which doesn't split a word up
    }

    private fun paintObject(
        canvas: Canvas,
        thisLineStr: String,
        xOffset: Float,
        yOffset: Float,
        paint: Paint,
    ) {
        canvas.drawText(thisLineStr, xOffset, yOffset, paint)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        this.invalidate()
    }

    override fun invalidate() {
        this.needsMeasure = true
        super.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        width = if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            widthSize
        } else {
            this.width
        }

        height = if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            heightSize
        } else {
            mDesiredHeight
        }

        setMeasuredDimension(width, height)
    }

    // GETTERS AND SETTERS
    // text size
    fun getTextSize(): Float {
        return mTextsize
    }

    fun setTextSize(textSize: Float) {
        mTextsize = textSize
        mTextPaint!!.textSize = mTextsize
        mLinkPaint!!.textSize = mTextsize
        invalidate()
    }

    fun getTextColor(): Int {
        return mTextColor
    }

    fun setTextColor(color: Int) {
        mTextColor = color
        mTextPaint!!.color = mTextColor
        invalidate()
    }

    // typeface
    fun getTypeFace(): Typeface? {
        return typeFace
    }

    fun setTypeface(type: Typeface?) {
        typeFace = type
        mTextPaint!!.typeface = typeFace
        mLinkPaint!!.typeface = typeFace
        invalidate()
    }

    // text paint
    fun getTextPaint(): TextPaint? {
        return mTextPaint
    }

    fun setTextPaint(mTextPaint: TextPaint?) {
        this.mTextPaint = mTextPaint
        invalidate()
    }

    // link paint
    fun getLinkPaint(): TextPaint? {
        return mLinkPaint
    }

    fun setLinkPaint(mLinkPaint: TextPaint?) {
        this.mLinkPaint = mLinkPaint
        invalidate()
    }

    // text content
    fun getText(): CharSequence? {
        return mText
    }

    fun setText(text: CharSequence) {
        mText = text
        if (text is Spannable) {
            mIsHtml = true
            mSpanParser.setSpannable(text)
        } else {
            mIsHtml = false
        }
        this.invalidate()
    }

    // text colour
    fun getColor(): Int {
        return mColor
    }

    fun setColor(color: Int) {
        mColor = color
        if (mTextPaint != null) {
            mTextPaint!!.color = mColor
        }
        mPaintHelper.setColor(mColor)
        this.invalidate()
    }

    // link click listener
    fun getOnLinkClickListener(): OnLinkClickListener? {
        return mClickHandler.getOnLinkClickListener()
    }

    fun setOnLinkClickListener(onLinkClickListener: OnLinkClickListener?) {
        mClickHandler.setOnLinkClickListener(onLinkClickListener)
    }

    // line height
    fun getLineHeight(): Int {
        return (mTextPaint!!.getFontMetricsInt(null) * mSpacingMult
                + mSpacingAdd).roundToInt()
    }

    // page height
    fun setPageHeight(pageHeight: Int) {
        this.pageHeight = pageHeight
        invalidate()
    }
}
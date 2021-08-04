package com.bda.omnilibrary.views.flowTextView.helpers

import com.bda.omnilibrary.views.flowTextView.models.Area
import com.bda.omnilibrary.views.flowTextView.models.Line
import com.bda.omnilibrary.views.flowTextView.models.Obstacle


object CollisionHelper {
    private val mAreas: ArrayList<Area> = ArrayList()

    fun calculateLineSpaceForGivenYOffset(
        lineYbottom: Float,
        lineHeight: Int,
        viewWidth: Float,
        obstacles: ArrayList<Obstacle>,
    ): Line {
        val line = Line(0f, 0f)
        line.leftBound = 0f
        line.rightBound = viewWidth
        val lineYtop = lineYbottom - lineHeight
        mAreas.clear()
        for (obstacle in obstacles) {
            if (obstacle.topLefty > lineYbottom || obstacle.bottomRighty < lineYtop) {
            } else {
                val leftArea = Area(0f, 0f, 0f)
                leftArea.x1 = 0f
                for (innerObstacle in obstacles) {
                    if (innerObstacle.topLefty > lineYbottom || innerObstacle.bottomRighty < lineYtop) {
                    } else {
                        if (innerObstacle.topLeftx < obstacle.topLeftx) {
                            leftArea.x1 = innerObstacle.bottomRightx.toFloat()
                        }
                    }
                }
                leftArea.x2 = obstacle.topLeftx.toFloat()
                leftArea.width = leftArea.x2 - leftArea.x1
                val rightArea = Area(0f, 0f, 0f)
                rightArea.x1 = obstacle.bottomRightx.toFloat()
                rightArea.x2 = viewWidth
                for (innerObstacle in obstacles) {
                    if (innerObstacle.topLefty > lineYbottom || innerObstacle.bottomRighty < lineYtop) {
                    } else {
                        if (innerObstacle.bottomRightx > obstacle.bottomRightx) {
                            rightArea.x2 = innerObstacle.topLeftx.toFloat()
                        }
                    }
                }
                rightArea.width = rightArea.x2 - rightArea.x1
                mAreas.add(leftArea)
                mAreas.add(rightArea)
            }
        }
        var mLargestArea: Area? = null
        if (mAreas.size > 0) { // if there is no areas then the whole line is clear, if there is areas, return the largest (it means there is one or more boxes colliding with this line)
            for (area in mAreas) {
                if (mLargestArea == null) {
                    mLargestArea = area
                } else {
                    if (area.width > mLargestArea.width) {
                        mLargestArea = area
                    }
                }
            }
            line.leftBound = mLargestArea!!.x1
            line.rightBound = mLargestArea.x2
        }
        return line
    }

}
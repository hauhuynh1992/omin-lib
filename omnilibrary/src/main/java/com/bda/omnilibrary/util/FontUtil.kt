package com.bda.omnilibrary.util

import android.content.res.AssetManager
import android.graphics.Typeface

@Suppress("unused")
object FontUtils {
    fun getFontSFLight(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Light.otf")
    }

    fun getFontSFRegular(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Regular.otf")
    }

    fun getFontSFHeavy(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Heavy.otf")
    }

    fun getFontSFBold(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Bold.otf")
    }

    fun getFontSFMedium(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Medium.otf")
    }

    fun getFontSFSemiBolt(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Display-Semibold.otf")
    }

    fun getFontSFItalic(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Text-LightItalic.otf")
    }

    fun getFontSFMediumText(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/SF-Pro-Text-Medium.otf")
    }

    fun getFontMulishLight(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Light.ttf")
    }

    fun getFontMulishRegular(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Regular.ttf")
    }

    fun getFontMulishHeavy(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-ExtraBold.ttf")
    }

    fun getFontMulishBold(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Bold.ttf")
    }

    fun getFontMulishMedium(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Medium.ttf")
    }

    fun getFontMulishSemiBolt(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-SemiBold.ttf")
    }

    fun getFontMulishItalic(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Italic.ttf")
    }

    fun getFontMulishMediumText(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Mulish-Medium.ttf")
    }


    fun getFontLatoLight(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Light.ttf")
    }

    fun getFontLatoRegular(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Regular.ttf")
    }

    fun getFontLatoHeavy(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Heavy.ttf")
    }

    fun getFontLatoBold(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Bold.ttf")
    }

    fun getFontLatoMedium(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Medium.ttf")
    }

    fun getFontLatoSemiBolt(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Semibold.ttf")
    }

    fun getFontLatoItalic(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Italic.ttf")
    }

    fun getFontLatoMediumText(mAssetManager: AssetManager): Typeface {
        return Typeface.createFromAsset(mAssetManager, "fonts/Lato-Medium.ttf")
    }
}
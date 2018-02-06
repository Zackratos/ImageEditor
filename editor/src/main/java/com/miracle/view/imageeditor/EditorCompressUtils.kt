package com.miracle.view.imageeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.R.attr.y
import android.os.Build
import android.content.Context.WINDOW_SERVICE
import android.view.WindowManager
import android.R.attr.x
import android.content.Context
import android.graphics.Point


/**
 * Created by lxw
 * @see https://github.com/Curzibn/Luban/blob/master/library/src/main/java/top/zibin/luban/Engine.java
 */
object EditorCompressUtils {

    private fun computeSize(inputWidth: Int, inputHeight: Int): Int {
        val mSampleSize: Int
        var srcWidth = if (inputWidth % 2 == 1) inputWidth + 1 else inputWidth
        var srcHeight = if (inputHeight % 2 == 1) inputHeight + 1 else inputHeight
        srcWidth = if (srcWidth > srcHeight) srcHeight else srcWidth
        srcHeight = if (srcWidth > srcHeight) srcWidth else srcHeight
        val scale = srcWidth * 1.0 / srcHeight
        if (scale <= 1 && scale > 0.5625) {
            if (srcHeight < 1664) {
                mSampleSize = 1
            } else if (srcHeight in 1666 until 4990) {
                mSampleSize = 2
            } else if (srcHeight in 4990 until 10240) {
                mSampleSize = 4
            } else {
                mSampleSize = if (srcHeight / 1280 == 0) 1 else srcHeight / 1280
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            mSampleSize = if (srcHeight / 1280 == 0) 1 else srcHeight / 1280
        } else {
            mSampleSize = Math.ceil(srcHeight / (1280.0 / scale)).toInt()
        }
        return mSampleSize
    }

    fun getImageBitmap(context: Context, filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
//        val outWidth = options.outWidth
//        val outHeight = options.outHeight
//        options.inSampleSize = computeSize(outWidth, outHeight)*2
        options.inSampleSize = calculateInSampleSize(options, getScreenWidth(context), getScreenHeight(context))
        options.inJustDecodeBounds = false
        logD1("options.inSampleSize=${options.inSampleSize}")
        return BitmapFactory.decodeFile(filePath, options)
    }


    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        // 源图片的高度和宽度
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }


    /**
     * 获取屏幕的宽度（单位：px）
     *
     * @return 屏幕宽
     */
    private fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) ?: return context.resources.displayMetrics.heightPixels
        wm as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.x
    }

    /**
     * 获取屏幕的高度（单位：px）
     *
     * @return 屏幕高
     */
    private fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) ?: return context.resources.displayMetrics.widthPixels
        wm as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        return point.y
    }

}
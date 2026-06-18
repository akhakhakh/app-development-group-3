package com.group3.camera

import android.graphics.Bitmap
import androidx.annotation.DrawableRes

object GameResultStore {

    data class CapturedResult(
        val sequenceIndex: Int,
        val bitmap: Bitmap,
        val expressionName: String,
        @DrawableRes val drawableId: Int,
    )

    private val _results = mutableListOf<CapturedResult>()
    val results: List<CapturedResult> get() = _results

    fun clear() = _results.clear()

    fun add(sequenceIndex: Int, bitmap: Bitmap, expressionName: String, @DrawableRes drawableId: Int) {
        _results.add(CapturedResult(sequenceIndex, bitmap, expressionName, drawableId))
    }
}

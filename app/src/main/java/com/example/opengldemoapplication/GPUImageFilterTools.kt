/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.opengldemoapplication

import android.app.AlertDialog
import android.content.Context
import android.graphics.BitmapFactory
import com.example.opengldemoapplication.GPUImageFilterTools.FilterType.*
import jp.co.cyberagent.android.gpuimage.filter.*
import java.util.*

object GPUImageFilterTools {
    @JvmStatic
    fun showDialog(
            context: Context,
            listener: (filter: GPUImageFilter) -> Unit
    ) {
        val filters = FilterList().apply {
            addFilter("Invert", INVERT)
            addFilter("Pixelation", PIXELATION)
            addFilter("Hue", HUE)
            addFilter("Swirl", SWIRL)
            addFilter("glitch01", GLITCH01)
        }

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
        builder.setItems(filters.names.toTypedArray()) { _, item ->
            listener(createFilterForType(context, filters.filters[item]))
        }
        builder.create().show()
    }

    private fun createFilterForType(context: Context, type: FilterType): GPUImageFilter {
        return when (type) {
            INVERT -> GPUImageColorInvertFilter()
            PIXELATION -> GPUImagePixelationFilter()
            HUE -> GPUImageHueFilter(90.0f)
            SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetectionFilter()
            SWIRL -> GPUImageSwirlFilter()
            GLITCH01 -> GPUImageGlitch01()
            GLITCH02 -> TODO()
            BLACKWHITE -> TODO()
            DISTOTION -> TODO()
            INTERFERENCE -> TODO()
            TAPE -> TODO()
            TRACKING -> TODO()
        }
    }

    private fun createBlendFilter(
            context: Context,
            filterClass: Class<out GPUImageTwoInputFilter>
    ): GPUImageFilter {
        return try {
            filterClass.newInstance().apply {
                bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher_background)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            GPUImageFilter()
        }
    }

    private enum class FilterType {
        HUE, GLITCH01, GLITCH02, BLACKWHITE, DISTOTION, INTERFERENCE, TAPE, TRACKING, INVERT, PIXELATION, SOBEL_EDGE_DETECTION,
        SWIRL
    }

    private class FilterList {
        val names: MutableList<String> = LinkedList()
        val filters: MutableList<FilterType> = LinkedList()

        fun addFilter(name: String, filter: FilterType) {
            names.add(name)
            filters.add(filter)
        }
    }

    class FilterAdjuster(filter: GPUImageFilter) {
        private val adjuster: Adjuster<out GPUImageFilter>?

        init {
            adjuster = when (filter) {
                is GPUImageSobelEdgeDetectionFilter -> SobelAdjuster(filter)
                is GPUImageHueFilter -> HueAdjuster(filter)
                is GPUImageSwirlFilter -> SwirlAdjuster(filter)
                is GPUImageGlitch01 -> Glitch01Adjuster(filter)
                else -> null
            }
        }

        fun canAdjust(): Boolean {
            return adjuster != null
        }

        fun adjust(percentage: Int) {
            adjuster?.adjust(percentage)
        }

        private abstract inner class Adjuster<T : GPUImageFilter>(protected val filter: T) {

            abstract fun adjust(percentage: Int)

            protected fun range(percentage: Int, start: Float, end: Float): Float {
                return (end - start) * percentage / 100.0f + start
            }

            protected fun range(percentage: Int, start: Int, end: Int): Int {
                return (end - start) * percentage / 100 + start
            }
        }

        private inner class HueAdjuster(filter: GPUImageHueFilter) :
                Adjuster<GPUImageHueFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setHue(range(percentage, 0.0f, 360f))
            }
        }

        private inner class Glitch01Adjuster(filter: GPUImageGlitch01) :
                Adjuster<GPUImageGlitch01>(filter) {
            override fun adjust(percentage: Int) {
                filter.setGlitch01(range(percentage, -2.0f, 2.0f))
            }
        }

        private inner class SobelAdjuster(filter: GPUImageSobelEdgeDetectionFilter) :
                Adjuster<GPUImageSobelEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class SwirlAdjuster(filter: GPUImageSwirlFilter) :
                Adjuster<GPUImageSwirlFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setAngle(range(percentage, 0.0f, 2.0f))
            }
        }
    }
}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scandit.datacapture.listbuildingsamplecompose

import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.scandit.datacapture.barcode.data.Barcode
import com.scandit.datacapture.barcode.data.Symbology
import com.scandit.datacapture.barcode.spark.capture.SparkScan
import com.scandit.datacapture.barcode.spark.capture.SparkScanListener
import com.scandit.datacapture.barcode.spark.capture.SparkScanSession
import com.scandit.datacapture.barcode.spark.capture.SparkScanSettings
import com.scandit.datacapture.barcode.spark.feedback.SparkScanBarcodeFeedback
import com.scandit.datacapture.barcode.spark.feedback.SparkScanFeedbackDelegate
import com.scandit.datacapture.barcode.spark.ui.SparkScanCoordinatorLayout
import com.scandit.datacapture.barcode.spark.ui.SparkScanView
import com.scandit.datacapture.barcode.spark.ui.SparkScanViewSettings
import com.scandit.datacapture.core.capture.DataCaptureContext
import com.scandit.datacapture.core.data.FrameData
import com.scandit.datacapture.core.time.TimeInterval
import com.scandit.datacapture.listbuildingsamplecompose.data.rememberDataCaptureContext

@Composable
private fun rememberSparkScanView(
    parentView: ViewGroup,
    sparkScan: SparkScan,
    dataCaptureContext: DataCaptureContext,
): SparkScanView {

    // You can customize the SparkScanView using SparkScanViewSettings.
    val sparkScanViewSettings = remember {
        SparkScanViewSettings()
    }

    // Creating the instance of SparkScanView. The instance will be automatically added
    // to the container.
    val sparkScanView = remember(sparkScan, parentView, dataCaptureContext) {
        SparkScanView.newInstance(parentView, dataCaptureContext, sparkScan, sparkScanViewSettings)
    }

    return sparkScanView
}

@Composable
private fun rememberSparkScanSettings(): SparkScanSettings {
    // The spark scan process is configured through SparkScan settings
    // which are then applied to the spark scan instance that manages the spark scan.
    val sparkScanSettings = remember {
        SparkScanSettings().apply {
            // The settings instance initially has all types of barcodes (symbologies) disabled.
            // For the purpose of this sample we enable a very generous set of symbologies.
            // In your own app ensure that you only enable the symbologies that your app requires
            // as every additional enabled symbology has an impact on processing times.
            val symbologies = setOf(
                Symbology.EAN13_UPCA,
                Symbology.EAN8,
                Symbology.UPCE,
                Symbology.CODE39,
                Symbology.CODE128,
                Symbology.INTERLEAVED_TWO_OF_FIVE
            )
            enableSymbologies(symbologies)

            // Some linear/1d barcode symbologies allow you to encode variable-length data.
            // By default, the Scandit Data Capture SDK only scans barcodes in a certain length range.
            // If your application requires scanning of one of these symbologies, and the length is
            // falling outside the default range, you may need to adjust the "active symbol counts"
            // for this symbology. This is shown in the following few lines of code for one of the
            // variable-length symbologies.
            val symbologySettings = getSymbologySettings(Symbology.CODE39)
            symbologySettings.activeSymbolCounts =
                setOf(7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        }
    }

    return sparkScanSettings
}

@Composable
private fun rememberSparkScan(
    settings: SparkScanSettings
): SparkScan {
    // Create the spark scan instance.
    // Spark scan will automatically apply and maintain the optimal camera settings.
    val sparkScan = remember(settings) {
        SparkScan(settings)
    }
    return sparkScan
}

private fun Barcode?.isValid(): Boolean {
    return this?.data != null && this.data != "123456789"
}

@Composable
internal fun SparkScanComponent(
    padding: PaddingValues,
    onValidBarcodeScanned: (barcode: Barcode, data: FrameData?) -> Unit
) {
    val context = LocalContext.current

    val sparkScanParent = remember(context) { SparkScanCoordinatorLayout(context) }
    val dataCaptureContext = rememberDataCaptureContext()
    val sparkScanSettings = rememberSparkScanSettings()
    val sparkScan = rememberSparkScan(sparkScanSettings)
    val sparkScanView = rememberSparkScanView(
        parentView = sparkScanParent,
        sparkScan = sparkScan,
        dataCaptureContext = dataCaptureContext,
    )

    // Set up feedback delegate
    DisposableEffect(sparkScanView) {
        val feedbackDelegate = object : SparkScanFeedbackDelegate {
            override fun getFeedbackForBarcode(barcode: Barcode): SparkScanBarcodeFeedback? {
                return if (barcode.isValid()) {
                    SparkScanBarcodeFeedback.Success()
                } else {
                    SparkScanBarcodeFeedback.Error(
                        context.getString(R.string.wrong_barcode),
                        TimeInterval.seconds(60f)
                    )
                }
            }
        }
        sparkScanView.feedbackDelegate = feedbackDelegate

        onDispose {
            sparkScanView.feedbackDelegate = null
        }
    }

    // Register self as a listener to get informed of tracked barcodes.
    DisposableEffect(sparkScan) {
        val listener = object : SparkScanListener {

            override fun onBarcodeScanned(
                sparkScan: SparkScan,
                session: SparkScanSession,
                data: FrameData?
            ) {
                val barcode = session.newlyRecognizedBarcode

                if (barcode?.isValid() == true) {
                    onValidBarcodeScanned(barcode, data)
                }
            }
        }
        sparkScan.addListener(listener)

        onDispose {
            sparkScan.removeListener(listener)
        }
    }

    LifecycleResumeEffect(sparkScanView) {
        sparkScanView.onResume()
        onPauseOrDispose {
            sparkScanView.onPause()
        }
    }

    AndroidView(
        modifier = Modifier.padding(padding),
        factory = { _ ->
            sparkScanParent
        }
    )
}

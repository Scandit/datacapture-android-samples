/*
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

package com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture

import android.content.Context
import com.scandit.datacapture.id.capture.IdCaptureDocument
import com.scandit.datacapture.id.capture.RegionSpecific
import com.scandit.datacapture.id.capture.description
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.id.data.IdCaptureRegion
import com.scandit.datacapture.id.data.RegionSpecificSubtype
import com.scandit.datacapture.idcapturesettingssample.R
import com.scandit.datacapture.idcapturesettingssample.model.PhysicalDocumentScannerType
import com.scandit.datacapture.idcapturesettingssample.model.Settings
import com.scandit.datacapture.idcapturesettingssample.utils.toNameCase

fun getAnonymizationSummary(settings: Settings): String {
    val modeName = settings.idCapture.anonymizationMode.name.toNameCase()
    val selectedFieldCount = settings.idCapture.anonymizedFields.size

    val fieldsSummary = when (selectedFieldCount) {
        0 -> "No fields"
        1 -> "1 field"
        else -> "$selectedFieldCount fields"
    }

    return "$modeName | $fieldsSummary"
}

fun getAcceptedDocumentsSummary(context: Context, settings: Settings): String =
    getDocumentsSummary(context, settings.idCapture.acceptedDocuments)

fun getRejectedDocumentsSummary(context: Context, settings: Settings): String =
    getDocumentsSummary(context, settings.idCapture.rejectedDocuments)

private fun getDocumentsSummary(
    context: Context,
    selectedDocuments: List<IdCaptureDocument>,
): String {
    if (selectedDocuments.isEmpty()) {
        return context.getString(R.string.settings_value_none)
    }

    val documentsByType = selectedDocuments.groupBy { it.documentType }
    val sortedTypes = documentsByType.keys.sortedBy { it.name }

    return sortedTypes.joinToString("\n") { documentType ->
        val documents = documentsByType[documentType] ?: emptyList()
        val details = getSummaryForDocumentType(context, documentType, documents)
        "${documentType.name.toNameCase()} ($details)"
    }
}

fun getSummaryForDocumentType(
    context: Context,
    type: IdCaptureDocumentType,
    selectedDocuments: List<IdCaptureDocument>,
): String = if (type == IdCaptureDocumentType.REGION_SPECIFIC) {
    val subtypes = selectedDocuments
        .filterIsInstance<RegionSpecific>()
        .map { it.subtype }
    formatSubtypesSummary(context, subtypes)
} else {
    val regions = selectedDocuments
        .filter { it.documentType == type }
        .map { it.region }
    formatRegionsSummary(context, regions)
}

fun formatRegionsSummary(
    context: Context,
    regions: List<IdCaptureRegion>,
): String = when {
    regions.isEmpty() -> context.getString(R.string.settings_value_none)
    IdCaptureRegion.ANY in regions -> context.getString(R.string.id_capture_any_region)
    else -> regions
        .sortedBy { it.description }
        .joinToString { it.description }
}

fun formatSubtypesSummary(
    context: Context,
    subtypes: List<RegionSpecificSubtype>,
): String = when {
    subtypes.isEmpty() -> context.getString(R.string.settings_value_none)
    subtypes.size == RegionSpecificSubtype.entries.size ->
        context.getString(R.string.id_capture_any_subtype)

    else -> subtypes
        .sorted()
        .joinToString { it.name.toNameCase() }
}

fun getScannerSummary(context: Context, settings: Settings): String {
    val summaryParts = mutableListOf<String>()

    val scannerType = settings.idCapture.physicalDocumentScannerType

    when (scannerType) {
        PhysicalDocumentScannerType.SINGLE_SIDE -> {
            val physicalOptions = mutableListOf<String>()
            if (settings.idCapture.singleSideScannerBarcodeEnabled) {
                physicalOptions.add(context.getString(R.string.single_side_scanner_barcode))
            }
            if (settings.idCapture.singleSideScannerMrzEnabled) {
                physicalOptions.add(context.getString(R.string.single_side_scanner_mrz))
            }
            if (settings.idCapture.singleSideScannerVizEnabled) {
                physicalOptions.add(context.getString(R.string.single_side_scanner_viz))
            }

            if (physicalOptions.isNotEmpty()) {
                summaryParts.add(
                    context.getString(
                        R.string.scanner_summary_physical,
                        physicalOptions.joinToString()
                    )
                )
            } else {
                summaryParts.add(
                    context.getString(
                        R.string.scanner_summary_physical,
                        context.getString(R.string.physical_document_scanner_single_side_type)
                    )
                )
            }
        }

        PhysicalDocumentScannerType.FULL -> {
            summaryParts.add(
                context.getString(
                    R.string.scanner_summary_physical,
                    context.getString(R.string.physical_document_scanner_full_type)
                )
            )
        }
    }

    val mobileOptions = mutableListOf<String>()
    if (settings.idCapture.mobileScannerIso1801315Enabled) {
        mobileOptions.add(context.getString(R.string.mobile_scanner_iso_18013_15))
    }
    if (settings.idCapture.mobileScannerOcrEnabled) {
        mobileOptions.add(context.getString(R.string.mobile_scanner_ocr))
    }

    if (mobileOptions.isNotEmpty()) {
        summaryParts.add(
            context.getString(
                R.string.scanner_summary_mobile,
                mobileOptions.joinToString()
            )
        )
    }

    return summaryParts.joinToString(" | ")
}

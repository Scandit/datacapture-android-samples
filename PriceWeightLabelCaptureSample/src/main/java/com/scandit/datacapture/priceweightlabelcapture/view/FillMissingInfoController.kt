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
package com.scandit.datacapture.priceweightlabelcapture.view

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.scandit.datacapture.priceweightlabelcapture.data.PartialLabel
import com.scandit.datacapture.priceweightlabelcapture.data.ScannedResult
import com.scandit.datacapture.priceweightlabelcapture.databinding.FragmentScanBinding

class FillMissingInfoController(
    private val binding: FragmentScanBinding,
    private val onDismiss: () -> Unit,
    onSlide: (slideOffset: Float) -> Unit
) : BottomSheetController {
    private var dismissed = true

    private val behaviorSheet =
        BottomSheetBehavior.from(binding.fillMissingInfoContainer).apply {
            isHideable = true
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        if (dismissed) {
                            onDismiss()
                        }
                        dismissed = true
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    onSlide(slideOffset)
                }
            })
            state = BottomSheetBehavior.STATE_HIDDEN
        }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit

        override fun afterTextChanged(s: Editable) {
            binding.submitButton.isEnabled = areInputsValid
        }
    }

    override val isHidden get() = behaviorSheet.state == BottomSheetBehavior.STATE_HIDDEN

    override fun hide(dismissed: Boolean) {
        if (behaviorSheet.state == BottomSheetBehavior.STATE_HIDDEN) {
            if (dismissed) {
                onDismiss()
            }

            return
        }

        this.dismissed = dismissed
        behaviorSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun show(partialLabel: PartialLabel, submit: (label: ScannedResult.Label) -> Unit) {
        bindPartialLabel(partialLabel, submit)

        behaviorSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun bindPartialLabel(
        partialLabel: PartialLabel,
        submit: (label: ScannedResult.Label) -> Unit
    ) {
        binding.upcContent.text = partialLabel.data
        binding.weightEditor.setText(partialLabel.weight)
        binding.unitPriceEditor.setText(partialLabel.unitPrice)
        binding.weightEditor.addTextChangedListener(textWatcher)
        binding.unitPriceEditor.addTextChangedListener(textWatcher)

        when {
            partialLabel.weight.isNullOrBlank() -> {
                binding.weightEditor.imeOptions = EditorInfo.IME_ACTION_NEXT
                showKeyboard(binding.weightEditor)
            }
            partialLabel.unitPrice.isNullOrBlank() -> {
                binding.weightEditor.imeOptions = EditorInfo.IME_ACTION_NEXT
                showKeyboard(binding.unitPriceEditor)
            }
            else -> binding.weightEditor.imeOptions = EditorInfo.IME_ACTION_DONE
        }

        val editorAction = TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && areInputsValid) {
                submitLabel(partialLabel, submit)
                true
            } else {
                false
            }
        }
        binding.weightEditor.setOnEditorActionListener(editorAction)
        binding.unitPriceEditor.setOnEditorActionListener(editorAction)

        binding.submitButton.isEnabled = areInputsValid
        binding.submitButton.setOnClickListener {
            submitLabel(partialLabel, submit)
        }
        binding.dismissButton.setOnClickListener {
            hide()
        }
    }

    private fun submitLabel(
        partialLabel: PartialLabel,
        submit: (label: ScannedResult.Label) -> Unit
    ) {
        val finalLabel = ScannedResult.Label(
            data = partialLabel.data,
            weight = binding.weightEditor.text.toString(),
            unitPrice = binding.unitPriceEditor.text.toString()
        )
        closeKeyboard()
        submit(finalLabel)
    }

    private fun closeKeyboard() {
        binding.root.context.getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        binding.root.context.getSystemService(InputMethodManager::class.java)
            ?.showSoftInput(view, 0)
    }

    private val areInputsValid
        get() = binding.weightEditor.text.isNotBlank() &&
            binding.unitPriceEditor.text.isNotBlank()
}

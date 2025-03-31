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
package com.scandit.datacapture.priceweightlabelcapture.view.result

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.scandit.datacapture.priceweightlabelcapture.R
import com.scandit.datacapture.priceweightlabelcapture.data.ScanViewModel
import com.scandit.datacapture.priceweightlabelcapture.databinding.FragmentResultBinding
import kotlinx.coroutines.launch

class ResultFragment : BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<ScanViewModel>()
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private val resultAdapter = ResultAdapter()
    var onDismiss: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.resultList.let { resultList ->
            resultList.adapter = resultAdapter
            resultList.layoutManager = LinearLayoutManager(resultList.context)
            resultList.addItemDecoration(DividerDecorationWithNoLastLine(resultList.context))
        }
        binding.clear.setOnClickListener {
            viewModel.clearResults()
        }
        binding.continueScan.setOnClickListener {
            dismiss()
        }

        (dialog as? BottomSheetDialog)?.behavior?.apply {
            peekHeight =
                PEEK_HEIGHT_IN_DP * requireContext().resources.displayMetrics.density.toInt()
            skipCollapsed = false
        }

        // Make bottomSheet expand to full screen.
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.layoutParams?.height =
            ViewGroup.LayoutParams.MATCH_PARENT

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.results.collect { results ->
                    binding.emptyList.isVisible = results.isEmpty()
                    binding.clear.isInvisible = results.isEmpty()

                    resultAdapter.setResults(results)
                    binding.itemScanCount.text =
                        resources.getQuantityString(
                            R.plurals.x_items_scanned,
                            results.size,
                            results.size
                        )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

    companion object {
        private const val PEEK_HEIGHT_IN_DP = 264
        const val TAG = "ResultFragment"
    }
}

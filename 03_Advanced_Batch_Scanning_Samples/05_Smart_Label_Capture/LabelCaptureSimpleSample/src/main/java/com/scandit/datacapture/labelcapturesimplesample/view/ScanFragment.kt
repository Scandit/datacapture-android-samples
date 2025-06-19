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
package com.scandit.datacapture.labelcapturesimplesample.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.control.TorchSwitchControl
import com.scandit.datacapture.label.ui.overlay.validation.LabelCaptureValidationFlowOverlay
import com.scandit.datacapture.labelcapturesimplesample.data.ScanViewModel
import com.scandit.datacapture.labelcapturesimplesample.databinding.FragmentScanBinding
import kotlinx.coroutines.launch

class ScanFragment : Fragment() {
    private val viewModel by viewModels<ScanViewModel>()

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataCaptureView: DataCaptureView
    private lateinit var validationFlowOverlay: LabelCaptureValidationFlowOverlay

    private var currentDialog: DialogFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root

        val labelCaptureOverlay = viewModel.buildLabelCaptureOverlay(requireContext())
        validationFlowOverlay = viewModel.getValidationFlowOverlay(requireContext())

        // Creating dataCaptureView and adding it to the container
        dataCaptureView = DataCaptureView.newInstance(view.context, viewModel.dataCaptureContext)
        dataCaptureView.addControl(TorchSwitchControl(requireContext()))
        dataCaptureView.addOverlay(labelCaptureOverlay)
        dataCaptureView.addOverlay(validationFlowOverlay)
        binding.dataCaptureViewContainer.addView(
            dataCaptureView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                for (message in viewModel.messages) {
                    showLabelCapturedDialog(message)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun showLabelCapturedDialog(message: String) {
        if (parentFragmentManager.findFragmentByTag(LabelCapturedFragment.TAG) == null) {
            currentDialog = LabelCapturedFragment.newInstance(message).also {
                it.onDismiss = ::onDialogDismissed
            }
            currentDialog?.show(parentFragmentManager, LabelCapturedFragment.TAG)
        }
    }

    private fun closeKeyboard() {
        binding.root.context.getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun onDialogDismissed() {
        closeKeyboard()

        viewModel.onDialogDismissed()
    }

    companion object {
        fun newInstance(): ScanFragment = ScanFragment()
    }
}

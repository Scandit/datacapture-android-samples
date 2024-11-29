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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.scandit.datacapture.core.common.feedback.Feedback
import com.scandit.datacapture.core.ui.DataCaptureView
import com.scandit.datacapture.core.ui.control.TorchSwitchControl
import com.scandit.datacapture.priceweightlabelcapture.data.ScanViewModel
import com.scandit.datacapture.priceweightlabelcapture.data.UiState
import com.scandit.datacapture.priceweightlabelcapture.databinding.FragmentScanBinding
import com.scandit.datacapture.priceweightlabelcapture.view.result.ResultFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScanFragment : Fragment() {
    private val viewModel by activityViewModels<ScanViewModel>()

    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataCaptureView: DataCaptureView
    private val feedback = Feedback.defaultFeedback()

    private lateinit var fillMissingInfoController: FillMissingInfoController

    private val fillMissingInfoOffset = MutableStateFlow(-1f)
    private val dialogsVisibility: Flow<Float> =
        fillMissingInfoOffset.map { (1f + it).coerceIn(0f, 1f) }

    private var resultFragment: ResultFragment? = null

    private val onBackPressed = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeDialogs(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        val view = binding.root

        // Creating dataCaptureView and adding it to the container
        dataCaptureView = DataCaptureView.newInstance(view.context, viewModel.dataCaptureContext)
        dataCaptureView.addControl(TorchSwitchControl(requireContext()))
        dataCaptureView.addOverlay(viewModel.labelCaptureOverlay)
        binding.dataCaptureViewContainer.addView(
            dataCaptureView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        binding.touchHandler.setOnClickListener {
            onTap()
        }

        fillMissingInfoController = FillMissingInfoController(
            binding = binding,
            onDismiss = ::onTap,
            onSlide = { slideOffset ->
                fillMissingInfoOffset.value = slideOffset
            }
        )

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressed)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.guidanceText.collect { guidanceText ->
                        if (guidanceText != null) {
                            binding.guidance.text = guidanceText
                            binding.guidance.visibility = View.VISIBLE
                        } else {
                            binding.guidance.visibility = View.GONE
                        }
                    }
                }
                launch {
                    viewModel.onLabelScanned.collect {
                        withContext(Dispatchers.Main) {
                            feedback.emit()
                        }
                    }
                }
                launch {
                    viewModel.messages.collect { message ->
                        binding.infoHint.isInvisible = message == null
                        message?.let { binding.infoHint.text = it.text }
                    }
                }

                launch {
                    viewModel.dialogState.collect { uiState ->
                        when (uiState) {
                            UiState.NoDialog -> closeDialogs(false)
                            UiState.CompleteLabelDialog -> showFillInfoDialog()
                            UiState.ShowResultsDialog -> showResultsDialog()
                        }
                    }
                }
                launch {
                    dialogsVisibility.collect { alpha ->
                        binding.touchHandler.alpha = alpha
                        binding.touchHandler.isClickable = alpha > 0
                        onBackPressed.isEnabled = alpha > 0
                    }
                }
                viewModel.run()
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

    private fun closeDialogs(dismiss: Boolean) {
        fillMissingInfoController.hide(dismiss)
        resultFragment?.dismiss()
    }

    private fun showFillInfoDialog() {
        val partialLabel = viewModel.incompleteLabel.value ?: return

        fillMissingInfoController.show(partialLabel) { label ->
            viewModel.submitLabel(label)
        }
        fillMissingInfoOffset.value = 1f

        resultFragment?.dismiss()
    }

    private fun showResultsDialog() {
        fillMissingInfoController.hide(dismissed = false)
        if (parentFragmentManager.findFragmentByTag(ResultFragment.TAG) == null) {
            resultFragment = ResultFragment()
            resultFragment?.onDismiss = ::onTap
            resultFragment?.show(parentFragmentManager, ResultFragment.TAG)
        }
    }

    private fun closeKeyboard() {
        binding.root.context.getSystemService(InputMethodManager::class.java)
            ?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun onTap() {
        closeKeyboard()

        viewModel.onTap()
    }

    companion object {
        fun newInstance(): ScanFragment = ScanFragment()
    }
}

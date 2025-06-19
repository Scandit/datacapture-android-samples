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

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.scandit.datacapture.core.internal.sdk.extensions.displayDensity
import com.scandit.datacapture.labelcapturesimplesample.databinding.FragmentLabelCapturedBinding

class LabelCapturedFragment : DialogFragment() {
    private var _binding: FragmentLabelCapturedBinding? = null
    private val binding get() = _binding!!

    var onDismiss: () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLabelCapturedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.message.text = arguments?.getString(ARG_MESSAGE)
        binding.continueButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dialog ->
            dialog.window?.setGravity(Gravity.CENTER)
            val attributes = dialog.window?.attributes
            attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.attributes = attributes
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    companion object {
        private const val ARG_MESSAGE = "message"
        const val TAG = "LabelCapturedFragment"

        fun newInstance(message: String): LabelCapturedFragment {
            return LabelCapturedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_MESSAGE, message)
                }
            }
        }

        fun Float.pxFromDp(context: Context): Float {
            val displayDensity = context.displayDensity
            return this * displayDensity
        }
    }
}

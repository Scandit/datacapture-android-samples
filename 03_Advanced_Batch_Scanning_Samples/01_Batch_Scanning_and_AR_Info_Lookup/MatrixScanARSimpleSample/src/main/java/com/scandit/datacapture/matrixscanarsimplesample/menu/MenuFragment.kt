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

package com.scandit.datacapture.matrixscanarsimplesample.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.scandit.datacapture.matrixscanarsimplesample.R
import com.scandit.datacapture.matrixscanarsimplesample.models.BarcodeArMode
import com.scandit.datacapture.matrixscanarsimplesample.scan.ScanFragment

class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.button_highlights).setOnClickListener {
            navigateToMode(BarcodeArMode.Highlights)
        }
        view.findViewById<View>(R.id.button_annotations).setOnClickListener {
            navigateToMode(BarcodeArMode.Annotations)
        }
        view.findViewById<View>(R.id.button_popovers).setOnClickListener {
            navigateToMode(BarcodeArMode.Popovers)
        }
        view.findViewById<View>(R.id.button_status_icons).setOnClickListener {
            navigateToMode(BarcodeArMode.StatusIcons)
        }
    }

    private fun navigateToMode(mode: BarcodeArMode) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, ScanFragment.newInstance(mode))
            .addToBackStack(null)
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MenuFragment()
    }
}

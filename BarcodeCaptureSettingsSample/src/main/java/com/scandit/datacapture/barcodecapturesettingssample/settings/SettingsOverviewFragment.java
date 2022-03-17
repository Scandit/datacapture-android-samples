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

package com.scandit.datacapture.barcodecapturesettingssample.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.scandit.datacapture.barcodecapturesettingssample.R;
import com.scandit.datacapture.barcodecapturesettingssample.base.NavigationFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.barcodecapture.BarcodeCaptureSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.camera.CameraSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.resulthandling.ResultHandlingSettingsFragment;
import com.scandit.datacapture.barcodecapturesettingssample.settings.view.ViewSettingsFragment;
import com.scandit.datacapture.core.capture.DataCaptureVersion;

public class SettingsOverviewFragment extends NavigationFragment
        implements SettingsOverviewAdapter.Callback {

    private SettingsOverviewViewModel viewModel;

    public static SettingsOverviewFragment newInstance() {
        return new SettingsOverviewFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        viewModel = new ViewModelProvider(this).get(SettingsOverviewViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_settings_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView sdkVersionTextView = view.findViewById(R.id.text_scandit_sdk_version);
        sdkVersionTextView
                .setText(getString(R.string.sdk_version, DataCaptureVersion.VERSION_STRING));

        RecyclerView recyclerOverviewOptions = view.findViewById(R.id.recycler_overview);
        recyclerOverviewOptions.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerOverviewOptions.setAdapter(
                new SettingsOverviewAdapter(viewModel.getOverviewEntries(), this)
        );
    }

    @Override
    protected boolean shouldShowBackButton() {
        return true;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.settings);
    }

    @Override
    public void onEntryClick(SettingsOverviewEntry entry) {
        moveToDeeperSettings(entry);
    }

    private void moveToDeeperSettings(SettingsOverviewEntry entry) {
        switch (entry) {
            case BARCODE_CAPTURE:
                moveToFragment(BarcodeCaptureSettingsFragment.newInstance(), true, null);
                break;
            case CAMERA:
                moveToFragment(CameraSettingsFragment.newInstance(), true, null);
                break;
            case VIEW:
                moveToFragment(ViewSettingsFragment.newInstance(), true, null);
                break;
            case RESULT_HANDLING:
                moveToFragment(ResultHandlingSettingsFragment.newInstance(), true, null);
                break;
        }
    }
}

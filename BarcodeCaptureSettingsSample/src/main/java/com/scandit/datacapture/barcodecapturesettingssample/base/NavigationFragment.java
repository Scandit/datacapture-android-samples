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

package com.scandit.datacapture.barcodecapturesettingssample.base;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.scandit.datacapture.barcodecapturesettingssample.R;

public abstract class NavigationFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        setupActionBar();
    }

    private void setupActionBar() {
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(shouldShowBackButton());
            actionBar.setTitle(getTitle());
        }
    }

    protected void moveToFragment(Fragment fragment, boolean addToBackStack, String tag) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    protected void dismissKeyboard(View focusedView) {
        try {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE
            );
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        } catch (Exception e) {
            Log.d(getClass().getSimpleName(), "Error closing the keyboard", e);
        }
    }

    protected abstract boolean shouldShowBackButton();

    protected abstract String getTitle();
}

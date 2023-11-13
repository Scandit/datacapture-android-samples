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

package com.scandit.datacapture.ageverifieddeliverysample.util;

import com.google.android.material.tabs.TabLayout;

/**
 * The TabLayout.OnTabSelectedListener implementation with all methods empty.
 */
public class EmptyOnTabSelectedListener implements TabLayout.OnTabSelectedListener {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // By default does nothing.
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // By default does nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // By default does nothing.
    }
}

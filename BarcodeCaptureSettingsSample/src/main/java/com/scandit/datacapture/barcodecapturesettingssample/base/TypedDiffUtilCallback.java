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

import androidx.recyclerview.widget.DiffUtil;

public abstract class TypedDiffUtilCallback<T> extends DiffUtil.Callback {

    private T[] oldEntries, newEntries;

    public TypedDiffUtilCallback(T[] oldEntries, T[] newEntries) {
        this.oldEntries = oldEntries;
        this.newEntries = newEntries;
    }

    @Override
    public int getOldListSize() {
        return oldEntries.length;
    }

    @Override
    public int getNewListSize() {
        return newEntries.length;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return areItemsTheSame(oldEntries[oldItemPosition], newEntries[newItemPosition]);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return areContentsTheSame(oldEntries[oldItemPosition], newEntries[newItemPosition]);
    }

    public abstract boolean areItemsTheSame(T oldItem, T newItem);

    public abstract boolean areContentsTheSame(T oldItem, T newItem);
}

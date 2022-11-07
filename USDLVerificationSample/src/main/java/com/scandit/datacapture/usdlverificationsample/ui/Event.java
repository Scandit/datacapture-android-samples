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

package com.scandit.datacapture.usdlverificationsample.ui;

import androidx.annotation.Nullable;

/**
 * An event should be handled by the UI only once, when it's emitted by the observed live data.
 * It's useful for such interactions like showing a Snackbar or navigating to a new screen.
 */
public class Event<T> {
    /**
     * An optional content of this event.
     */
    private final T content;

    /**
     * Whether this event has been handled.
     */
    private boolean isHandled = false;

    /**
     * Create a new instance of this event with the given content.
     */
    public Event(T content) {
        this.content = content;
    }

    /**
     * Get the content of this event or null if this event has already been handled.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (isHandled) {
            return null;
        } else {
            isHandled = true;

            return content;
        }
    }

    /**
     * Get whether this event has been handled.
     */
    public boolean isHandled() {
        return isHandled;
    }
}

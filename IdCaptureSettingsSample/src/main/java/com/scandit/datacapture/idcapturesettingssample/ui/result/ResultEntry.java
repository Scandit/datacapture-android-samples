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

package com.scandit.datacapture.idcapturesettingssample.ui.result;

import android.os.Parcel;
import android.os.Parcelable;

public class ResultEntry implements Parcelable {

    private final String key;
    private final String value;

    public ResultEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private ResultEntry(Parcel in) {
        key = in.readString();
        value = in.readString();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }

    public static final Creator<ResultEntry> CREATOR = new Creator<ResultEntry>() {
        public ResultEntry createFromParcel(Parcel in) {
            return new ResultEntry(in);
        }

        public ResultEntry[] newArray(int size) {
            return new ResultEntry[size];
        }
    };
}

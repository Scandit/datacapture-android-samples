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

package com.scandit.datacapture.usdlverificationsample.ui.result;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class CaptureResult implements Parcelable {
    public static final Creator<CaptureResult> CREATOR = new Creator<CaptureResult>() {
        public CaptureResult createFromParcel(Parcel in) {
            return new CaptureResult(in);
        }

        public CaptureResult[] newArray(int size) {
            return new CaptureResult[size];
        }
    };

    private final ArrayList<Entry> entries;

    @Nullable private final byte[] faceImageBytes;
    @Nullable private final byte[] verificationImageBytes;

    private boolean isExpired;
    private boolean isFrontBackComparisonSuccessful;
    private boolean isBarcodeVerificationSuccessful;
    private boolean isShownLicenseWarning;

    public CaptureResult(
            Collection<Entry> entries,
            @Nullable byte[] faceImageBytes,
            boolean isExpired,
            boolean isFrontBackComparisonSuccessful,
            boolean isBarcodeVerificationSuccessful,
            @Nullable byte[] verificationImageBytes,
            boolean isShownLicenseWarning
    ) {
        this.entries = new ArrayList<>(entries);
        this.faceImageBytes = faceImageBytes;
        this.isExpired = isExpired;
        this.isFrontBackComparisonSuccessful = isFrontBackComparisonSuccessful;
        this.isBarcodeVerificationSuccessful = isBarcodeVerificationSuccessful;
        this.verificationImageBytes = verificationImageBytes;
        this.isShownLicenseWarning = isShownLicenseWarning;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    @Nullable
    public byte[] getFaceImageBytes() {
        return faceImageBytes;
    }

    @Nullable
    public byte[] getVerificationImageBytes() {
        return verificationImageBytes;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public boolean isFrontBackComparisonSuccessful() {
        return isFrontBackComparisonSuccessful;
    }

    public boolean isBarcodeVerificationSuccessful() {
        return isBarcodeVerificationSuccessful;
    }

    public boolean isShownLicenseWarning() {
        return isShownLicenseWarning;
    }

    private CaptureResult(Parcel in) {
        entries = in.readArrayList(getClass().getClassLoader());

        int faceImageBytesSize = in.readInt();
        faceImageBytes = new byte[faceImageBytesSize];
        in.readByteArray(faceImageBytes);
        isExpired = in.readInt() == 1;
        isFrontBackComparisonSuccessful = in.readInt() == 1;
        isBarcodeVerificationSuccessful = in.readInt() == 1;
        int verificationImageBytesSize = in.readInt();
        verificationImageBytes = new byte[verificationImageBytesSize];
        in.readByteArray(verificationImageBytes);
        isShownLicenseWarning = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(entries);

        if (faceImageBytes != null) {
            dest.writeInt(faceImageBytes.length);
            dest.writeByteArray(faceImageBytes);
        }
        dest.writeInt(isExpired ? 1 : 0);
        dest.writeInt(isFrontBackComparisonSuccessful ? 1 : 0);
        dest.writeInt(isBarcodeVerificationSuccessful ? 1 : 0);
        if (verificationImageBytes != null) {
            dest.writeInt(verificationImageBytes.length);
            dest.writeByteArray(verificationImageBytes);
        }
        dest.writeInt(isShownLicenseWarning ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class Entry implements Parcelable {
        public static final Creator<Entry> CREATOR = new Creator<Entry>() {
            public Entry createFromParcel(Parcel in) {
                return new Entry(in);
            }

            public Entry[] newArray(int size) {
                return new Entry[size];
            }
        };

        private final String key;

        private final String value;


        private Entry(Parcel in) {
            key = in.readString();
            value = in.readString();
        }

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @Override
        public int describeContents() { return 0; }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeString(value);
        }
    }
}

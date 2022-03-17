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

import androidx.annotation.NonNull;
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

    @NonNull private final String fullname;
    @NonNull private final String dateOfBirth;

    @Nullable private final byte[] faceImageBytes;
    @Nullable private final byte[] idFrontImageBytes;
    @Nullable private final byte[] idBackImageBytes;

    public CaptureResult(
            Collection<Entry> entries,
            @NonNull String fullname,
            @NonNull String dateOfBirth,
            @Nullable byte[] faceImageBytes,
            @Nullable byte[] idFrontImageBytes,
            @Nullable byte[] idBackImageBytes
    ) {
        this.entries = new ArrayList<>(entries);
        this.fullname = fullname;
        this.dateOfBirth = dateOfBirth;
        this.faceImageBytes = faceImageBytes;
        this.idFrontImageBytes = idFrontImageBytes;
        this.idBackImageBytes = idBackImageBytes;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    @NonNull
    public String getFullname() {
        return fullname;
    }

    @NonNull
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @Nullable
    public byte[] getFaceImageBytes() {
        return faceImageBytes;
    }
    @Nullable
    public byte[] getIdFrontImageBytes() {
        return idFrontImageBytes;
    }
    @Nullable
    public byte[] getIdBackImageBytes() {
        return idBackImageBytes;
    }

    private CaptureResult(Parcel in) {
        entries = in.readArrayList(getClass().getClassLoader());
        fullname = in.readString();
        dateOfBirth = in.readString();

        int faceImageBytesSize = in.readInt();
        faceImageBytes = new byte[faceImageBytesSize];
        in.readByteArray(faceImageBytes);

        int idFrontImageBytesSize = in.readInt();
        idFrontImageBytes = new byte[idFrontImageBytesSize];
        in.readByteArray(idFrontImageBytes);

        int idBackImageBytesSize = in.readInt();
        idBackImageBytes = new byte[idBackImageBytesSize];
        in.readByteArray(idBackImageBytes);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(entries);
        dest.writeString(fullname);
        dest.writeString(dateOfBirth);

        if (faceImageBytes != null) {
            dest.writeInt(faceImageBytes.length);
            dest.writeByteArray(faceImageBytes);
        }
        if (idFrontImageBytes != null) {
            dest.writeInt(idFrontImageBytes.length);
            dest.writeByteArray(idFrontImageBytes);
        }
        if (idBackImageBytes != null) {
            dest.writeInt(idBackImageBytes.length);
            dest.writeByteArray(idBackImageBytes);
        }
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

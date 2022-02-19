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

package com.scandit.datacapture.vincodessample.data;

import androidx.annotation.Nullable;

import com.scandit.datacapture.parser.ParsedData;

import java.util.Map;

/**
 * The data decoded from a Vehicle Identification Number (VIN). Consult ScanditDataCapture
 * documentation for the meaning of each field.
 */
public class VinResult {
    private String region;

    private String fullCode;

    @Nullable
    private String numberOfVehicles;

    private String vds;

    private String minModelYear;

    private String maxModelYear;

    private String plant;

    @Nullable
    private String wmiSuffix;

    private String serialNumber;

    private String checksum;

    private boolean checksumPassed;

    private String standard;

    /**
     * Create an instance of this class from the data returned by Parser.
     */
    public static VinResult from(ParsedData parsedData) {
        VinResult vin = new VinResult();

        Map<String, Object> wmi = getField(parsedData, "WMI");
        vin.region = (String) wmi.get("region");
        vin.fullCode = (String) wmi.get("fullCode");
        vin.numberOfVehicles = (String) wmi.get("numberOfVehicles");

        // VDS part
        vin.vds = (String) parsedData.getFieldsByName().get("VDS").getParsed();

        // VIS part
        Map<String, Object> vis = getField(parsedData, "VIS");
        Object[] modelYear = (Object[]) vis.get("modelYear");
        vin.minModelYear = modelYear[0].toString();
        vin.maxModelYear = modelYear[1].toString();
        vin.plant = (String) vis.get("plant");
        vin.wmiSuffix = (String) vis.get("wmiSuffix");
        vin.serialNumber = (String) vis.get("serialNumber");

        // metadata part
        Map<String, Object> metadata = getField(parsedData, "metadata");
        vin.checksum = (String) metadata.get("checksum");
        vin.checksumPassed = (Boolean) metadata.get("passedChecksum");
        vin.standard = (String) metadata.get("standard");

        return vin;
    }

    private static Map<String, Object> getField(ParsedData parsedData, String name) {
        //noinspection unchecked
        return (Map<String, Object>) parsedData.getFieldsByName().get(name).getParsed();
    }

    private VinResult() {
        // Use the factory method.
    }

    public String getRegion() {
        return region;
    }

    public String getFullCode() {
        return fullCode;
    }

    @Nullable
    public String getNumberOfVehicles() {
        return numberOfVehicles;
    }

    public String getVds() {
        return vds;
    }

    public String getMinModelYear() {
        return minModelYear;
    }

    public String getMaxModelYear() {
        return maxModelYear;
    }

    public String getPlant() {
        return plant;
    }

    @Nullable
    public String getWmiSuffix() {
        return wmiSuffix;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getChecksum() {
        return checksum;
    }

    public boolean isChecksumPassed() {
        return checksumPassed;
    }

    public String getStandard() {
        return standard;
    }
}
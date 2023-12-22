# Age Verified Delivery Sample

This sample demonstrates how to use ID Capture for age verification during a delivery workflow.

## Installation

- Clone this repo locally
- Sign in to your Developer Account at [ssl.scandit.com](http://ssl.scandit.com) and generate a license key.  If you do not have an account, sign up here: [https://ssl.scandit.com/dashboard/sign-up?p=test](https://ssl.scandit.com/dashboard/sign-up?p=test).
- Replace the license key in the sample where you see

```swift
extension DataCaptureContext {
    private static let licenseKey = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --"

    // Get a licensed DataCaptureContext
    static let licensed = DataCaptureContext(licenseKey: licenseKey)
}
```

- Build and run this sample on your mobile device

## What is ID Capture?

ID Capture is a capability of Scandit’s Data Capture SDK.

## Documentation

Select your platform to access ID capture documentation

[iOS](https://docs.scandit.com/data-capture-sdk/ios/index.html), [Android,](https://docs.scandit.com/data-capture-sdk/android/index.html) [Web](https://docs.scandit.com/data-capture-sdk/web/index.html), [Cordova](https://docs.scandit.com/data-capture-sdk/cordova/index.html), Xamarin ([iOS](https://docs.scandit.com/data-capture-sdk/xamarin.ios/index.html), [Android](https://docs.scandit.com/data-capture-sdk/xamarin.android/index.html), [Forms](https://docs.scandit.com/data-capture-sdk/xamarin.forms/index.html)), .NET ([iOS](https://docs.scandit.com/data-capture-sdk/dotnet.ios/index.html), [Android](https://docs.scandit.com/data-capture-sdk/dotnet.android/index.html)), [React Native](https://docs.scandit.com/data-capture-sdk/react-native/index.html), [Flutter,](https://docs.scandit.com/data-capture-sdk/flutter/index.html) [Capacitor](https://docs.scandit.com/data-capture-sdk/capacitor/index.html)

## Sample Barcodes

Once you get the sample up and running, go find some barcodes to scan. Don’t feel like getting up from your desk? Here’s a [handy pdf of barcodes](https://github.com/Scandit/.github/blob/main/images/PrintTheseBarcodes.pdf) you can print out.

## Trial Signup

To add ID Capture to your app, sign up for your Scandit Developer Account  and get instant access to your license key: [https://ssl.scandit.com/dashboard/sign-up?p=test](https://ssl.scandit.com/dashboard/sign-up?p=test)

## Support

Our support engineers can be reached at [support@scandit.com](mailto:support@scandit.com).

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)
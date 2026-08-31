# Price Capture Sample

This sample verifies shelf prices in a single shot. It captures the product barcode and the printed
price from a price label using Smart Label Capture's pre-built price-label definition
(`createPriceCaptureDefinition`), then compares the captured `(barcode, price)` pair against a local
reference database and highlights the price on the label with an AR overlay telling you whether the
price is correct, incorrect, or unknown.

## Installation

- Clone this repo locally.
- Sign in to your Developer Account at [ssl.scandit.com](http://ssl.scandit.com) and generate a license key. If you do not have an account, sign up here: [https://ssl.scandit.com/dashboard/sign-up?p=test](https://ssl.scandit.com/dashboard/sign-up?p=test).
- Open `secrets.properties` and set the value of `SCANDIT_LICENSE_KEY` to your license key in place of the placeholder `YOUR_SCANDIT_LICENSE_KEY`.
- Build and run this sample on your mobile device.

## AI Coding Skills

Use the matching Scandit skill to get AI-assisted help for this sample. Install the Scandit skills collection once, then invoke the label-capture-android skill.

[![Install via skills.sh](https://img.shields.io/badge/skills.sh-install-green)](https://skills.sh/scandit/skills)
[![Install in Cursor](https://img.shields.io/badge/Install%20in-Cursor-blue?style=flat-square&logo=cursor)](https://cursor.com/marketplace/scandit)

## How it works

A single full-screen camera preview runs Label Capture with the pre-built
`LabelDefinition.createPriceCaptureDefinition(...)`, which reads the product barcode (`BARCODE`
field) and the printed price (`TEXT` field) in one shot. Each captured `(barcode, price)` pair is
looked up in the reference database and the price field is marked with the result: a colored outline
around the field (`LabelCaptureBasicOverlay`) plus a status pin floated above it
(`LabelCaptureAdvancedOverlay`).

| State     | Color  | Pin | Meaning                                                       |
| --------- | ------ | --- | ------------------------------------------------------------- |
| Correct   | green  | ✓   | Barcode found in the database and the captured price matches. |
| Incorrect | red    | ✕   | Barcode found in the database but the captured price differs. |
| Unknown   | yellow | !   | Barcode not present in the database.                          |

No text is drawn on the label; the captured price is logged to logcat (tag `PriceCapture`) for debugging.

## Trying it out

`sample_products.png` (in this folder) is a shelf of price labels you can scan straight off the
screen or a printout. With the bundled reference data, most products read as **correct** (green);
Espresso Classic, Organic Granola, and Blueberry Jam read as **incorrect** (red — their shelf price
differs from the reference); and Peach-Mango Jam, which isn't in the database, reads as **unknown**
(yellow).

## Swapping the reference data

The reference prices live in `src/main/assets/barcode_price_database.csv`, a two-column
`barcode,price` CSV with one product per line, for example:

```csv
# Correct prices
7846987774588,7.99
7617400031003,4.29
# Incorrect prices
7853105120547,5.99
```

To use your own data, edit that file (keep the `barcode,price` format, one entry per line) and
rebuild the app. Blank lines and lines starting with `#` are ignored, so you can annotate the data.
The database is loaded once at startup, is read-only at runtime, and is never surfaced in the UI.
Comparison is exact at the cent.

## What is Smart Label Capture?

Smart Label Capture coordinates capturing the data contained in multiple barcodes and text that
occur together on a label in a single scan.

## Documentation

Smart Label Capture is an API of the Scandit Data Capture SDK. Our SDK is supported on most popular
frameworks.

Get started with Label Capture on [iOS](https://docs.scandit.com/sdks/ios/label-capture/get-started/), [Android](https://docs.scandit.com/sdks/android/label-capture/get-started), [React Native](https://docs.scandit.com/sdks/react-native/label-capture/get-started/).

For all data capture functionality across frameworks, see documentation:

[iOS](https://docs.scandit.com/data-capture-sdk/ios/index.html), [Android](https://docs.scandit.com/data-capture-sdk/android/index.html), [Web](https://docs.scandit.com/data-capture-sdk/web/index.html), [Cordova](https://docs.scandit.com/data-capture-sdk/cordova/index.html), .NET ([iOS](https://docs.scandit.com/data-capture-sdk/dotnet.ios/index.html), [Android](https://docs.scandit.com/data-capture-sdk/dotnet.android/index.html)), [React Native](https://docs.scandit.com/data-capture-sdk/react-native/index.html), [Flutter](https://docs.scandit.com/data-capture-sdk/flutter/index.html), [Capacitor](https://docs.scandit.com/data-capture-sdk/capacitor/index.html), [Titanium](https://docs.scandit.com/data-capture-sdk/titanium/index.html)

## Trial Signup

To add Smart Label Capture to your app, sign up for your Scandit Developer Account and get instant access to your license key: [https://ssl.scandit.com/dashboard/sign-up?p=test](https://ssl.scandit.com/dashboard/sign-up?p=test)

## Support

Our support engineers can be reached at [support@scandit.com](mailto:support@scandit.com).

## License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

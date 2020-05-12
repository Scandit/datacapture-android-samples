# Available Samples

We have created both simple and advanced samples that show you how to capture barcodes and how to use MatrixScan functionality.
The simple samples allow you to get going quickly, while the advanced ones show you how to use the additional settings and setup the scanner for the best user experience.

## Barcode Capture Samples

|                               Simple Sample                              |                                View Sample                               |                             Settings Sample                              |
|:------------------------------------------------------------------------:|:------------------------------------------------------------------------:|:------------------------------------------------------------------------:|
| ![Simple Sample](images/sample-bc-simple-1.jpg?raw=true "Simple Sample") ![Simple Sample](images/sample-bc-simple-2.jpg?raw=true "Simple Sample") | ![View Sample](images/sample-bc-view-1.jpg?raw=true "View Sample") ![View Sample](images/sample-bc-view-2.jpg?raw=true "View Sample") | ![Settings Sample](images/sample-bc-settings-1.jpg?raw=true "Settings Sample") ![Settings Sample](images/sample-bc-settings-2.jpg?raw=true "Settings Sample") |
| Basic sample that uses the camera to read a single barcode.              | Demonstrates the various ways to best integrate the scanner into the UI of your app. | Demonstrates how you can adapt the scanner settings best to your needs and experiment with all the options. |

|                               Reject Sample                              |
|:------------------------------------------------------------------------:|
| ![Reject Sample](images/sample-bc-reject-1.jpg?raw=true "Reject Sample") ![Reject Sample](images/sample-bc-reject-2.jpg?raw=true "Reject Sample") |
 Sample that uses the camera to read<br> a single QR code that starts with "09:"<br> but ignores/rejects all other codes. |

## MatrixScan Samples

|                               Reject Sample                              |                               Bubble Sample                              |                          Search And Find Sample                          |
|:------------------------------------------------------------------------:|:------------------------------------------------------------------------:|:------------------------------------------------------------------------:|
| ![Reject Sample](images/sample-ms-reject-1.jpg?raw=true "Reject Sample") ![Reject Sample](images/sample-ms-reject-2.jpg?raw=true "Reject Sample") | ![Bubble Sample](images/sample-ms-bubble-1.jpg?raw=true "Bubble Sample") ![Bubble Sample](images/sample-ms-bubble-2.jpg?raw=true "Bubble Sample") | ![Search](images/sample-ms-saf-1.jpg?raw=true "Search") ![Find](images/sample-ms-saf-2.jpg?raw=true "Find") |
| Sample which shows how you can highlight selected (by a custom condition) barcodes on screen with the Scandit Data Capture SDK. | Demonstrates the use of more advanced augmented reality use cases with the Scandit Data Capture SDK. | Demonstrates a use case that requires a consecutive use of both Barcode Capture and MatrixScan in a single app. |

## Text Capture Samples

|                           Text Recognition Sample                        |                             MRZ Scanner Sample                           |
|:------------------------------------------------------------------------:|:------------------------------------------------------------------------:|
| ![Text Recognition Sample - Scan Screen](images/sample-tc-textrecognition-1.png?raw=true "Text Recognition Sample - Scan Screen") ![Text Recognition Sample - Result Dialog](images/sample-tc-textrecognition-2.png?raw=true "Text Recognition Sample - Result Dialog") ![Text Recognition Sample - Settings Screen](images/sample-tc-textrecognition-3.png?raw=true "Text Recognition Sample - Settings Screen") | ![MRZ Scanner Sample - Scan Screen](images/sample-tc-mrz-1.png?raw=true "MRZ Scanner Sample - Scan Screen") ![MRZ Scanner Sample - Result Dialog](images/sample-tc-mrz-2.png?raw=true "MRZ Scanner Sample - Result Dialog") |
| Shows how to recognize various kinds of texts<br \>in the specific locations in the frame. | Demonstrates MRZ recognition by combining Text Capture and Parser. |

# Run the Samples

The best way to start working with the Scandit Data Capture SDK is to run one of our sample apps.

Before you can run a sample app, you need to go through a few simple steps:

  1. Clone or download the samples repository.
  
  2. Sign in to your Scandit account and download the newest Android Framework at <https://ssl.scandit.com/downloads/>. Unzip the archive and copy the `libraries` directory into the parent directory of the one with all the samples. It should look like this:
  
  ![Frameworks setup](images/samples-libs-setup.png?raw=true "Frameworks setup")
  
  3. Open the `samples` directory as a project in Android Studio or IntelliJ IDEA. Make sure you always have the most recent version of Android Studio or IntelliJ IDEA and the Android plugin installed.
  
  4. Set the license key. To do this, sign in to your Scandit account and find your license key at <https://ssl.scandit.com/licenses/>. Once you have the license key, add it to the sample:
  
      ```java
      // Enter your Scandit SDK License key here.
      // Your Scandit SDK License key is available via your Scandit SDK web account.
      public static final String SCANDIT_LICENSE_KEY = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";
      ```
  
      `SCANDIT_LICENSE_KEY` variables are placed in each sample project Activity class.
  
  5. Run the sample in Android Studio or IntelliJ IDEA by selecting a run configuration and pressing the Run button. We recommend running our samples on a physical device as otherwise no camera is available.

# Documentation & Getting Started Guides

If you want to learn more, check the complete documentation and getting started guides [here](https://docs.scandit.com/data-capture-sdk/android/)

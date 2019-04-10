# Available Samples

We have created both simple and advanced samples that show you how to capture barcodes and how to use Matrix Scan functionality.
The simple samples allow you to get going quickly, while the advanced samples show you how to use additional settings and setup the scanner for the best user experience.

## Barcode Capture Samples

|                        Simple Sample                        |                                 User Interface Sample                                |                                               Settings Sample                                               |
|:-----------------------------------------------------------:|:------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------------------------:|
|  ![alt text](/images/sample-bc-simple.png?raw=true "Simple Sample")  |                                      Coming Soon                                     |                                                 Coming Soon                                                 |
| Basic sample that uses the camera to read a single barcode. | Demonstrates the various ways to best integrate the scanner into the UI of your app. | Demonstrates how you can adapt the scanner settings best to your needs and experiment with all the options. |


## MatrixScan Samples

|                                               Simple Sample                                               |                                           Advanced Sample                                           |
|:---------------------------------------------------------------------------------------------------------:|:---------------------------------------------------------------------------------------------------:|
|                         ![alt text](/images/sample-ms-simple.png?raw=true "Simple Sample")                         |                                             Coming Soon                                             |
| Very simple sample which shows how you can highlight barcodes on screen with the Scandit DataCapture SDK. | Demonstrates the use of more advanced augmented reality use cases with the Scandit DataCapture SDK. |

# Run the Samples

The best way to start working with the Scandit DataCapture SDK is to run one of our sample apps.

Before you can run a sample app, you need to go through a few simple steps:

  1. Clone or download the samples repository.
  
  2. Sign in to your Scandit account and download the newest iOS Framework at <https://ssl.scandit.com/downloads/>. Unzip the archive and copy the `frameworks` directory into the parent directory of the one with all the samples. It should look like this:
  
  ![alt text](/images/samples-libs-setup.png?raw=true "Frameworks setup")
  
  3. Open the `samples` directory as a project in Android Studio or IntelliJ IDEA. Make sure you always have the most recent version of Android Studio or IntelliJ IDEA and the Android plugin installed.
  
  4. Set the license key. To do this, sign in to your Scandit account and find your license key at <https://ssl.scandit.com/licenses/>`. Once you have the license key, add it to the sample:
  
      ```java
      // Enter your Scandit SDK License key here.
      // Your Scandit SDK License key is available via your Scandit SDK web account.
      public static final String scanditLicenseKey = "-- ENTER YOUR SCANDIT LICENSE KEY HERE --";
      ```
  
      `scanditLicenseKey` variables are placed in each sample project Activity class.
  
  5. Run the sample in Android Studio or IntelliJ IDEA by selecting a run configuration and pressing the Run button. We recommend running our samples on a physical device as otherwise no camera is available.

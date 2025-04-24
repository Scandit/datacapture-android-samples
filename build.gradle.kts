import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.

val secretsPropertiesFile = file("secrets.properties")
val keystoreFile = file("keystore-scandit.jks")

buildscript {
    val sdk: Map<String, Any> by extra(
        mapOf(
            "ndk" to "27.0.12077973",
            "min" to 23,
            "target" to 33,
            "compile" to 33
        )
    )

    val versions: Map<String, Any> by extra(
        mapOf(
            // The value of scandit_sdk_version is updated automatically in the bump_sdc_version.py script, please do not edit manually.
            "scandit_sdk_version" to "7.2.1",
            "android_gradle" to "8.5.1",
            "android_material" to "1.6.1",
            "androidx_animations" to "1.0.0",
            "androidx_appcompat" to "1.3.1",
            "androidx_cardview" to "1.0.0",
            "androidx_constraintlayout" to "2.1.4",
            "androidx_coordinatorlayout" to "1.2.0",
            "androidx_lifecycle" to "2.3.1",
            "androidx_preference" to "1.1.1",
            "androidx_test_core" to "1.5.0",
            "androidx_test_espresso" to "3.5.1",
            "androidx_test_ext_junit" to "1.1.5",
            "androidx_test_orchestrator" to "1.4.2",
            "androidx_test_rules" to "1.5.0",
            "androidx_test_runner" to "1.5.2",
            "androidx_test_services" to "1.5.0-alpha01",
            "desugar" to "1.1.5",
            "java" to JavaVersion.VERSION_1_8,
            "kotlin" to "1.8.22",
            "material" to "1.3.0",
            "mockito_android" to "3.12.4",
            "mockito_kotlin_version" to "4.0.0"
        )
    )

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${versions["android_gradle"]}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions["kotlin"]}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

subprojects {
    afterEvaluate {
        if (project.plugins.hasPlugin("com.android.application")) {
            extensions.configure<ApplicationExtension> {
                signingConfigs {
                    create("release") {
                        storeFile = keystoreFile
                        storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                        keyAlias = "scandit-release-key"
                        keyPassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                    }
                }

                buildTypes {
                    getByName("release") {
                        signingConfig = signingConfigs.getByName("release")
                    }
                }

                buildFeatures.buildConfig = true

                defaultConfig {
                    val properties = Properties()
                    val licenseKey = getLicenseKey(secretsPropertiesFile, properties)

                    buildConfigField(
                        "String", "SCANDIT_LICENSE_KEY",
                        "\"$licenseKey\""
                    )

                    if (licenseKey == "YOUR_SCANDIT_LICENSE_KEY") {
                        throw GradleException(
                            "Make sure to set the value of SCANDIT_LICENSE_KEY property to" +
                                " your license key in 'secrets.properties' file."
                        )
                    }
                }
            }
        }
    }
}

fun getLicenseKey(secretsPropertiesFile: File, properties: Properties) =
    if (secretsPropertiesFile.exists()) {
        properties.load(FileInputStream(secretsPropertiesFile))
        properties.getProperty("SCANDIT_LICENSE_KEY").orEmpty()
    } else {
        ""
    }
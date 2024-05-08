import com.android.build.gradle.BaseExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val sdk: Map<String, Int> by extra(
        mapOf(
            "min" to 23,
            "target" to 33,
            "compile" to 33
        )
    )

    val versions: Map<String, Any> by extra(
        mapOf(
            // The value of scandit_sdk_version is updated automatically in the prepare-release.py script, please do not edit manually.
            "scandit_sdk_version" to "6.24.0",
            "android_gradle" to "8.1.0",
            "android_material" to "1.6.1",
            "androidx_animations" to "1.0.0",
            "androidx_appcompat" to "1.3.1",
            "androidx_cardview" to "1.0.0",
            "androidx_constraintlayout" to "2.1.2",
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
            "kotlin" to "1.7.21",
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
}

subprojects {
    afterEvaluate {
        extensions.configure<BaseExtension> {
            signingConfigs {
                create("release") {
                    storeFile = file("../keystore-scandit.jks")
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
        }
    }
}

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

package com.scandit.datacapture.idcapturesettingssample.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.scandit.datacapture.id.data.IdCaptureDocumentType
import com.scandit.datacapture.idcapturesettingssample.data.SettingsRepository
import com.scandit.datacapture.idcapturesettingssample.ui.result.ResultScreen
import com.scandit.datacapture.idcapturesettingssample.ui.result.ResultViewModel
import com.scandit.datacapture.idcapturesettingssample.ui.scan.ScanScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.SettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.camera.CameraSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.IdCaptureSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.anonymization.IdCaptureAnonymizationSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.model.DocumentSelectionType
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.documents.IdCaptureDocumentsSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.feedback.IdCaptureFeedbackSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.regions.IdCaptureRegionsSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.rejection.IdCaptureRejectionSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.scanner.IdCaptureScannerSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.idcapture.subtypes.IdCaptureSubtypesSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.result.ResultSettingsScreen
import com.scandit.datacapture.idcapturesettingssample.ui.settings.view.ViewSettingsScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destinations.SCAN_ROUTE,
    settingsRepository: SettingsRepository,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        route = Destinations.GRAPH_ROUTE,
    ) {
        composable(Destinations.SCAN_ROUTE) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Destinations.GRAPH_ROUTE)
            }
            val resultViewModel = viewModel<ResultViewModel>(parentEntry)

            ScanScreen(
                resultViewModel = resultViewModel,
                onNavigateToResult = { result ->
                    navController.navigate(Destinations.RESULT_ROUTE)
                },
                onNavigateToSettings = {
                    navController.navigate(Destinations.SETTINGS_ROUTE)
                }
            )
        }
        composable(Destinations.SETTINGS_ROUTE) {
            SettingsScreen(
                onBackClick = navController::navigateUp,
                onIdCapturePressed = navController::navigateToIdCaptureSettings,
                onCameraPressed = navController::navigateToCameraSettings,
                onViewPressed = navController::navigateToViewSettings,
                onResultPressed = navController::navigateToResultSettings,
            )
        }
        composable(Destinations.ID_CAPTURE_SETTINGS_ROUTE) {
            IdCaptureSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
                onNavigateToAnonymization = navController::navigateToIdCaptureAnonymization,
                onNavigateToAcceptedDocuments = navController::navigateToIdCaptureAcceptedDocuments,
                onNavigateToRejectedDocuments = navController::navigateToIdCaptureRejectedDocuments,
                onNavigateToScanner = navController::navigateToIdCaptureScanner,
                onNavigateToFeedback = navController::navigateToIdCaptureFeedback,
                onNavigateToRejection = navController::navigateToIdCaptureRejection,
            )
        }
        composable(Destinations.ID_CAPTURE_ANONYMIZATION_ROUTE) {
            IdCaptureAnonymizationSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(
            route = Destinations.ID_CAPTURE_DOCUMENTS_ROUTE,
            arguments = listOf(
                navArgument("documentSelectionType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val documentSelectionTypeString =
                backStackEntry.arguments?.getString("documentSelectionType")
            val documentSelectionType = requireNotNull(
                DocumentSelectionType.entries.find { it.name == documentSelectionTypeString })

            IdCaptureDocumentsSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
                documentSelectionType = documentSelectionType,
                onDocumentClick = { documentType ->
                    when (documentType) {
                        IdCaptureDocumentType.REGION_SPECIFIC ->
                            navController.navigateToIdCaptureSubtypes(documentSelectionType)

                        else -> navController.navigateToIdCaptureRegions(
                            documentType, documentSelectionType
                        )
                    }
                },
            )
        }
        composable(Destinations.ID_CAPTURE_SCANNER_ROUTE) {
            IdCaptureScannerSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(Destinations.ID_CAPTURE_FEEDBACK_ROUTE) {
            IdCaptureFeedbackSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(Destinations.ID_CAPTURE_REJECTION_ROUTE) {
            IdCaptureRejectionSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(
            route = Destinations.ID_CAPTURE_REGIONS_ROUTE,
            arguments = listOf(
                navArgument("documentType") { type = NavType.StringType },
                navArgument("documentSelectionType") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val documentTypeString = backStackEntry.arguments?.getString("documentType") ?: ""
            val documentType = requireNotNull(
                IdCaptureDocumentType.entries.find { it.name == documentTypeString })
            val documentSelectionTypeString =
                backStackEntry.arguments?.getString("documentSelectionType")
            val documentSelectionType = requireNotNull(
                DocumentSelectionType.entries.find { it.name == documentSelectionTypeString })

            IdCaptureRegionsSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
                documentType = documentType,
                documentSelectionType = documentSelectionType,
            )
        }
        composable(
            route = Destinations.ID_CAPTURE_SUBTYPES_ROUTE,
            arguments = listOf(
                navArgument("documentSelectionType") { type = NavType.StringType },
                navArgument("searchQuery") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val documentSelectionTypeString =
                backStackEntry.arguments?.getString("documentSelectionType")
            val documentSelectionType = requireNotNull(
                DocumentSelectionType.entries.find { it.name == documentSelectionTypeString })

            IdCaptureSubtypesSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
                documentSelectionType = documentSelectionType,
            )
        }
        composable(Destinations.CAMERA_SETTINGS_ROUTE) {
            CameraSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(Destinations.VIEW_SETTINGS_ROUTE) {
            ViewSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(Destinations.RESULT_SETTINGS_ROUTE) {
            ResultSettingsScreen(
                onBackClick = navController::navigateUp,
                settingsRepository = settingsRepository,
            )
        }
        composable(Destinations.RESULT_ROUTE) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Destinations.GRAPH_ROUTE)
            }
            val resultViewModel = viewModel<ResultViewModel>(parentEntry)
            ResultScreen(
                onBackClick = navController::navigateUp,
                viewModel = resultViewModel,
            )
        }
    }
}

private fun NavController.navigateToIdCaptureSettings() {
    navigate(Destinations.ID_CAPTURE_SETTINGS_ROUTE)
}

private fun NavController.navigateToIdCaptureAnonymization() {
    navigate(Destinations.ID_CAPTURE_ANONYMIZATION_ROUTE)
}

private fun NavController.navigateToIdCaptureAcceptedDocuments() {
    navigate(Destinations.idCaptureDocumentsRoute(DocumentSelectionType.ACCEPTED))
}

private fun NavController.navigateToIdCaptureRejectedDocuments() {
    navigate(Destinations.idCaptureDocumentsRoute(DocumentSelectionType.REJECTED))
}

private fun NavController.navigateToIdCaptureScanner() {
    navigate(Destinations.ID_CAPTURE_SCANNER_ROUTE)
}

private fun NavController.navigateToIdCaptureFeedback() {
    navigate(Destinations.ID_CAPTURE_FEEDBACK_ROUTE)
}

private fun NavController.navigateToIdCaptureRejection() {
    navigate(Destinations.ID_CAPTURE_REJECTION_ROUTE)
}

private fun NavController.navigateToCameraSettings() {
    navigate(Destinations.CAMERA_SETTINGS_ROUTE)
}

private fun NavController.navigateToViewSettings() {
    navigate(Destinations.VIEW_SETTINGS_ROUTE)
}

private fun NavController.navigateToResultSettings() {
    navigate(Destinations.RESULT_SETTINGS_ROUTE)
}

private fun NavController.navigateToIdCaptureRegions(
    documentType: IdCaptureDocumentType,
    documentSelectionType: DocumentSelectionType,
) {
    navigate(
        Destinations.idCaptureRegionsRoute(
            documentType,
            documentSelectionType
        )
    )
}

private fun NavController.navigateToIdCaptureSubtypes(
    documentSelectionType: DocumentSelectionType,
) {
    navigate(
        Destinations.idCaptureSubtypesRoute(documentSelectionType)
    )
}
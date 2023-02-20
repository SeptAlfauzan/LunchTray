/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lunchtray

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.*

// TODO: Screen enum
enum class LaunchTrayScreen(val title: String){
    Start(title = "Start"),
    EntreeMenu(title = "Entree Menu"),
    SideDishMenu(title = "Side Dish Menu"),
    AccompanimentMenu(title = "Accompaniment Menu"),
    Checkout(title = "Checkout")
}
// TODO: AppBar

@Composable
fun TopBar(title: String, onBack: () -> Unit = {}, isOnStart: Boolean = false){
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if(!isOnStart) Button(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "back")
            } else null
        },
        elevation = 1.dp
    )
}

@Composable
fun LunchTrayApp(modifier: Modifier = Modifier) {
    // TODO: Create Controller and initialization
    val navHostController: NavHostController = rememberNavController()
    val navBackAsEntry by navHostController.currentBackStackEntryAsState()

    // Create ViewModel
    val viewModel: OrderViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    val title = LaunchTrayScreen.values().find { it.name == navBackAsEntry?.destination?.route }?.title

    Scaffold(
        topBar = {
            // TODO: AppBar
            TopBar(
                title = title ?: LaunchTrayScreen.Start.title,
                isOnStart = navBackAsEntry?.destination?.route == LaunchTrayScreen.Start.name
            )
        }
    ) { innerPadding ->
        // TODO: Navigation host
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navHostController,
            startDestination = LaunchTrayScreen.Start.name){
            composable(LaunchTrayScreen.Start.name){
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navHostController.navigate(LaunchTrayScreen.EntreeMenu.name)
                    }
                )
            }
            composable(LaunchTrayScreen.EntreeMenu.name){
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = { navHostController.goBackResetUiState(viewModel) },
                    onNextButtonClicked = { navHostController.navigate(LaunchTrayScreen.SideDishMenu.name) },
                    onSelectionChanged = { viewModel.updateEntree(it) }
                )
            }
            composable(LaunchTrayScreen.SideDishMenu.name){
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = { navHostController.goBackResetUiState(viewModel) },
                    onNextButtonClicked = { navHostController.navigate(LaunchTrayScreen.AccompanimentMenu.name) },
                    onSelectionChanged = { viewModel.updateSideDish(it) }
                )
            }
            composable(LaunchTrayScreen.AccompanimentMenu.name){
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = { navHostController.goBackResetUiState(viewModel) },
                    onNextButtonClicked = { navHostController.navigate(LaunchTrayScreen.Checkout.name) },
                    onSelectionChanged = { viewModel.updateAccompaniment(it) }
                )
            }
            composable(LaunchTrayScreen.Checkout.name){
                CheckoutScreen(
                    orderUiState = uiState,
                    onNextButtonClicked = { navHostController.goBackResetUiState(viewModel) },
                    onCancelButtonClicked = { navHostController.goBackResetUiState(viewModel) })
            }
        }
    }
}

private fun NavHostController.goBackResetUiState(viewModel: OrderViewModel){
    popBackStack(LaunchTrayScreen.Start.name, inclusive = false)
    viewModel.resetOrder()
}

@Preview
@Composable
fun previewComponent(){
    TopBar("Test")
}
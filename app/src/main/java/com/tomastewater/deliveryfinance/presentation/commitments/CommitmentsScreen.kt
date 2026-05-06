package com.tomastewater.deliveryfinance.presentation.commitments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomastewater.deliveryfinance.presentation.commitments.components.CommitmentCard
import com.tomastewater.deliveryfinance.presentation.commitments.components.DynamicCommitmentFab
import com.tomastewater.deliveryfinance.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentsScreen(
    onNavigateBack: () -> Unit,
    viewModel: CommitmentsViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val tabs = listOf("A Pagar", "A Cobrar", "Historial")

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Compromisos", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        },
        // 👇 NUESTRO NUEVO FAB DINÁMICO
        floatingActionButton = {
            DynamicCommitmentFab(
                selectedTab = selectedTab,
                onClick = {
                    /* TODO (FIN-411): Abrir el ModalBottomSheet aquí */
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- TABS NAVEGACIÓN ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundGray,
                contentColor = PrimaryBlue,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryBlue,
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { viewModel.setTab(index) },
                        text = { Text(title, fontWeight = FontWeight.Bold) },
                        selectedContentColor = PrimaryBlue,
                        unselectedContentColor = TextMuted
                    )
                }
            }

            // --- CONTENIDO DE LA LISTA ---
            val listToShow = when (selectedTab) {
                0 -> viewModel.getToPayList()
                1 -> viewModel.getToCollectList()
                else -> viewModel.getHistoryList()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (listToShow.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No hay registros aquí.", color = TextMuted)
                        }
                    }
                } else {
                    items(listToShow) { commitment ->
                        CommitmentCard(commitment = commitment)
                    }
                }

                // Añadimos un pequeño espacio al final para que el FAB no tape el último item
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}
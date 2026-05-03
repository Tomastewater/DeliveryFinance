package com.tomastewater.deliveryfinance.presentation.goal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomastewater.deliveryfinance.core.designsystem.ConfirmDialog
import com.tomastewater.deliveryfinance.core.navigation.Screen
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.presentation.dashboard.components.DeliveryBottomBar
import com.tomastewater.deliveryfinance.ui.theme.*

// COMPONENTES
import com.tomastewater.deliveryfinance.presentation.goal.components.AddGoalPlaceholderCard
import com.tomastewater.deliveryfinance.presentation.goal.components.AddMoneyAnimatedDialog
import com.tomastewater.deliveryfinance.presentation.goal.components.CompletedGoalCard
import com.tomastewater.deliveryfinance.presentation.goal.components.FeaturedGoalCard
import com.tomastewater.deliveryfinance.presentation.goal.components.FilterPillRow
import com.tomastewater.deliveryfinance.presentation.goal.components.SavingsTipsCarousel
import com.tomastewater.deliveryfinance.presentation.goal.components.SecondaryGoalCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToAddGoal: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onAddMoneyClick: (Goal) -> Unit,
    onNavigateToGoalDetail: (Long) -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Estados para los diálogos
    var goalToAddMoney by remember { mutableStateOf<Goal?>(null) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }
    var goalToComplete by remember { mutableStateOf<Goal?>(null) }
    var goalToMakePrincipal by remember { mutableStateOf<Goal?>(null) }

    // Obtenemos las metas
    val featuredGoal = state.activeGoals.find { it.isPrincipal } ?: state.activeGoals.firstOrNull()
    val secondaryGoals = state.activeGoals.filter { it.id != featuredGoal?.id }
    val projection by viewModel.principalGoalProjection.collectAsState()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("Metas de Ahorro", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        },
        bottomBar = {
            DeliveryBottomBar(
                currentRoute = Screen.GoalHistory.route,
                onNavigate = { route ->
                    when(route) {
                        Screen.Dashboard.route -> onNavigateToDashboard()
                        Screen.History.route -> onNavigateToHistory()
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. HEADER
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text("Tus Objetivos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Construyendo tu libertad", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }

            // 2. FILTROS
            item {
                FilterPillRow(
                    isShowingActive = state.isShowingActive,
                    onFilterChanged = { viewModel.setFilter(it) }
                )
            }

            // 3. RENDERIZADO CONDICIONAL SEGÚN LA PESTAÑA
            if (state.isShowingActive) {
                // --- VISTA: METAS EN CURSO ---
                if (featuredGoal != null) {
                    item {
                        FeaturedGoalCard(
                            goal = featuredGoal,
                            projection = projection,
                            onAddMoneyClick = { goalToAddMoney = featuredGoal },
                            onEditClick = { onNavigateToGoalDetail(featuredGoal.id) },
                            onMakePrincipal = { /* Ya es la principal */ },
                            onDelete = { goalToDelete = featuredGoal },
                            onComplete = { goalToComplete = featuredGoal }
                        )
                    }
                }

                if (secondaryGoals.isNotEmpty() || featuredGoal != null) {
                    item { Text("Otras Metas", fontWeight = FontWeight.Bold, color = PrimaryBlue) }

                    items(secondaryGoals.chunked(2)) { rowGoals ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowGoals.forEach { goal ->
                                SecondaryGoalCard(
                                    goal = goal,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onNavigateToGoalDetail(goal.id) },
                                    onEdit = { onNavigateToGoalDetail(goal.id) },
                                    onMakePrincipal = { goalToMakePrincipal = goal },
                                    onDelete = { goalToDelete = goal },
                                    onComplete = { goalToComplete = goal }
                                )
                            }
                            if (rowGoals.size == 1) {
                                AddGoalPlaceholderCard(modifier = Modifier.weight(1f), onClick = onNavigateToAddGoal)
                            }
                        }
                    }
                    if (secondaryGoals.size % 2 == 0) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AddGoalPlaceholderCard(modifier = Modifier.weight(1f), onClick = onNavigateToAddGoal)
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    item {
                        AddGoalPlaceholderCard(
                            modifier = Modifier.fillMaxWidth().height(150.dp),
                            onClick = onNavigateToAddGoal
                        )
                    }
                }

                // 4. CARRUSEL DE TIPS (Reemplaza al TipCard viejo)
                item {
                    SavingsTipsCarousel(
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

            } else {
                // --- VISTA: METAS FINALIZADAS ---
                if (state.completedGoals.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("Aún no has completado ninguna meta.", color = TextMuted)
                        }
                    }
                } else {
                    items(state.completedGoals) { goal ->
                        CompletedGoalCard(goal = goal)
                    }
                }
            }

            // Espacio final para el BottomBar
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // --- MANEJO DE DIÁLOGOS ---
    goalToAddMoney?.let { selectedGoal ->
        AddMoneyAnimatedDialog(
            goal = selectedGoal,
            availableBalance = state.availableBalance,
            onDismiss = { goalToAddMoney = null },
            onConfirm = { amount ->
                viewModel.addMoneyToGoal(selectedGoal, amount)
                goalToAddMoney = null
            }
        )
    }

    goalToDelete?.let { goal ->
        ConfirmDialog(
            title = "Eliminar Meta",
            message = "¿Estás seguro de que deseas eliminar '${goal.title}'? Esta acción no se puede deshacer.",
            confirmText = "Eliminar",
            isDestructive = true,
            onConfirm = { viewModel.deleteGoal(goal); goalToDelete = null },
            onDismiss = { goalToDelete = null }
        )
    }

    goalToMakePrincipal?.let { goal ->
        ConfirmDialog(
            title = "Hacer Principal",
            message = "Esto destacará '${goal.title}' en tu inicio y reemplazará la actual.",
            confirmText = "Aceptar",
            onConfirm = { viewModel.makePrincipal(goal); goalToMakePrincipal = null },
            onDismiss = { goalToMakePrincipal = null }
        )
    }

    goalToComplete?.let { goal ->
        ConfirmDialog(
            title = "Completar Manualmente",
            message = "Esto moverá '${goal.title}' a Finalizadas sin descontar saldo de tu billetera.",
            confirmText = "Completar",
            onConfirm = { viewModel.completeGoalManually(goal); goalToComplete = null },
            onDismiss = { goalToComplete = null }
        )
    }
}
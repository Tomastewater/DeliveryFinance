package com.tomastewater.deliveryfinance.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomastewater.deliveryfinance.core.navigation.Screen
import com.tomastewater.deliveryfinance.presentation.dashboard.components.*
import com.tomastewater.deliveryfinance.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddGoal: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGoalHistory: () -> Unit,
    onNavigateToAddTransaction: (String) -> Unit,
    onNavigateToCommitments: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = { Text("DeliveryFinance", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        },
        bottomBar = {
            DeliveryBottomBar(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route ->
                    when(route) {
                        Screen.GoalHistory.route -> onNavigateToGoalHistory()
                        Screen.History.route -> onNavigateToHistory()
                    }
                }
            )
        },
        floatingActionButton = {
            ExpandableFAB(
                onAddIncome = { onNavigateToAddTransaction("INCOME") },
                onAddExpense = { onNavigateToAddTransaction("EXPENSE") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. HEADER
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Tu semana actual", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Resumen financiero", color = TextMuted)
                }
            }

            // 2. TARJETA PRINCIPAL (Saldo)
            item {
                BalanceSummaryCard(
                    totalBalance = state.totalBalance,
                    dailyIncome = state.dailyIncome,
                    dailyExpenses = state.dailyExpenses
                )
            }

            // 3. ACCESOS RÁPIDOS
            item {
                QuickActionsRow(
                    onAddIncomeClick = { /* TODO: */ },
                    onAddExpenseClick = { /* TODO: */ },
                    onNavigateToCommitments = onNavigateToCommitments,
                    onNavigateToBudgets = { /* TODO: Presupuestos */ }
                )
            }

            // 4. CUADRÍCULA BENTO (Metas y Distribución)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DistributionBentoCard(modifier = Modifier.weight(1f))

                    ActiveGoalBentoCard(
                        goal = state.activeGoals.firstOrNull(),
                        onClick = onNavigateToAddGoal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Espacio al final para el FAB
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}
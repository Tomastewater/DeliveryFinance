package com.tomastewater.deliveryfinance.presentation.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.model.Transaction
import com.tomastewater.deliveryfinance.domain.model.TransactionType
import com.tomastewater.deliveryfinance.presentation.dashboard.components.GoalCard
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tomastewater.deliveryfinance.presentation.dashboard.components.ComparisonChart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import com.tomastewater.deliveryfinance.presentation.dashboard.components.ExpandableFAB


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddGoal: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGoalHistory: () -> Unit,
    onNavigateToAddTransaction: (String) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val balanceColor = if (state.totalBalance < 0) Color.Red else MaterialTheme.colorScheme.onPrimary

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("DELIVERY FINANCE", style = MaterialTheme.typography.labelLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExpandableFAB(
                onAddIncome = { onNavigateToAddTransaction("INCOME") },
                onAddExpense = { onNavigateToAddTransaction("EXPENSE") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp) // Esto da el margen perfecto entre tarjetas
        ) {
            // 1. Tarjeta de Meta Diaria
            item {
                GoalProgressBar(
                    goal = state.activeGoal,
                    onNavigateToAddGoal = onNavigateToAddGoal,
                    onCompleteGoal = { viewModel.onCompleteGoal(it) },
                    onDeleteGoal = { viewModel.onDeleteGoal(it) }
                )
            }

            // 2. Tarjeta del Saldo
            item {
                BalanceCard(balance = state.totalBalance)
            }

            // 3. Tarjeta del Gráfico Comparativo
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Balance Semanal",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        ComparisonChart(
                            income = state.dailyIncome,
                            expenses = state.dailyExpenses,
                            modifier = Modifier.padding(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            LegendItem(label = "Ingresos", color = MaterialTheme.colorScheme.primary)
                            LegendItem(label = "Gastos", color = MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }

            // 4. Título de historial
            item {
                Text(
                    text = "Explorar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                // Fila 1 de tarjetas (2 columnas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardActionCard(
                        title = "Movimientos",
                        subtitle = "Ingresos y Egresos",
                        icon = Icons.Default.List,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToHistory
                    )

                    DashboardActionCard(
                        title = "Mis Logros",
                        subtitle = "Metas cumplidas",
                        icon = Icons.Default.Star,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToGoalHistory // <-- Agregaremos esta ruta ahora
                    )
                }
            }

            item {
                // Fila 2 de tarjetas (Espacio para el futuro)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardActionCard(
                        title = "Estadísticas",
                        subtitle = "Próximamente",
                        icon = Icons.Default.Menu, // Cambia el icono luego
                        modifier = Modifier.weight(1f),
                        onClick = { /* TODO en el futuro */ }
                    )

                    // Tarjeta vacía invisible para mantener el ancho de la columna izquierda si solo hay 3 tarjetas
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun GoalProgressBar(
    goal: Goal?,
    onNavigateToAddGoal: () -> Unit,
    onCompleteGoal: (Goal) -> Unit,
    onDeleteGoal: (Goal) -> Unit
) {
    if (goal == null) {
        // ESTADO VACÍO: Dibuja la tarjeta para invitar a crear una meta
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clickable { onNavigateToAddGoal() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ Toca aquí para definir una Meta",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    } else {
        // ESTADO ACTIVO: Dibuja la barra de progreso
        var expanded by remember { mutableStateOf(false) }
        val animatedProgress by animateFloatAsState(
            targetValue = goal.progressPercentage,
            animationSpec = tween(durationMillis = 1000),
            label = "progress"
        )

        // --- CONTENEDOR MODERNIZADO ---
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onNavigateToAddGoal() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = goal.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "${(animatedProgress * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                        // Botón de 3 puntitos
                        Box {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Opciones de meta",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Menú desplegable
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                containerColor = MaterialTheme.colorScheme.surface
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Completar y Guardar") },
                                    onClick = {
                                        expanded = false
                                        onCompleteGoal(goal)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Editar Meta") },
                                    onClick = {
                                        expanded = false
                                        onNavigateToAddGoal() // Reutilizamos la pantalla de crear para editar
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        expanded = false
                                        onDeleteGoal(goal)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Barra base (fondo)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                ) {
                    // Barra de progreso (relleno)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$${goal.savedAmount.toInt()} / $${goal.targetAmount.toInt()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
@Composable
fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.type == TransactionType.INCOME
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(transaction.category, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (isIncome) "Ingreso" else "Gasto",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "${if (isIncome) "+" else "-"} $${transaction.amount.toInt()}",
                style = MaterialTheme.typography.titleMedium,
                color = if (isIncome) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, shape = CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun BalanceCard(balance: Double) {
    val balanceColor = if (balance < 0) Color.Red else MaterialTheme.colorScheme.onPrimary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            // Usamos un alpha del 30% para que se funda elegantemente con el fondo oscuro
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Saldo Disponible",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${balance.toInt()}",
                color = balanceColor,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
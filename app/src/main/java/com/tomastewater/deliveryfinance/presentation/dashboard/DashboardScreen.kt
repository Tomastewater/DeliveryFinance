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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tomastewater.deliveryfinance.core.navigation.Screen
import com.tomastewater.deliveryfinance.presentation.dashboard.components.ActiveGoalItem
import com.tomastewater.deliveryfinance.presentation.dashboard.components.DeliveryBottomBar
import com.tomastewater.deliveryfinance.presentation.dashboard.components.ExpandableFAB
import com.tomastewater.deliveryfinance.ui.theme.BackgroundGray
import com.tomastewater.deliveryfinance.ui.theme.CardSurface
import com.tomastewater.deliveryfinance.ui.theme.ExpenseRed
import com.tomastewater.deliveryfinance.ui.theme.IncomeGreen
import com.tomastewater.deliveryfinance.ui.theme.PrimaryBlue
import com.tomastewater.deliveryfinance.ui.theme.TextDark
import com.tomastewater.deliveryfinance.ui.theme.TextMuted
import com.tomastewater.deliveryfinance.ui.theme.WarningBackground
import com.tomastewater.deliveryfinance.ui.theme.WarningText

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

    Scaffold(
        containerColor = BackgroundGray, // El fondo gris claro de tu prototipo
        topBar = {
            TopAppBar(
                title = { Text("DeliveryFinance", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        },
        bottomBar = {
            DeliveryBottomBar(
                currentRoute = Screen.Dashboard.route, // Le decimos que estamos en el inicio
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

            // 1. HEADER (Semana actual)
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text("Tu semana actual", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    Text("Resumen financiero", color = TextMuted)
                }
            }

            // 2. TARJETA PRINCIPAL (Saldo y Egresos/Ingresos)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Saldo disponible", color = TextMuted, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "$${state.totalBalance.toInt()}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (state.totalBalance < 0) ExpenseRed else TextDark
                                )
                            }
                            // Píldora de advertencia si es negativo
                            if (state.totalBalance < 0) {
                                Box(
                                    modifier = Modifier.background(WarningBackground, RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Atención: Negativo", color = WarningText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = BackgroundGray)

                        // Ingresos y Egresos en la misma tarjeta
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ingresos", color = TextMuted, fontSize = 12.sp)
                                Text("$${state.dailyIncome.toInt()}", color = IncomeGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Egresos", color = TextMuted, fontSize = 12.sp)
                                Text("$${state.dailyExpenses.toInt()}", color = ExpenseRed, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // 3. CUADRÍCULA BENTO (Metas y Distribución)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Tarjeta Izquierda (Distribución/Estadísticas futuras)
                    Card(
                        modifier = Modifier.weight(1f).height(180.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Distribución", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            // Aquí irá tu Donut Chart en el futuro
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Gráfico", color = TextMuted)
                            }
                        }
                    }

                    // Tarjeta Derecha (Meta Activa)
                    Card(
                        modifier = Modifier.weight(1f).height(180.dp).clickable { onNavigateToAddGoal() },
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                            val mainGoal = state.activeGoals.firstOrNull()

                            if (mainGoal != null) {
                                Column {
                                    Text("Meta activa", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(mainGoal.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 2)
                                }
                                Column {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("$${mainGoal.savedAmount.toInt()}", color = IncomeGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("$${mainGoal.targetAmount.toInt()}", color = TextMuted, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { mainGoal.progressPercentage },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = IncomeGreen,
                                        trackColor = BackgroundGray
                                    )
                                }
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("+ Crear Meta", color = TextMuted, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Espacio al final para que el FAB no tape contenido
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
package com.tomastewater.deliveryfinance.presentation.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomastewater.deliveryfinance.core.navigation.Screen
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.presentation.dashboard.components.DeliveryBottomBar
import com.tomastewater.deliveryfinance.ui.theme.BackgroundGray
import com.tomastewater.deliveryfinance.ui.theme.PrimaryBlue
import com.tomastewater.deliveryfinance.ui.theme.PrimaryLight
import com.tomastewater.deliveryfinance.ui.theme.SecondaryContainer
import com.tomastewater.deliveryfinance.ui.theme.SecondaryGreen
import com.tomastewater.deliveryfinance.ui.theme.TextMuted


// --- PANTALLA PRINCIPAL ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    onNavigateToAddGoal: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onAddMoneyClick: (Goal) -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Obtenemos la meta principal y las secundarias
    val featuredGoal = state.activeGoals.find { it.isPrincipal } ?: state.activeGoals.firstOrNull()
    val secondaryGoals = state.activeGoals.filter { it.id != featuredGoal?.id }

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
                currentRoute = Screen.GoalHistory.route, // Le decimos que estamos en Metas
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

            // 2. FILTROS (FIN-402)
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
                            onAddMoneyClick = { onAddMoneyClick(featuredGoal) },
                            onEditClick = { /* TODO FIN-404 */ }
                        )
                    }
                }

                if (secondaryGoals.isNotEmpty() || featuredGoal != null) {
                    item { Text("Otras Metas", fontWeight = FontWeight.Bold, color = PrimaryBlue) }

                    items(secondaryGoals.chunked(2)) { rowGoals ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            rowGoals.forEach { goal ->
                                SecondaryGoalCard(goal = goal, modifier = Modifier.weight(1f), onClick = { onAddMoneyClick(goal) })
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
                    item { AddGoalPlaceholderCard(modifier = Modifier.fillMaxWidth().height(150.dp), onClick = onNavigateToAddGoal) }
                }

                item { TipCard() }

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
}

// --- COMPONENTES UI (TARJETAS Y BOTONES) ---

@Composable
fun FilterPillRow(isShowingActive: Boolean, onFilterChanged: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (isShowingActive) PrimaryBlue else Color.Transparent)
                .clickable { onFilterChanged(true) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "En curso",
                color = if (isShowingActive) Color.White else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(if (!isShowingActive) PrimaryBlue else Color.Transparent)
                .clickable { onFilterChanged(false) }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Finalizadas",
                color = if (!isShowingActive) Color.White else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun FeaturedGoalCard(goal: Goal, onAddMoneyClick: () -> Unit, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Box(modifier = Modifier.background(SecondaryContainer, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("META PRINCIPAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF005236))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(goal.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                }
                Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("$${goal.savedAmount.toInt()}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = PrimaryBlue)
                Text("de $${goal.targetAmount.toInt()}", fontWeight = FontWeight.Bold, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { goal.progressPercentage },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = SecondaryGreen,
                trackColor = BackgroundGray
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(goal.progressPercentage * 100).toInt()}% completado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryGreen)
                Text("Faltan $${(goal.targetAmount - goal.savedAmount).toInt()}", fontSize = 12.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onAddMoneyClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Aportar a meta")
                }
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextMuted)
                }
            }
        }
    }
}

@Composable
fun SecondaryGoalCard(goal: Goal, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() }.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.background(PrimaryLight, RoundedCornerShape(8.dp)).padding(6.dp)) {
                    Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }
                Text("${(goal.progressPercentage * 100).toInt()}%", fontWeight = FontWeight.Bold, color = SecondaryGreen)
            }
            Column {
                Text(goal.title, fontWeight = FontWeight.Bold, color = PrimaryBlue, maxLines = 1)
                Text("$${goal.savedAmount.toInt()} / $${goal.targetAmount.toInt()}", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { goal.progressPercentage },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = SecondaryGreen,
                    trackColor = BackgroundGray
                )
            }
        }
    }
}

@Composable
fun CompletedGoalCard(goal: Goal) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).background(Color(0xFF6CF8BB).copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = Color(0xFF006C49))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 16.sp)
                Text("Logrado: $${goal.targetAmount.toInt()}", color = TextMuted, fontSize = 14.sp)
            }
            Text(text = "100%", fontWeight = FontWeight.Black, color = Color(0xFF006C49), fontSize = 16.sp)
        }
    }
}

@Composable
fun AddGoalPlaceholderCard(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(2.dp, TextMuted.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nueva Meta", fontWeight = FontWeight.Bold, color = TextMuted)
        }
    }
}

@Composable
fun TipCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Tip de ahorro", fontWeight = FontWeight.Bold, color = Color.White)
                Text("Si guardas lo de 2 entregas diarias extra, llegarás a tu meta principal 3 semanas antes.", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 18.sp)
            }
        }
    }
}
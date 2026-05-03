package com.tomastewater.deliveryfinance.presentation.goal

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import com.tomastewater.deliveryfinance.core.designsystem.ConfirmDialog
import com.tomastewater.deliveryfinance.domain.usecase.goal.ProjectionResult
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
    onNavigateToGoalDetail: (Long) -> Unit,
    viewModel: GoalsViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    // Estado para controlar qué meta estamos aportando (null significa que el diálogo está cerrado)
    var goalToAddMoney by remember { mutableStateOf<Goal?>(null) }
    var goalToDelete by remember { mutableStateOf<Goal?>(null) }
    var goalToComplete by remember { mutableStateOf<Goal?>(null) }
    var goalToMakePrincipal by remember { mutableStateOf<Goal?>(null) }

    // Obtenemos la meta principal y las secundarias
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
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
                            onAddMoneyClick = { goalToAddMoney = featuredGoal },
                            onEditClick = { onNavigateToGoalDetail(featuredGoal.id) },
                            onMakePrincipal = { /* Ya es la principal, puedes omitirlo o pasar un lambda vacío */ },
                            onDelete = { viewModel.deleteGoal(featuredGoal) },
                            onComplete = { viewModel.completeGoalManually(featuredGoal) },
                            projection = projection
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
                    item { AddGoalPlaceholderCard(modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), onClick = onNavigateToAddGoal) }
                }

                item { TipCard() }

            } else {
                // --- VISTA: METAS FINALIZADAS ---
                if (state.completedGoals.isEmpty()) {
                    item {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center) {
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
            onConfirm = { viewModel.deleteGoal(goal) },
            onDismiss = { goalToDelete = null }
        )
    }

    goalToMakePrincipal?.let { goal ->
        ConfirmDialog(
            title = "Hacer Principal",
            message = "Esto destacará '${goal.title}' en tu inicio y reemplazará la actual.",
            confirmText = "Aceptar",
            onConfirm = { viewModel.makePrincipal(goal) },
            onDismiss = { goalToMakePrincipal = null }
        )
    }

    goalToComplete?.let { goal ->
        ConfirmDialog(
            title = "Completar Manualmente",
            message = "Esto moverá '${goal.title}' a Finalizadas sin descontar saldo de tu billetera.",
            confirmText = "Completar",
            onConfirm = { viewModel.completeGoalManually(goal) },
            onDismiss = { goalToComplete = null }
        )
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
fun FeaturedGoalCard(
    goal: Goal,
    projection: ProjectionResult?,
    onAddMoneyClick: () -> Unit,
    onEditClick: () -> Unit,
    onComplete: () -> Unit,
    onMakePrincipal: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // --- CABECERA ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column {
                    Box(modifier = Modifier
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("META PRINCIPAL", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color(0xFF005236))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(goal.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                }
                Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MONTOS ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Text("$${goal.savedAmount.toInt()}", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))
                Text("de $${goal.targetAmount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF757682))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- BARRA DE PROGRESO ---
            LinearProgressIndicator(
                progress = { goal.progressPercentage },
                modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF006C49),
                trackColor = Color(0xFFF3F4F6)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${(goal.progressPercentage * 100).toInt()}% completado", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006C49))
                Text("Faltan $${(goal.targetAmount - goal.savedAmount).toInt()}", fontSize = 12.sp, color = Color(0xFF757682))
            }

            // --- BANNER DEL MOTOR PREDICTIVO ---
            if (projection != null) {
                Spacer(modifier = Modifier.height(16.dp))

                val bannerText: String
                val bannerBg: Color
                val bannerContentColor: Color
                val bannerIcon: androidx.compose.ui.graphics.vector.ImageVector

                when (projection) {
                    is ProjectionResult.Success -> {
                        bannerText = "Faltan aprox. ${projection.weeks * 7} días a este ritmo"
                        bannerBg = Color(0xFFF0FDF4)
                        bannerContentColor = Color(0xFF166534)
                        bannerIcon = Icons.Default.AutoGraph
                    }
                    is ProjectionResult.Negative -> {
                        bannerText = "Ahorro insuficiente para calcular"
                        bannerBg = Color(0xFFFEF2F2)
                        bannerContentColor = Color(0xFF991B1B)
                        bannerIcon = Icons.Default.WarningAmber
                    }
                    is ProjectionResult.InsufficientData -> {
                        bannerText = "Datos insuficientes para proyectar"
                        bannerBg = Color(0xFFFFFBEB)
                        bannerContentColor = Color(0xFFB45309)
                        bannerIcon = Icons.Default.Info
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bannerBg, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(bannerIcon, contentDescription = null, tint = bannerContentColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(bannerText, color = bannerContentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTONES ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onAddMoneyClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
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
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF757682))
                }
            }
        }
    }
}

@Composable
fun SecondaryGoalCard(
    goal: Goal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onMakePrincipal: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { onClick() }
            .animateContentSize() // <--- Animación
            .height(150.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier
                    .background(PrimaryLight, RoundedCornerShape(8.dp))
                    .padding(6.dp)) {
                    Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                }

                // --- MENÚ DE TRES PUNTOS ---
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = TextMuted)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        if (!goal.isPrincipal) {
                            DropdownMenuItem(
                                text = { Text("Hacer principal", color = Color.Gray) },
                                onClick = { showMenu = false; onMakePrincipal() }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Completar manualmente", color = SecondaryGreen) },
                            onClick = { showMenu = false; onComplete() }
                        )
                        DropdownMenuItem(
                            text = { Text("Editar meta", color = Color.Gray) },
                            onClick = { showMenu = false; onEdit() }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = Color.Red) },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }
            Column {
                Text(goal.title, fontWeight = FontWeight.Bold, color = PrimaryBlue, maxLines = 1)
                Text("$${goal.savedAmount.toInt()} / $${goal.targetAmount.toInt()}", fontSize = 12.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { goal.progressPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF6CF8BB).copy(alpha = 0.3f), CircleShape),
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
            Box(modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
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

@Composable
fun AddMoneyAnimatedDialog(
    goal: Goal,
    availableBalance: Double, // Pasamos el saldo actual
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) } // Control de error

    LaunchedEffect(Unit) { isVisible = true }

    fun closeAndDismiss() {
        isVisible = false
        onDismiss()
    }

    Dialog(onDismissRequest = { closeAndDismiss() }) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(300)) + scaleIn(tween(300), initialScale = 0.8f),
            exit = fadeOut(tween(200)) + scaleOut(tween(200), targetScale = 0.8f)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aportar a ${goal.title}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryBlue)

                    // Mostrar saldo actual como referencia
                    Text("Saldo disponible: $${availableBalance.toInt()}", color = SecondaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Faltan $${(goal.targetAmount - goal.savedAmount).toInt()}", color = TextMuted, fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = {
                            amountText = it
                            errorMessage = null // Limpiamos el error al escribir
                        },
                        label = { Text("Monto a sumar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(Color.Gray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage != null, // Pinta el borde rojo si hay error
                        supportingText = {
                            if (errorMessage != null) {
                                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { closeAndDismiss() }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = TextMuted)
                        }
                        Button(
                            onClick = {
                                val amount = amountText.toDoubleOrNull() ?: 0.0
                                if (amount > availableBalance) {
                                    errorMessage = "Saldo insuficiente."
                                } else if (amount > 0) {
                                    onConfirm(amount)
                                    closeAndDismiss()
                                } else {
                                    errorMessage = "Ingresa un monto válido."
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Aportar")
                        }
                    }
                }
            }
        }

    }

}

package com.tomastewater.deliveryfinance.presentation.goal

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tomastewater.deliveryfinance.core.designsystem.ConfirmDialog
import com.tomastewater.deliveryfinance.presentation.goal.components.AddMoneyAnimatedDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    onNavigateBack: () -> Unit,
    viewModel: GoalDetailViewModel = hiltViewModel()
) {
    val goal by viewModel.goal.collectAsState()
    val availableBalance by viewModel.availableBalance.collectAsState()

    // Si la meta no ha cargado, mostramos un loading
    if (goal == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentGoal = goal!! // Seguro usarlo aquí

    // Estados
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember(currentGoal) { mutableStateOf(currentGoal.title) }
    var editedAmount by remember(currentGoal) { mutableStateOf(currentGoal.targetAmount.toString()) }
    var editedLink by remember(currentGoal) { mutableStateOf(currentGoal.linkUrl) }
    var editedImageUrl by remember(currentGoal) { mutableStateOf(currentGoal.imageUrl) }

    var showSaveConfirm by remember { mutableStateOf(false) }
    var showAddMoney by remember { mutableStateOf(false) }

    val uriHandler = LocalUriHandler.current
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> if (uri != null) editedImageUrl = uri.toString() }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editando Meta" else "Detalles", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    if (isEditing) {
                        TextButton(onClick = { showSaveConfirm = true }) {
                            Text("Guardar", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF3F4F6))
            )
        },
        containerColor = Color(0xFFF3F4F6)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // IMAGEN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .clickable(enabled = isEditing) {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (editedImageUrl != null) {
                    AsyncImage(
                        model = editedImageUrl,
                        contentDescription = "Meta",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (isEditing) {
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = goalIconsMap[currentGoal.iconId] ?: Icons.Default.Image, contentDescription = null, tint = Color(0xFF757682), modifier = Modifier.size(64.dp))
                        if (isEditing) Text("Tocar para agregar imagen", color = Color(0xFF1E3A8A), modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }

            // INFO CARD
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    if (isEditing) {
                        OutlinedTextField(value = editedTitle, onValueChange = { editedTitle = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editedAmount, onValueChange = { editedAmount = it }, label = { Text("Monto Objetivo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editedLink, onValueChange = { editedLink = it }, label = { Text("Link Mercado Libre (Opcional)") }, modifier = Modifier.fillMaxWidth())
                    } else {
                        Text(currentGoal.title, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Guardado", color = Color(0xFF757682))
                                Text("$${currentGoal.savedAmount.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF006C49))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Objetivo", color = Color(0xFF757682))
                                Text("$${currentGoal.targetAmount.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                            }
                        }

                        LinearProgressIndicator(progress = { currentGoal.progressPercentage }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)), color = Color(0xFF006C49), trackColor = Color(0xFFF3F4F6))

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF757682), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Creado el ${dateFormatter.format(Date(currentGoal.createdAt))}", color = Color(0xFF757682))
                        }

                        if (currentGoal.linkUrl.isNotBlank()) {
                            Button(
                                onClick = { uriHandler.openUri(currentGoal.linkUrl) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF080), contentColor = Color(0xFF2D3277))
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ver en Mercado Libre", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // BOTÓN APORTAR
            AnimatedVisibility(visible = !isEditing && !currentGoal.isCompleted, enter = slideInVertically(initialOffsetY = { it }) + fadeIn(), exit = fadeOut()) {
                Button(
                    onClick = { showAddMoney = true },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Aportar Dinero", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // MODALES
    if (showSaveConfirm) {
        ConfirmDialog(
            title = "Guardar Cambios",
            message = "¿Deseas guardar las modificaciones de esta meta?",
            confirmText = "Guardar",
            isDestructive = false,
            onConfirm = {
                val updated = currentGoal.copy(
                    title = editedTitle,
                    targetAmount = editedAmount.toDoubleOrNull() ?: currentGoal.targetAmount,
                    linkUrl = editedLink,
                    imageUrl = editedImageUrl
                )
                viewModel.updateGoal(updated)
                isEditing = false
            },
            onDismiss = { showSaveConfirm = false }
        )
    }

    if (showAddMoney) {
        AddMoneyAnimatedDialog(
            goal = currentGoal,
            availableBalance = availableBalance,
            onDismiss = { showAddMoney = false },
            onConfirm = { amount ->
                viewModel.addMoney(amount)
                showAddMoney = false
            }
        )
    }
}
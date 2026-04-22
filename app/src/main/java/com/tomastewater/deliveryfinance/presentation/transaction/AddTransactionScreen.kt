package com.tomastewater.deliveryfinance.presentation.transaction

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
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
import com.tomastewater.deliveryfinance.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isIncome = state.transactionType == TransactionType.INCOME

    // Colores basados en el tipo
    val bgColor = if (isIncome) Color(0xFF00A2FF) else MaterialTheme.colorScheme.error
    val contentColor = Color.White

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isIncome) "NUEVO INGRESO" else "NUEVO GASTO") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                actions = {
                    IconButton(onClick = { viewModel.onSaveTransaction(onNavigateBack) }) {
                        Icon(Icons.Default.Check, "Guardar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor, titleContentColor = contentColor, navigationIconContentColor = contentColor, actionIconContentColor = contentColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(bgColor)) {
            AnimatedContent(
                targetState = state.isDetailView,
                transitionSpec = {
                    slideInHorizontally(initialOffsetX = { if (targetState) it else -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { if (targetState) -it else it })
                }, label = "view_transition"
            ) { isDetail ->
                if (isDetail) {
                    // --- VISTA 2: DETALLES EXTRA ---
                    TransactionDetailsView(state, viewModel)
                } else {
                    // --- VISTA 1: CALCULADORA RÁPIDA ---
                    QuickAmountView(state, viewModel, contentColor)
                }
            }
        }
    }
}

@Composable
fun QuickAmountView(state: AddTransactionUiState, viewModel: AddTransactionViewModel, contentColor: Color) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Zona Superior: Monto y Flecha
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$ ${state.amountStr}",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Botón de categoría rápida
                TextButton(
                    onClick = { /* Abrir modal de categorías */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                ) {
                    Text("Categoría: ${state.category}", fontSize = 18.sp)
                }
            }

            // FLECHA LATERAL DERECHA (Como en tu imagen)
            IconButton(
                onClick = { viewModel.toggleDetailView() },
                modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward, // o ArrowForwardIos si agregaste la librería
                    contentDescription = "Más detalles",
                    tint = contentColor // <-- El cambio está aquí
                )
            }
        }

        // Zona Inferior: Teclado Numérico Blanco
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            val keys = listOf("7", "8", "9", "4", "5", "6", "1", "2", "3", "00", "0", "DEL")
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(keys) { key ->
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).clickable {
                            if (key == "DEL") viewModel.onBackspace() else viewModel.onNumberPress(key)
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(Icons.Default.Backspace, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.onSurface)
                        } else {
                            Text(text = key, fontSize = 32.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDetailsView(state: AddTransactionUiState, viewModel: AddTransactionViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón para volver a la calculadora
        TextButton(onClick = { viewModel.toggleDetailView() }) {
            Text("← Volver al monto")
        }

        OutlinedTextField(
            value = state.note,
            onValueChange = { viewModel.onNoteChange(it) },
            label = { Text("Nota / Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.paymentMethod,
            onValueChange = { viewModel.onPaymentMethodChange(it) },
            label = { Text("Tipo de pago (Efectivo, Tarjeta, etc.)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.location,
            onValueChange = { viewModel.onLocationChange(it) },
            label = { Text("Lugar / Ubicación") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
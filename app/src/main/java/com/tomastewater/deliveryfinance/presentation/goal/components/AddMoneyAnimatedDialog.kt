package com.tomastewater.deliveryfinance.presentation.goal.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tomastewater.deliveryfinance.domain.model.Goal

@Composable
fun AddMoneyAnimatedDialog(
    goal: Goal,
    availableBalance: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var isVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aportar a ${goal.title}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E3A8A))
                    Text("Saldo disponible: $${availableBalance.toInt()}", color = Color(0xFF006C49), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Faltan $${(goal.targetAmount - goal.savedAmount).toInt()}", color = Color(0xFF757682), fontSize = 14.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it; errorMessage = null },
                        label = { Text("Monto a sumar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage != null,
                        supportingText = { if (errorMessage != null) Text(errorMessage!!, color = MaterialTheme.colorScheme.error) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { closeAndDismiss() }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = Color(0xFF757682))
                        }
                        Button(
                            onClick = {
                                val amount = amountText.toDoubleOrNull() ?: 0.0
                                if (amount > availableBalance) errorMessage = "Saldo insuficiente."
                                else if (amount > 0) { onConfirm(amount); closeAndDismiss() }
                                else errorMessage = "Ingresa un monto válido."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                        ) {
                            Text("Aportar")
                        }
                    }
                }
            }
        }
    }
}
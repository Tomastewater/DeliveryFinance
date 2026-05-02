package com.tomastewater.deliveryfinance.core.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String = "Confirmar", // Texto dinámico
    isDestructive: Boolean = false,    // true = Botón Rojo, false = Botón Azul
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

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
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1E3A8A))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(message, color = Color(0xFF757682), fontSize = 14.sp, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = { closeAndDismiss() }, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = Color(0xFF757682))
                        }
                        Button(
                            onClick = { onConfirm(); closeAndDismiss() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                // Si es destructivo (eliminar) se pone rojo, si no, azul oscuro
                                containerColor = if (isDestructive) Color(0xFFE53935) else Color(0xFF1E3A8A)
                            )
                        ) {
                            Text(confirmText, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
package com.tomastewater.deliveryfinance.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tomastewater.deliveryfinance.domain.model.Transaction

@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onConfirm: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {
    // Estados locales para los campos de texto
    var amountText by remember { mutableStateOf(transaction.amount.toInt().toString()) }
    var categoryText by remember { mutableStateOf(transaction.category) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Movimiento", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = { categoryText = it },
                    label = { Text("Categoría / Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto ($)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedAmount = amountText.toDoubleOrNull() ?: transaction.amount
                    if (categoryText.isNotBlank()) {
                        // Enviamos la transacción actualizada respetando el ID original
                        onConfirm(
                            transaction.copy(
                                amount = updatedAmount,
                                category = categoryText
                            )
                        )
                    }
                }
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
package com.tomastewater.deliveryfinance.presentation.goal.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomastewater.deliveryfinance.domain.model.Goal
import com.tomastewater.deliveryfinance.domain.usecase.goal.ProjectionResult
import com.tomastewater.deliveryfinance.presentation.goal.goalIconsMap


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
            .animateContentSize()
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
                    .background(Color(0xFFE0E7FF), RoundedCornerShape(8.dp)) // PrimaryLight
                    .padding(6.dp)) {
                    Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(20.dp))
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Opciones", tint = Color(0xFF757682))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(Color.White)) {
                        if (!goal.isPrincipal) {
                            DropdownMenuItem(text = { Text("Hacer principal", color = Color.Gray) }, onClick = { showMenu = false; onMakePrincipal() })
                        }
                        DropdownMenuItem(text = { Text("Completar manualmente", color = Color(0xFF006C49)) }, onClick = { showMenu = false; onComplete() })
                        DropdownMenuItem(text = { Text("Editar meta", color = Color.Gray) }, onClick = { showMenu = false; onEdit() })
                        DropdownMenuItem(text = { Text("Eliminar", color = Color.Red) }, onClick = { showMenu = false; onDelete() })
                    }
                }
            }
            Column {
                Text(goal.title, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), maxLines = 1)
                Text("$${goal.savedAmount.toInt()} / $${goal.targetAmount.toInt()}", fontSize = 12.sp, color = Color(0xFF757682))
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { goal.progressPercentage },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF006C49),
                    trackColor = Color(0xFFF3F4F6)
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
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(Color(0xFF6CF8BB).copy(alpha = 0.3f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = goalIconsMap[goal.iconId] ?: Icons.Default.Star, contentDescription = null, tint = Color(0xFF006C49))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(goal.title, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), fontSize = 16.sp)
                Text("Logrado: $${goal.targetAmount.toInt()}", color = Color(0xFF757682), fontSize = 14.sp)
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
            .border(2.dp, Color(0xFF757682).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = Color(0xFF757682), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nueva Meta", fontWeight = FontWeight.Bold, color = Color(0xFF757682))
        }
    }
}
package com.tomastewater.deliveryfinance.presentation.commitments

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tomastewater.deliveryfinance.ui.theme.BackgroundGray
import com.tomastewater.deliveryfinance.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitmentsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(

                title = { Text("Compromisos", fontWeight = FontWeight.Bold, color = PrimaryBlue) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGray)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("Pantalla de Compromisos en construcción 🚧", color = PrimaryBlue)
        }
    }
}
package com.example.voglioisoldi.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.voglioisoldi.ui.viewmodel.ChartPeriod

@Composable
fun PeriodSelector(
    selected: ChartPeriod,
    onSelect: (ChartPeriod) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf(
            ChartPeriod.DAILY to "Giorno",
            ChartPeriod.WEEKLY to "Settimana",
            ChartPeriod.MONTHLY to "Mese"
        ).forEach { (period, label) ->
            Button(
                onClick = { onSelect(period) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == period) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    contentColor = if (selected == period) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(20.dp)
            ) { Text(label) }
        }
    }
}
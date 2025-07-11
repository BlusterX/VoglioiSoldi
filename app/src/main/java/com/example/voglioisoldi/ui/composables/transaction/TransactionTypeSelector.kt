package com.example.voglioisoldi.ui.composables.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.voglioisoldi.ui.viewmodel.TransactionType


@Composable
fun TransactionTypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onTypeSelected(TransactionType.ENTRATA) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == TransactionType.ENTRATA)
                    Color(0xFF81C784)
                else
                    MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                "Entrata",
                color = if (selectedType == TransactionType.ENTRATA)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
        Button(
            onClick = { onTypeSelected(TransactionType.USCITA) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == TransactionType.USCITA)
                    Color(0xFFE57373)
                else
                    MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                "Uscita",
                color = if (selectedType == TransactionType.USCITA)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
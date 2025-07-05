package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTransactionScreen(
    navController: NavController
) {
    val viewModel: AuthViewModel = koinViewModel()
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Uscita") }
    var selectedCategory by remember { mutableStateOf("Spesa") }

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Aggiungi Transazione", style = MaterialTheme.typography.headlineSmall)
            // Importo
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Importo (€)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            // Descrizione
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrizione") },
                singleLine = true
            )
            // Tipo (Entrata / Uscita)
            Text("Tipo:", style = MaterialTheme.typography.labelLarge)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                //TODO: Bottoni semplicati??
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { type = "Entrata" }
                ) {
                    RadioButton(
                        selected = type == "Entrata",
                        onClick = null
                    )
                    Text("Entrata")
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { type = "Uscita" }
                ) {
                    RadioButton(
                        selected = type == "Uscita",
                        onClick = null
                    )
                    Text("Uscita")
                }
            }
            //TODO: Richiama composable in base al tipo
            DropdownMenuBox(
                selected = selectedCategory,
                items = listOf("Spesa", "Stipendio", "Trasporti", "Altro"),
                onItemSelected = { selectedCategory = it }
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    //TODO: Una volta premuto salva deve dare un pop-up di conferma
                        //Se preme "si" torna alla Home, in caso resta nella pagina
                        // e non succede niente
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salva")
            }
        }
    }
}


//TODO: in base alla "Tipo" deve essere un menu a tendina differente
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuBox(
    selected: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoria") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                //Deprecato??
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

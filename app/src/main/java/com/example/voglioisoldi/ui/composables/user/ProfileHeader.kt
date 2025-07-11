package com.example.voglioisoldi.ui.composables.user

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.voglioisoldi.ui.util.rememberProfilePictureLauncher

@Composable
fun ProfileHeader(
    name: String,
    surname: String,
    profilePictureUri: Uri? = null,
    onProfilePictureSelected: (Uri) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val initials = "${name.firstOrNull() ?: ""}${surname.firstOrNull() ?: ""}"
    val launcher = rememberProfilePictureLauncher { uri ->
        onProfilePictureSelected(uri)
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .clickable { expanded = true },
                contentAlignment = Alignment.Center
            ) {
                ProfilePicture(profilePictureUri, initials)

                // Menu to select the profile picture
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Scatta foto") },
                        onClick = {
                            expanded = false
                            launcher.captureImage()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Scegli dalla galleria") },
                        onClick = {
                            expanded = false
                            launcher.selectFromGallery()
                        }
                    )
                    if (profilePictureUri != null && profilePictureUri != Uri.EMPTY) {
                        DropdownMenuItem(
                            text = { Text("Rimuovi foto") },
                            onClick = {
                                expanded = false
                                showConfirmDialog = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(horizontal = 10.dp))

            Column {
                Text(
                    text = "$name $surname",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Profilo Utente",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Conferma rimozione") },
            text = { Text("Sei sicuro di voler rimuovere la foto profilo?") },
            confirmButton = {
                TextButton(onClick = {
                    onProfilePictureSelected(Uri.EMPTY)
                    showConfirmDialog = false
                }) {
                    Text("Rimuovi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
}

@Composable
private fun ProfilePicture(uri: Uri?, initials: String) {
    if (uri != null && uri != Uri.EMPTY) {
        AsyncImage(
            model = uri,
            contentDescription = "Foto profilo",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        // Immagine di default contenente le iniziali
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
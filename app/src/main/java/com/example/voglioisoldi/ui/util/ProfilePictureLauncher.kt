package com.example.voglioisoldi.ui.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

interface ProfilePictureLauncher {
    val selectedImageUri: Uri
    fun captureImage()
    fun selectFromGallery()
}

@Composable
fun rememberProfilePictureLauncher(
    onImageSelected: (imageUri: Uri) -> Unit = {}
): ProfilePictureLauncher {
    val ctx = LocalContext.current

    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
    var selectedImageUri by remember { mutableStateOf(Uri.EMPTY) }

    val cameraActivityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { pictureTaken ->
            if (!pictureTaken) return@rememberLauncherForActivityResult
            selectedImageUri = imageUri
            onImageSelected(selectedImageUri)
        }

    val galleryActivityLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                onImageSelected(selectedImageUri)
            }
        }

    val profilePictureLauncher = remember(cameraActivityLauncher, galleryActivityLauncher) {
        object : ProfilePictureLauncher {
            override val selectedImageUri get() = selectedImageUri

            override fun captureImage() {
                val imageFile = File
                    .createTempFile("profile_image", ".jpg", ctx.externalCacheDir)
                imageUri = FileProvider
                    .getUriForFile(ctx, ctx.packageName + ".provider", imageFile)
                cameraActivityLauncher.launch(imageUri)
            }

            override fun selectFromGallery() {
                galleryActivityLauncher.launch("image/*")
            }
        }
    }
    return profilePictureLauncher
}
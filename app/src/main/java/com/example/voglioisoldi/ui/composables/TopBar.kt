package com.example.voglioisoldi.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voglioisoldi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "VOGLIOiSOLDI",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        },
        navigationIcon = {
            if (showBackButton && onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_button),
                        contentDescription = "Torna indietro",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    )
}
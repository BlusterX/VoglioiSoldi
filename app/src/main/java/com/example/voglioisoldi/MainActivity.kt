package com.example.voglioisoldi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoglioiSoldiTheme {
                MyMainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "VOGLIOiSOLDI",
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
        }
    )
}

@Composable
fun MyBottomBar() {
    BottomAppBar(
        containerColor = Color.White,
        modifier = Modifier.height(120.dp)
    ) {
        IconButton(
            onClick = { /* Account */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.AccountCircle,
                contentDescription = "Account",
                modifier = Modifier.size(50.dp)
            )
        }
        IconButton(
            onClick = { /* Transaction */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.ShoppingCart,
                contentDescription = "Transactions",
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(Modifier.weight(1f, true))
        IconButton(
            onClick = { /* Settings */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(50.dp)
            )
        }
        IconButton(
            onClick = { /* Graphs */ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = "Graphs",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Composable
fun MyMainScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MyTopBar() },
        //TODO: To insert in another Composable(button add)
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add */ },
                modifier = Modifier.size(60.dp).offset(y = 45.dp),
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    modifier = Modifier.size(36.dp),
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = { MyBottomBar() }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.LightGray)
        )
    }
}


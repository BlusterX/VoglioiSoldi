package com.example.voglioisoldi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.voglioisoldi.ui.Navigation
import com.example.voglioisoldi.ui.theme.VoglioiSoldiTheme

//Al posto di Text("Screen 2") -> Text(stringResource(R.string.screen2_name))
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoglioiSoldiTheme {
                //TODO: Capire come mettere lo sfondo LightGray in ogni pagina dove si naviga(solo il contenuto)
                val navController = rememberNavController()
                Navigation(navController)
            }
        }
    }
}




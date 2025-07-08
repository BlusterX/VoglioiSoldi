package com.example.voglioisoldi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.voglioisoldi.ui.composables.BottomBar
import com.example.voglioisoldi.ui.composables.TopBar
import com.example.voglioisoldi.ui.viewmodel.ChartPeriod
import com.example.voglioisoldi.ui.viewmodel.ChartPoint
import com.example.voglioisoldi.ui.viewmodel.GraphsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.androidx.compose.koinViewModel


@Composable
fun GraphsScreen(
    navController: NavController
) {
    val viewModel: GraphsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                showBackButton = true,
                onBackClick = { navController.popBackStack() })
                 },
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text("Andamento Conto", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))
            PeriodSelector(
                selected = uiState.selectedPeriod,
                onSelect = viewModel::setPeriod
            )
            Spacer(Modifier.height(18.dp))

            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.points.isEmpty() -> Text("Nessun dato per il periodo selezionato.")
                else -> LineChartCompose(uiState.points)
            }
        }
    }
}

@Composable
fun LineChartCompose(points: List<ChartPoint>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                setNoDataText("Nessun dato")
                description.isEnabled = false
                axisRight.isEnabled = false
                legend.isEnabled = false
                xAxis.granularity = 1f
                xAxis.position = XAxis.XAxisPosition.BOTTOM
            }
        },
        update = { chart ->
            val entries = points.map { Entry(it.x, it.y) }
            val dataSet = LineDataSet(entries, "Saldo")
            dataSet.setDrawCircles(true)
            dataSet.circleRadius = 4f
            dataSet.setDrawValues(false)
            dataSet.lineWidth = 3f
            dataSet.setDrawFilled(true)
            dataSet.color = android.graphics.Color.rgb(33, 150, 243)
            dataSet.fillColor = android.graphics.Color.rgb(197, 225, 250)
            dataSet.valueTextColor = android.graphics.Color.BLACK

            chart.data = LineData(dataSet)
            chart.invalidate()
        }
    )
}
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
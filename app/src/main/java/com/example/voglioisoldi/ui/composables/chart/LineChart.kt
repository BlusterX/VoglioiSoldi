package com.example.voglioisoldi.ui.composables.chart

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.voglioisoldi.ui.viewmodel.ChartPoint
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun LineChartComposable(points: List<ChartPoint>, isDark: Boolean) {
    val axisTextColor = if (isDark) android.graphics.Color.WHITE else android.graphics.Color.BLACK
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
            dataSet.valueTextColor = axisTextColor
            chart.data = LineData(dataSet)
            chart.invalidate()
            chart.xAxis.textColor = axisTextColor
            chart.axisLeft.textColor = axisTextColor
            chart.axisRight.textColor = axisTextColor
        }
    )
}
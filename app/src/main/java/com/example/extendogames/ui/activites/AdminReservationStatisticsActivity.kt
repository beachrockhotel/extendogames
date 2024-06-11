package com.example.extendogames.ui.activites

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.AdminReservationStatisticsViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminReservationStatisticsActivity : AppCompatActivity() {

    private val viewModel: AdminReservationStatisticsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_statistics)

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val revenueChart = findViewById<LineChart>(R.id.revenueChart)
        val countChart = findViewById<LineChart>(R.id.countChart)

        val revenueTitle = findViewById<TextView>(R.id.revenueTitle)
        val countTitle = findViewById<TextView>(R.id.countTitle)
        val totalRevenueText = findViewById<TextView>(R.id.totalRevenueText)
        val totalReservationsText = findViewById<TextView>(R.id.totalReservationsText)

        viewModel.statistics.observe(this, Observer { statistics ->
            if (statistics != null) {
                val entriesRevenue = ArrayList<Entry>()
                val entriesCount = ArrayList<Entry>()
                val dates = ArrayList<String>()
                var index = 0
                var totalRevenue = 0f
                var totalCount = 0
                val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                val serverDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                statistics.forEach { (date, stat) ->
                    totalRevenue += stat.revenue.toFloat()
                    totalCount += stat.count
                    entriesRevenue.add(Entry(index.toFloat(), stat.revenue.toFloat()))
                    entriesCount.add(Entry(index.toFloat(), stat.count.toFloat()))

                    try {
                        val parsedDate = serverDateFormat.parse(date)
                        val formattedDate = dateFormat.format(parsedDate!!)
                        dates.add(formattedDate)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        dates.add(date) // На случай, если парсинг даты не удался, добавляем оригинальную дату
                    }
                    index++
                }

                totalRevenueText.text = "Общая выручка за период: $totalRevenue"
                totalReservationsText.text = "Количество бронирований за период: $totalCount"

                val dataSetRevenue = LineDataSet(entriesRevenue, "Выручка").apply {
                    color = Color.parseColor("#54408C")
                    lineWidth = 2f
                    setCircleColor(Color.parseColor("#54408C"))
                    circleRadius = 4f
                    valueTextSize = 12f // Размер текста значения
                }

                val dataSetCount = LineDataSet(entriesCount, "Бронирования").apply {
                    color = Color.parseColor("#54408C")
                    lineWidth = 2f
                    setCircleColor(Color.parseColor("#54408C"))
                    circleRadius = 4f
                    valueTextSize = 12f
                    valueFormatter = object : ValueFormatter() {
                        override fun getPointLabel(entry: Entry?): String {
                            return entry?.y?.toInt().toString()
                        }
                    }
                }

                val lineDataRevenue = LineData(dataSetRevenue)
                revenueChart.data = lineDataRevenue

                val lineDataCount = LineData(dataSetCount)
                countChart.data = lineDataCount

                revenueChart.xAxis.apply {
                    textSize = 14f
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return dates.getOrNull(value.toInt()) ?: value.toString()
                        }
                    }
                    position = XAxis.XAxisPosition.BOTTOM
                    setAvoidFirstLastClipping(true)
                }
                revenueChart.axisLeft.textSize = 14f
                revenueChart.axisRight.isEnabled = false
                revenueChart.description.isEnabled = false
                revenueChart.invalidate()

                countChart.xAxis.apply {
                    textSize = 14f
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                            return dates.getOrNull(value.toInt()) ?: value.toString()
                        }
                    }
                    position = XAxis.XAxisPosition.BOTTOM
                    setAvoidFirstLastClipping(true)
                }
                countChart.axisLeft.apply {
                    textSize = 14f
                    granularity = 1f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.toInt().toString()
                        }
                    }
                }
                countChart.axisRight.isEnabled = false
                countChart.description.isEnabled = false
                countChart.invalidate()

                revenueTitle.text = "Выручка"
                countTitle.text = "Бронирования"
            } else {
                Toast.makeText(this, "Failed to load statistics", Toast.LENGTH_SHORT).show()
            }
        })

        // Получаем текущий месяц
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -14)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().time)

        viewModel.fetchStatistics(startDate, endDate)
    }
}

package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.extendogames.R
import com.example.extendogames.ui.viewmodels.ReservationViewModel
import java.util.Calendar

class ReservationActivity : AppCompatActivity() {

    private val viewModel: ReservationViewModel by viewModels()

    private lateinit var endTimeView: TextView
    private lateinit var timeView: TextView
    private lateinit var hoursSpinner: Spinner
    private lateinit var gameStationStatusView: TextView
    private lateinit var deviceSpecsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        setupViews()
        setupObservers()

        viewModel.userProfile.observe(this, Observer { user ->
            user?.let {
                setupListeners()
            }
        })

        // Получаем данные о типе устройства и его характеристиках
        val deviceType = intent.getStringExtra("DEVICE_TYPE") ?: "Компьютер"
        val deviceSpecs = intent.getStringExtra("DEVICE_SPECS") ?: ""

        deviceSpecsView.text = deviceSpecs

        // Добавляем вызов updateEndTime сразу после установки значений для dateView и timeView
        val initialHours = hoursSpinner.selectedItem.toString().toInt()
        endTimeView.text = viewModel.updateEndTime(initialHours)

        val placeNumber = intent.getStringExtra("PLACE_NUMBER") ?: "1"
        val date = findViewById<TextView>(R.id.textView_date).text.toString()
        val time = findViewById<TextView>(R.id.textView_time).text.toString()
        val duration = hoursSpinner.selectedItem.toString().toInt()
        val costPerHour = 100.0 // Установите значение стоимости за час

        viewModel.checkStationAvailability(placeNumber, date, time, duration) { isAvailable ->
            runOnUiThread {
                gameStationStatusView.text = if (isAvailable) "Свободна" else "Занята"
            }
        }
    }

    private fun setupViews() {
        val backButton = findViewById<ImageView>(R.id.back_arrow)
        backButton.setOnClickListener { finish() }

        endTimeView = findViewById(R.id.end_time_view)
        timeView = findViewById(R.id.textView_time)
        hoursSpinner = findViewById(R.id.hours_spinner)
        gameStationStatusView = findViewById(R.id.game_station_status)
        deviceSpecsView = findViewById(R.id.textView6) // Обновим textView для характеристик устройства

        val dateView = findViewById<TextView>(R.id.textView_date)
        dateView.text = viewModel.dateFormat.format(Calendar.getInstance().time)
        timeView.text = viewModel.timeFormat.format(Calendar.getInstance().time)

        ArrayAdapter.createFromResource(
            this,
            R.array.hours_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            hoursSpinner.adapter = adapter
        }
    }

    private fun setupObservers() {
        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupListeners() {
        val dateView = findViewById<TextView>(R.id.textView_date)
        val registerButton = findViewById<Button>(R.id.register_button)

        dateView.setOnClickListener {
            viewModel.showDatePickerDialog(this) { date ->
                dateView.text = date
                Log.d("ReservationActivity", "Date selected: $date")
            }
        }

        timeView.setOnClickListener {
            viewModel.showTimePickerDialog(this, hoursSpinner.selectedItem.toString().toInt()) { time ->
                timeView.text = time
                // Обновление времени начала
                viewModel.updateStartTime(time)
                // Обновление времени окончания после выбора времени начала
                val endTime = viewModel.updateEndTime(hoursSpinner.selectedItem.toString().toInt())
                endTimeView.text = endTime
                Log.d("ReservationActivity", "Time selected: $time, End time: $endTime")
            }
        }

        hoursSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val endTime = viewModel.updateEndTime(hoursSpinner.selectedItem.toString().toInt())
                endTimeView.text = endTime
                Log.d("ReservationActivity", "Hours selected: ${hoursSpinner.selectedItem.toString()}, End time: $endTime")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        registerButton.setOnClickListener {
            val placeNumber = intent.getStringExtra("PLACE_NUMBER") ?: "1"
            val date = dateView.text.toString()
            val time = timeView.text.toString()
            val duration = hoursSpinner.selectedItem.toString().toInt()
            val costPerHour = 100.0 // Установите значение стоимости за час
            viewModel.checkAvailabilityAndReserve(placeNumber, date, time, duration, costPerHour)
            Log.d("ReservationActivity", "Register clicked: placeNumber=$placeNumber, date=$date, time=$time, duration=$duration")
        }
    }
}

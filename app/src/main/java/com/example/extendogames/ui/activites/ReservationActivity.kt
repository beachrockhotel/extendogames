package com.example.extendogames.ui.activites

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var costPerHourView: TextView

    private val premiumPlaces = setOf(8, 9, 10, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        setupViews()
        setupObservers()

        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        viewModel.userProfile.observe(this, Observer { user ->
            user?.let {
                setupListeners()
            }
        })

        val deviceType = intent.getStringExtra("DEVICE_TYPE") ?: "Компьютер"
        val deviceSpecs = intent.getStringExtra("DEVICE_SPECS") ?: ""

        deviceSpecsView.text = deviceSpecs

        val initialHours = hoursSpinner.selectedItem.toString().toInt()
        endTimeView.text = viewModel.updateEndTime(initialHours)

        val placeNumber = intent.getStringExtra("PLACE_NUMBER")?.toInt() ?: 1
        val date = findViewById<TextView>(R.id.textView_date).text.toString()
        val time = findViewById<TextView>(R.id.textView_time).text.toString()
        val duration = hoursSpinner.selectedItem.toString().toInt()

        val costPerHour = if (premiumPlaces.contains(placeNumber)) 200.0 else 100.0
        costPerHourView.text = "1 час - $costPerHour рублей"

        viewModel.checkStationAvailability(placeNumber.toString(), date, time, duration) { isAvailable ->
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
        deviceSpecsView = findViewById(R.id.textView6)
        costPerHourView = findViewById(R.id.cost_per_hour_view)

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
                viewModel.updateStartTime(time)
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
            val placeNumber = intent.getStringExtra("PLACE_NUMBER")?.toInt() ?: 1
            val date = dateView.text.toString()
            val time = timeView.text.toString()
            val duration = hoursSpinner.selectedItem.toString().toInt()
            val costPerHour = if (premiumPlaces.contains(placeNumber)) 200.0 else 100.0
            costPerHourView.text = "1 час - $costPerHour рублей"

            viewModel.checkAvailabilityAndReserve(placeNumber.toString(), date, time, duration, costPerHour)
            Log.d("ReservationActivity", "Register clicked: placeNumber=$placeNumber, date=$date, time=$time, duration=$duration")
        }
    }
}

package com.example.extendogames.ui.activites

import android.os.Bundle
import android.widget.ImageView
import android.widget.Button
import android.widget.Toast
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.models.ReservationRequest
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.models.User
import com.example.extendogames.api.responses.AvailabilityResponse
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class ReservationActivity : AppCompatActivity() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private lateinit var apiService: ApiService
    private var userEmail: String? = null
    private lateinit var userName: String
    private lateinit var endTimeView: TextView
    private lateinit var timeView: TextView
    private lateinit var hoursSpinner: Spinner
    private var startTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.extendogames.R.layout.activity_reservation)

        data class User(
            var name: String = "",
            var email: String = ""
        )


        userEmail = FirebaseAuth.getInstance().currentUser?.email
        setupApi()
        setupViews()
        initUserDataAndSetupListeners()
    }

    private fun setupApi() {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupViews() {
        val backButton = findViewById<ImageView>(com.example.extendogames.R.id.back_arrow)
        backButton.setOnClickListener { finish() }

        endTimeView = findViewById(com.example.extendogames.R.id.end_time_view)
        timeView = findViewById(com.example.extendogames.R.id.textView_time)
        hoursSpinner = findViewById(com.example.extendogames.R.id.hours_spinner)
    }

    private fun setupListeners() {
        val dateView = findViewById<TextView>(com.example.extendogames.R.id.textView_date)
        val registerButton = findViewById<Button>(com.example.extendogames.R.id.register_button)

        dateView.text = dateFormat.format(Calendar.getInstance().time)
        timeView.text = timeFormat.format(Calendar.getInstance().time)

        dateView.setOnClickListener {
            showDatePickerDialog { date -> dateView.text = date }
        }

        timeView.setOnClickListener {
            showTimePickerDialog { time -> timeView.text = time }
        }

        ArrayAdapter.createFromResource(
            this,
            com.example.extendogames.R.array.hours_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            hoursSpinner.adapter = adapter
        }

        hoursSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                updateEndTime()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        registerButton.setOnClickListener {
            val placeNumber = intent.getStringExtra("PLACE_NUMBER") ?: "1"
            val date = dateView.text.toString()
            val time = timeView.text.toString()
            val duration = hoursSpinner.selectedItem.toString().toInt()
            checkAvailabilityAndReserve(placeNumber, date, time, duration)
        }
    }

    private fun initUserDataAndSetupListeners() {
        getUserNameFromFirestore { userName ->
            this.userName = userName
            setupListeners()
        }
    }

    private fun updateEndTime() {
        val duration = hoursSpinner.selectedItem.toString().toInt()
        val endTime = (startTime.clone() as Calendar).apply {
            add(Calendar.HOUR, duration)
        }
        endTimeView.text = "Станция будет забронирована до: " + timeFormat.format(endTime.time)
    }

    private fun getUserNameFromFirestore(onResult: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_LONG).show()
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    onResult(user?.name ?: "Default User")
                } else {
                    Log.d("Firestore", "No such document")
                    onResult("Default User")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "get failed with ", exception)
                Toast.makeText(this, "Ошибка при загрузке данных пользователя: ${exception.message}", Toast.LENGTH_LONG).show()
                onResult("Default User")
            }
    }

    private fun checkAvailabilityAndReserve(placeNumber: String, date: String, time: String, duration: Int) {
        val checkRequest = ReservationRequest(placeNumber, date, time, duration, userEmail!!, userName)

        apiService.checkAvailability(checkRequest).enqueue(object : Callback<AvailabilityResponse> {
            override fun onResponse(call: Call<AvailabilityResponse>, response: Response<AvailabilityResponse>) {
                if (response.isSuccessful && response.body()?.available == true) {
                    reservePlace(placeNumber, date, time, duration)
                } else {
                    Toast.makeText(applicationContext, "Место уже забронировано в выбранное время", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<AvailabilityResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка при проверке доступности: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun reservePlace(placeNumber: String, date: String, time: String, duration: Int) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            Toast.makeText(this, "Пользователь не авторизован или адрес электронной почты недоступен", Toast.LENGTH_LONG).show()
            return
        }

        val reservationRequest = ReservationRequest(placeNumber, date, time, duration, userEmail, userName)
        apiService.reserveSeat(reservationRequest).enqueue(object : Callback<ReservationResponse> {
            override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Ошибка бронирования", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Ошибка сети: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showDatePickerDialog(onDateSet: (String) -> Unit) {
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSet(dateFormat.format(calendar.time))
        }, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePickerDialog(onTimeSet: (String) -> Unit) {
        TimePickerDialog(this, { _, hourOfDay, minute ->
            startTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            val formattedTime = timeFormat.format(startTime.time)
            onTimeSet(formattedTime)
            updateEndTime()
        }, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true).show()
    }
}

package com.example.extendogames.ui.viewmodels

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.ReservationRequest
import com.example.extendogames.api.models.ReservationResponse
import com.example.extendogames.api.models.User
import com.example.extendogames.api.responses.AvailabilityResponse
import com.example.extendogames.api.services.ApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationViewModel(application: Application) : AndroidViewModel(application) {

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val apiService: ApiService
    private var userEmail: String? = null
    private var userName: String = ""
    private var startTime: Calendar = Calendar.getInstance()

    init {
        userEmail = FirebaseAuth.getInstance().currentUser?.email

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .build()

        apiService = retrofit.create(ApiService::class.java)
        getUserNameFromFirestore()
    }

    private fun getUserNameFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            _message.value = "Пользователь не авторизован"
            return
        }
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    _userProfile.value = user
                    userName = user?.name ?: "Default User"
                } else {
                    _userProfile.value = User("Default User")
                    userName = "Default User"
                }
            }
            .addOnFailureListener { exception ->
                _message.value = "Ошибка при загрузке данных пользователя: ${exception.message}"
                _userProfile.value = User("Default User")
                userName = "Default User"
            }
    }

    fun checkStationAvailability(placeNumber: String, date: String, time: String, duration: Int, callback: (Boolean) -> Unit) {
        val request = ReservationRequest(placeNumber, date, time, duration, userEmail!!, userName)
        apiService.checkAvailability(request).enqueue(object : Callback<AvailabilityResponse> {
            override fun onResponse(call: Call<AvailabilityResponse>, response: Response<AvailabilityResponse>) {
                val isAvailable = response.isSuccessful && response.body()?.available ?: false
                callback(isAvailable)
            }

            override fun onFailure(call: Call<AvailabilityResponse>, t: Throwable) {
                callback(false)
            }
        })
    }

    fun checkAvailabilityAndReserve(placeNumber: String, date: String, time: String, duration: Int, costPerHour: Double) {
        val checkRequest = ReservationRequest(placeNumber, date, time, duration, userEmail!!, userName)

        apiService.checkAvailability(checkRequest).enqueue(object : Callback<AvailabilityResponse> {
            override fun onResponse(call: Call<AvailabilityResponse>, response: Response<AvailabilityResponse>) {
                if (response.isSuccessful && response.body()?.available == true) {
                    // Проверка баланса
                    val totalCost = duration * costPerHour
                    if (_userProfile.value?.balance ?: 0.0 >= totalCost) {
                        // Списание средств
                        _userProfile.value?.balance = _userProfile.value?.balance?.minus(totalCost) ?: 0.0
                        updateUserBalanceInFirestore(totalCost)
                        reservePlace(placeNumber, date, time, duration)
                    } else {
                        _message.value = "Недостаточно средств на балансе"
                    }
                } else {
                    _message.value = "Место уже забронировано в выбранное время"
                }
            }

            override fun onFailure(call: Call<AvailabilityResponse>, t: Throwable) {
                _message.value = "Ошибка при проверке доступности: ${t.message}"
            }
        })
    }

    private fun updateUserBalanceInFirestore(totalCost: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("Users").document(userId)
                .update("balance", FieldValue.increment(-totalCost))
                .addOnSuccessListener {
                    _message.value = "Средства успешно списаны"
                }
                .addOnFailureListener { e ->
                    _message.value = "Ошибка при обновлении баланса: ${e.message}"
                }
        }
    }

    private fun reservePlace(placeNumber: String, date: String, time: String, duration: Int) {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        if (userEmail == null) {
            _message.value = "Пользователь не авторизован или адрес электронной почты недоступен"
            return
        }

        val reservationRequest = ReservationRequest(placeNumber, date, time, duration, userEmail, userName)
        apiService.reserveSeat(reservationRequest).enqueue(object : Callback<ReservationResponse> {
            override fun onResponse(call: Call<ReservationResponse>, response: Response<ReservationResponse>) {
                if (response.isSuccessful) {
                    _message.value = response.body()?.message
                } else {
                    _message.value = "Ошибка бронирования"
                }
            }

            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                _message.value = "Ошибка сети: ${t.message}"
            }
        })
    }

    fun updateEndTime(hoursSpinner: Int): String {
        val endTime = (startTime.clone() as Calendar).apply {
            add(Calendar.HOUR, hoursSpinner)
        }
        val endTimeText = "Станция будет забронирована до: " + timeFormat.format(endTime.time)
        return endTimeText
    }

    fun updateStartTime(time: String) {
        startTime = Calendar.getInstance().apply {
            val parsedTime = timeFormat.parse(time)
            if (parsedTime != null) {
                timeInMillis = parsedTime.time
            }
        }
    }

    fun showDatePickerDialog(context: Context, onDateSet: (String) -> Unit) {
        DatePickerDialog(context, { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth)
            }
            onDateSet(dateFormat.format(calendar.time))
        }, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun showTimePickerDialog(context: Context, hoursSpinnerValue: Int, onTimeSet: (String) -> Unit) {
        TimePickerDialog(context, { _, hourOfDay, minute ->
            startTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            val formattedTime = timeFormat.format(startTime.time)
            onTimeSet(formattedTime)
            // Обновление времени окончания после выбора времени начала
            updateEndTime(hoursSpinnerValue)
        }, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true).show()
    }
}

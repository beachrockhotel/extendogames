package com.example.extendogames.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.extendogames.api.models.TournamentItem
import com.example.extendogames.api.responses.TournamentResponse
import com.example.extendogames.api.services.ApiService
import com.example.extendogames.api.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val _tournaments = MutableLiveData<List<TournamentItem>>()
    val tournaments: LiveData<List<TournamentItem>> get() = _tournaments

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val apiService: ApiService = RetrofitClient.instance

    init {
        loadTournaments()
    }

    private fun loadTournaments() {
        apiService.getTournaments().enqueue(object : Callback<TournamentResponse> {
            override fun onResponse(call: Call<TournamentResponse>, response: Response<TournamentResponse>) {
                if (response.isSuccessful) {
                    _tournaments.value = response.body()?.tournaments
                } else {
                    _message.value = "Ошибка: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<TournamentResponse>, t: Throwable) {
                _message.value = "Не удалось загрузить турниры: ${t.message}"
            }
        })
    }
}

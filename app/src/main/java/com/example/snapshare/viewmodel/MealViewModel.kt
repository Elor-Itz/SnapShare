package com.example.snapshare.viewmodel

import androidx.lifecycle.*
import com.example.snapshare.data.remote.Meal
import com.example.snapshare.data.remote.MealApiService
import com.example.snapshare.data.remote.MealRemote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MealViewModel : ViewModel() {
    private val mealRemote = MealRemote(MealApiService.create())

    private val _meals = MutableLiveData<List<Meal>>()
    val meals: LiveData<List<Meal>> get() = _meals

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun searchMeals(query: String) {
        _loading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = mealRemote.searchMeals(query)
                _meals.postValue(result)
            } catch (e: Exception) {
                _meals.postValue(emptyList())
            } finally {
                _loading.postValue(false)
            }
        }
    }
}
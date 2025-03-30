package com.example.snapshare.data.remote

class MealRemote(private val mealApiService: MealApiService) {

    suspend fun searchMeals(query: String): List<Meal> {
        val response = mealApiService.searchMeals(query)
        return response.meals ?: emptyList()
    }
}
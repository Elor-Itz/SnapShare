package com.example.snapshare.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.snapshare.data.remote.Meal
import com.example.snapshare.databinding.ItemMealBinding
import com.squareup.picasso.Picasso

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {
    private val meals = mutableListOf<Meal>()

    fun submitList(newMeals: List<Meal>) {
        meals.clear()
        meals.addAll(newMeals)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal) {
            binding.mealName.text = meal.strMeal
            Picasso.get().load(meal.strMealThumb).into(binding.mealImage)
        }
    }
}
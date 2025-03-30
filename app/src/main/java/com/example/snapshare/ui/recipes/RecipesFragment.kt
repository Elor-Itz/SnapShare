package com.example.snapshare.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapshare.databinding.FragmentRecipesBinding
import com.example.snapshare.viewmodel.MealViewModel

class RecipesFragment : Fragment() {
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private val mealViewModel: MealViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MealAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        mealViewModel.meals.observe(viewLifecycleOwner) { meals ->
            if (meals.isEmpty()) {
                Toast.makeText(requireContext(), "No meals found", Toast.LENGTH_SHORT).show()
            }
            adapter.submitList(meals)
        }

        mealViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Trigger search
        mealViewModel.searchMeals("chicken")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
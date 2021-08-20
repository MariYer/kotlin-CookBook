package com.mariyer.cookbook.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.navigation.NavigationBarView
import com.mariyer.cookbook.R
import com.mariyer.cookbook.databinding.FragmentListBinding
import com.mariyer.cookbook.ui.adapters.RecipeListAdapter
import com.mariyer.cookbook.ui.viewmodels.RecipeViewModel
import com.mariyer.cookbook.utils.AutoClearedValue
import com.mariyer.cookbook.utils.debug
import com.mariyer.cookbook.utils.setItemSelectedFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RecipesListFragment : Fragment(R.layout.fragment_list) {
    private val binding by viewBinding<FragmentListBinding>(FragmentListBinding::class.java)
    private val viewModel: RecipeViewModel by activityViewModels()
    private var recipeAdapter: RecipeListAdapter by AutoClearedValue()
    private var previousPage = R.id.pageMain


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        viewModel.recipes.observe(viewLifecycleOwner) { recipeAdapter.items = it }
        viewModel.bind(
            binding.categoriesNavigationRail.setItemSelectedFlow(),
            setFavoriteSelectFlow()
        )
        if (viewModel.currentCategory == null) {
            binding.categoriesNavigationRail.selectedItemId = R.id.salads
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.selectedItemId = previousPage
    }
    private fun setFavoriteSelectFlow(): Flow<Boolean> {
            return callbackFlow<Boolean> {
                val itemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
                    debug("setFavoriteSelectFlow", "${item.itemId}")
                    when (item.itemId) {
                        R.id.pageMain -> {
                            sendBlocking(false)
                            previousPage = binding.bottomNavigation.selectedItemId
                            true
                        }
                        R.id.pageAdd -> {
                            previousPage = binding.bottomNavigation.selectedItemId
                            val action =
                                RecipesListFragmentDirections.actionRecipesListFragmentToRecipeAddFragment(
                                    getCategoryName(binding.categoriesNavigationRail.selectedItemId)
                                )
                            findNavController().navigate(action)
                            sendBlocking(false)
                            true
                        }
                        R.id.pageFavorite -> {
                            debug("setFavoriteSelectFlow", "Favorites")
                            sendBlocking(true)
                            previousPage = binding.bottomNavigation.selectedItemId
                            true
                        }
                        R.id.pageOther -> {
                            findNavController().navigate(R.id.action_recipesListFragment_to_otherFragment)
                            sendBlocking(false)
                            previousPage = binding.bottomNavigation.selectedItemId
                            true
                        }
                        else -> {
                            debug("setFavoriteSelectFlow", "not Favorites")
                            sendBlocking(false)
                            false
                        }
                    }
                }
                binding.bottomNavigation.setOnItemSelectedListener(itemSelectedListener)
                awaitClose {
                    binding.bottomNavigation.setOnItemSelectedListener(null)
                }
            }
    }

    private fun getCategoryName(itemId: Int): String {
        return when (itemId) {
            R.id.salads -> "salads"
            R.id.soups -> "soups"
            R.id.secondDishes -> "second_dishes"
            R.id.desserts -> "desserts"
            R.id.bake -> "bake"
            R.id.drinks -> "drinks"
            else -> ""
        }
    }

    private fun initList() {
        recipeAdapter = RecipeListAdapter(requireContext(),
            {
                previousPage = binding.bottomNavigation.selectedItemId
                val action =
                    RecipesListFragmentDirections.actionRecipesListFragmentToRecipeDetailsFragment(
                        it
                    )
                findNavController().navigate(action)
            },
            {
                previousPage = binding.bottomNavigation.selectedItemId
                val action =
                    RecipesListFragmentDirections.actionRecipesListFragmentToRecipeEditFragment(
                        it
                    )
                findNavController().navigate(action)
            },
            {
                viewModel.deleteRecipe(it)
            },
            {
                viewModel.changeFavorite(it)
            }
        )
        with(binding.recipesList) {
            adapter = recipeAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
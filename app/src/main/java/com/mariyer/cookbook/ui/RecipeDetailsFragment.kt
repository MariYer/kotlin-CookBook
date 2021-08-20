package com.mariyer.cookbook.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.mariyer.cookbook.R
import com.mariyer.cookbook.data.db.model.RecipeTag
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import com.mariyer.cookbook.databinding.FragmentViewDetailBinding
import com.mariyer.cookbook.ui.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.item_card.*

class RecipeDetailsFragment : Fragment(R.layout.fragment_view_detail) {
    private val binding: FragmentViewDetailBinding by viewBinding(FragmentViewDetailBinding::class.java)
    private val viewModel: RecipeViewModel by activityViewModels()
    private val args: RecipeDetailsFragmentArgs by navArgs()
    private var tagsRecipe: MutableList<RecipeTag> = mutableListOf()
    private var tagsAll: MutableList<RecipeTag> = mutableListOf()
    private var currentRecipeId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let { showInfo(it) }
        }
        viewModel.getRecipeById(args.recipeId)
        currentRecipeId = args.recipeId
    }

    private fun showCheckedTags() {
        binding.tagsChipGroup.removeAllViews()
        tagsRecipe.forEach { item ->
            val chipTag = Chip(requireContext())
            chipTag.setText(item.tag)
            chipTag.setChipBackgroundColorResource(R.color.colorPrimaryLight)
            chipTag.setTextColor(requireContext().getColor(R.color.colorPrimaryText))
            chipTag.tag = item.tag
            chipTag.isCheckable = true
            chipTag.isChecked = true
            tagsChipGroup.addView(chipTag)
        }
    }

    private fun showInfo(recipe: RecipeWithTags) {
        binding.titleTextView.text = recipe.recipe.title
        binding.productsTextView.text = recipe.recipe.products
        binding.technologyTextView.text = recipe.recipe.technology

        Glide.with(this)
            .load(recipe.recipe.localUrl)
            .placeholder(R.drawable.ic_block)
            .error(R.drawable.ic_error)
            .into(binding.dishImageView)

        tagsRecipe.clear()
        tagsRecipe.addAll(recipe.tags)
        showCheckedTags()
    }
}
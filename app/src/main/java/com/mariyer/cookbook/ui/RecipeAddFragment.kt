package com.mariyer.cookbook.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.mariyer.cookbook.R
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeTag
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import com.mariyer.cookbook.databinding.FragmentEditDetailBinding
import com.mariyer.cookbook.ui.viewmodels.RecipeViewModel
import com.mariyer.cookbook.utils.debug
import kotlinx.android.synthetic.main.item_card.*

class RecipeAddFragment: Fragment(R.layout.fragment_edit_detail) {
    private val binding: FragmentEditDetailBinding by viewBinding(FragmentEditDetailBinding::class.java)
    private val viewModel: RecipeViewModel by activityViewModels()
    private val args: RecipeAddFragmentArgs by navArgs()
    private var category: String? = null
    private var localUrl: String? = null
    private var tagsRecipe: MutableList<RecipeTag> = mutableListOf()
    private var tagsAll: MutableList<RecipeTag> = mutableListOf()
    private var currentRecipeId: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTagButton.setOnClickListener {
            addRecipeTag()
            viewModel.getAllTags()
            debug("addRecipeTag", tagsRecipe.toString())
        }

        binding.saveButton.setOnClickListener {

            viewModel.insertRecipe(
                RecipeWithTags(
                    Recipe(
                        0L,
                        args.category,
                        binding.titleEditText.text.toString(),
                        binding.urlEditText.text.toString(),
                        binding.productsEditText.text.toString(),
                        binding.technologyEditText.text.toString(),
                        localUrl,
                        false
                    ),
                    tagsRecipe
                )
            )
            findNavController().popBackStack()
        }

        viewModel.localUrl.observe(viewLifecycleOwner) {
            localUrl = it
            localUrl?.let {
                Glide.with(this)
                    .load(localUrl)
                    .placeholder(R.drawable.ic_block)
                    .error(R.drawable.ic_error)
                    .into(binding.dishImageView)
            }

        }
        binding.downloadImageButton.setOnClickListener { viewModel.downloadImage(binding.urlEditText.text.toString()) }
        viewModel.allTags.observe(viewLifecycleOwner) {
            showAllTags(it)
        }
        viewModel.getAllTags()
        currentRecipeId = 0L
    }

    private fun addRecipeTag() {
        val dialogAddTagView =
            requireActivity().layoutInflater.inflate(R.layout.dialog_add_tag, null)
        val tagEditText: EditText? = dialogAddTagView?.findViewById(R.id.tagEditText)
        var newTag: String? = null

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить новый тэг")
            .setView(dialogAddTagView)
            .setPositiveButton("ОК") { _, _ ->
                val recipeTag = RecipeTag(0L, currentRecipeId!!, tagEditText?.text.toString())
                viewModel.addRecipeTag(recipeTag)
                viewModel.getAllTags()
            }
            .setNegativeButton("Отмена", { _, _ -> })
            .create()
            .show()
    }

    private fun showAllTags(list: List<RecipeTag>) {
        tagsAll.clear()
        tagsAll.addAll(list)
        tagsAll.forEach { tag ->
            val chipTag = Chip(requireContext())
            chipTag.setText(tag.tag)
            chipTag.setChipBackgroundColorResource(R.color.colorPrimaryLight)
            chipTag.setTextColor(requireContext().getColor(R.color.colorPrimaryText))
            chipTag.tag = tag.tag
            chipTag.isCheckable = true
            chipTag.isChecked = false
            chipTag.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (tagsRecipe.find { it.tag == buttonView.text.toString()} == null) {
                        tagsRecipe.add(RecipeTag(0L, currentRecipeId, buttonView.text.toString()))
                    }
                } else {
                    tagsRecipe.removeIf {
                        it.tag == buttonView.text.toString()
                    }
                }
            }
            tagsChipGroup.addView(chipTag)
        }
    }

    private fun showCheckedTags() {
        binding.tagsChipGroup.clearCheck()
        tagsRecipe.forEach { item ->
            val chipTag = binding.tagsChipGroup.findViewWithTag<Chip>(item.tag)
            chipTag?.isChecked = true
        }
    }
}
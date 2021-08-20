package com.mariyer.cookbook.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeWithTags

class RecipeListAdapter(
    private val context: Context,
    private val onItemClick: (id: Long) -> Unit,
    private val onEditClick: (id: Long) -> Unit,
    private val onDeleteClick: (id: Long) -> Unit,
    private val onFavoriteClick: (id: Long) -> Unit
) : AsyncListDifferDelegationAdapter<RecipeWithTags>(RecipeWithTagsDiffUtilsCallback()) {

    init {
        delegatesManager.addDelegate(RecipeListAdapterDelegate(context, onItemClick, onEditClick, onDeleteClick, onFavoriteClick))
    }

    class RecipeWithTagsDiffUtilsCallback : DiffUtil.ItemCallback<RecipeWithTags>() {
        override fun areItemsTheSame(oldItem: RecipeWithTags, newItem: RecipeWithTags): Boolean {
            return oldItem is RecipeWithTags && newItem is RecipeWithTags && oldItem.recipe.id == newItem.recipe.id
        }

        override fun areContentsTheSame(oldItem: RecipeWithTags, newItem: RecipeWithTags): Boolean {
            return oldItem == newItem
        }

    }
}
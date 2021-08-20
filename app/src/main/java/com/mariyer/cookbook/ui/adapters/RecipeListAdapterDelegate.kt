package com.mariyer.cookbook.ui.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.mariyer.cookbook.R
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import com.mariyer.cookbook.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_card.*

class RecipeListAdapterDelegate(
    private val context: Context,
    private val onItemClick: (id: Long) -> Unit,
    private val onEditClick: (id: Long) -> Unit,
    private val onDeleteClick: (id: Long) -> Unit,
    private val onFavoriteClick: (id: Long) -> Unit
) : AbsListItemAdapterDelegate<RecipeWithTags, RecipeWithTags, RecipeListAdapterDelegate.Holder>() {

    override fun isForViewType(
        item: RecipeWithTags,
        items: MutableList<RecipeWithTags>,
        position: Int
    ): Boolean {
        return item is RecipeWithTags
    }

    override fun onCreateViewHolder(parent: ViewGroup): Holder {
        return Holder(parent.inflate(R.layout.item_card), context, onItemClick, onEditClick, onDeleteClick, onFavoriteClick)
    }

    override fun onBindViewHolder(
        item: RecipeWithTags,
        holder: Holder,
        payloads: MutableList<Any>
    ) {
        holder.bind(item)
    }

    class Holder(
        override val containerView: View,
        private val context: Context,
        private val onItemClick: (id: Long) -> Unit,
        private val onEditClick: (id: Long) -> Unit,
        private val onDeleteClick: (id: Long) -> Unit,
        private val onFavoriteClick: (id: Long) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var currentId: Long? = null

        init {
            containerView.setOnClickListener {
                currentId?.let { id ->
                    onItemClick(id)
                }
            }
            editButton.setOnClickListener {
                currentId?.let {
                    onEditClick(it)
                }
            }
            deleteButton.setOnClickListener {
                currentId?.let {
                    onDeleteClick(it)
                }
            }
            favoriteImageView.setOnClickListener {
                currentId?.let {
                    onFavoriteClick(it)
                }

            }
        }

        fun bind(item: RecipeWithTags) {
            currentId = item.recipe.id

            titleTextView.text = item.recipe.title

            Glide.with(itemView)
                .load(item.recipe.localUrl)
                .placeholder(R.drawable.ic_block)
                .error(R.drawable.ic_error)
                .into(dishImageView)

            if (item.recipe.isFavorite ?: false) {
                favoriteImageView.setImageDrawable(context.getDrawable(R.drawable.ic_star))
            } else {
                favoriteImageView.setImageDrawable(context.getDrawable(R.drawable.ic_star_border))
            }

            tagsChipGroup.removeAllViews()
            item.tags.forEach { recipeTag ->
                val chipTag = Chip(context)
                chipTag.setText(recipeTag.tag)
                chipTag.setChipBackgroundColorResource(R.color.colorPrimaryLight)
                chipTag.setTextColor(context.getColor(R.color.colorPrimaryText))
                tagsChipGroup.addView(chipTag)
            }

        }

    }

}

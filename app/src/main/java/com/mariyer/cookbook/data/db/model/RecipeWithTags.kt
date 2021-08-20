package com.mariyer.cookbook.data.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.mariyer.cookbook.data.db.contracts.RecipeContract
import com.mariyer.cookbook.data.db.contracts.RecipeTagContract

data class RecipeWithTags(
    @Embedded
    val recipe: Recipe,
    @Relation(
        parentColumn = RecipeContract.Columns.ID,
        entityColumn = RecipeTagContract.Columns.RECIPE_ID
    )
    val tags: List<RecipeTag>
)

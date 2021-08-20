package com.mariyer.cookbook.data.db.contracts

object RecipeTagContract {
    const val TABLE_NAME = "recipe_tags"

    object Columns {
        const val ID = "id"
        const val RECIPE_ID = "recipe_id"
        const val TAG = "tag"
    }
}
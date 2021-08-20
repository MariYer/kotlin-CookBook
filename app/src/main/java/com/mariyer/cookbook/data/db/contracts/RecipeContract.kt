package com.mariyer.cookbook.data.db.contracts

object RecipeContract {
    const val TABLE_NAME = "recipes"

    object Columns {
        const val ID = "id"
        const val CATEGORY = "category"
        const val TITLE = "title"
        const val PRODUCTS = "products"
        const val TECHNOLOGY = "technology"
        const val REMOTE_URL = "remote_url"
        const val LOCAL_URL = "local_url"
        const val IS_FAVORITE = "favorite"
    }
}
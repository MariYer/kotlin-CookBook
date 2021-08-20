package com.mariyer.cookbook.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mariyer.cookbook.data.db.contracts.RecipeContract

@Entity(
    tableName = RecipeContract.TABLE_NAME,
    indices = [
        Index(
            RecipeContract.Columns.CATEGORY
        ),
        Index(
            RecipeContract.Columns.IS_FAVORITE
        )]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RecipeContract.Columns.ID)
    val id: Long,
    @ColumnInfo(name = RecipeContract.Columns.CATEGORY)
    val category: String,
    @ColumnInfo(name = RecipeContract.Columns.TITLE)
    val title: String,
    @ColumnInfo(name = RecipeContract.Columns.REMOTE_URL)
    val remoteUrl: String?,
    @ColumnInfo(name = RecipeContract.Columns.PRODUCTS)
    val products: String?,
    @ColumnInfo(name = RecipeContract.Columns.TECHNOLOGY)
    val technology: String?,
    @ColumnInfo(name = RecipeContract.Columns.LOCAL_URL)
    var localUrl: String?,
    @ColumnInfo(name = RecipeContract.Columns.IS_FAVORITE, defaultValue = "0")
    var isFavorite: Boolean?
)
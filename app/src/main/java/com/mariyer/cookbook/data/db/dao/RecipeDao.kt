package com.mariyer.cookbook.data.db.dao

import androidx.room.*
import com.mariyer.cookbook.data.db.contracts.RecipeContract
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM ${RecipeContract.TABLE_NAME} WHERE ${RecipeContract.Columns.CATEGORY}=:category")
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>

    @Query("SELECT * FROM ${RecipeContract.TABLE_NAME} WHERE ${RecipeContract.Columns.CATEGORY}=:category")
    fun getRecipesWithTagsByCategoryFlow(category: String): Flow<List<RecipeWithTags>>


    @Query("SELECT * FROM ${RecipeContract.TABLE_NAME} WHERE ${RecipeContract.Columns.CATEGORY}=:category")
    suspend fun getRecipesWithTagsByCategory(category: String): List<RecipeWithTags>

    @Query("SELECT * FROM ${RecipeContract.TABLE_NAME} \n"+
            " WHERE ${RecipeContract.Columns.IS_FAVORITE}=1 \n" +
            " AND ${RecipeContract.Columns.CATEGORY}=:category")
    suspend fun getFavoriteRecipesByCategory(category: String): List<RecipeWithTags>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Query("DELETE FROM ${RecipeContract.TABLE_NAME} WHERE ${RecipeContract.Columns.ID} = :recipeId")
    suspend fun removeRecipe(recipeId: Long)

    @Query("SELECT * FROM ${RecipeContract.TABLE_NAME} WHERE ${RecipeContract.Columns.ID} = :recipeId")
    suspend fun getRecipeById(recipeId: Long): RecipeWithTags
}
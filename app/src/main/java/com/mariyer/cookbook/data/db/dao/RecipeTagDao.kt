package com.mariyer.cookbook.data.db.dao

import androidx.room.*
import com.mariyer.cookbook.data.db.contracts.RecipeContract
import com.mariyer.cookbook.data.db.contracts.RecipeTagContract
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeTag
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeTagDao {

    @Query("SELECT MAX(${RecipeTagContract.Columns.ID}) AS ${RecipeTagContract.Columns.ID}," +
            "            MAX(${RecipeTagContract.Columns.RECIPE_ID}) AS ${RecipeTagContract.Columns.RECIPE_ID}," +
            "            ${RecipeTagContract.Columns.TAG}" +
            "       FROM ${RecipeTagContract.TABLE_NAME}" +
            "   GROUP BY ${RecipeTagContract.Columns.TAG}")
    suspend fun getAllTags(): List<RecipeTag>

    @Query("SELECT * FROM ${RecipeTagContract.TABLE_NAME} WHERE ${RecipeTagContract.Columns.RECIPE_ID} = :recipeId")
    suspend fun getTagsByRecipe(recipeId: Long): List<RecipeTag>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<RecipeTag>)

    @Update
    suspend fun updateRecipeTag(tag: RecipeTag)

    @Query("DELETE FROM ${RecipeTagContract.TABLE_NAME} WHERE ${RecipeTagContract.Columns.ID} = :tagId")
    suspend fun removeRecipeTag(tagId: Long)

    @Query("DELETE FROM ${RecipeTagContract.TABLE_NAME}" +
            " WHERE ${RecipeTagContract.Columns.RECIPE_ID} IS NULL" +
            " AND ${RecipeTagContract.Columns.TAG} IN (SELECT DISTINCT RT.${RecipeTagContract.Columns.TAG} FROM ${RecipeTagContract.TABLE_NAME} AS RT " +
            " WHERE RT.${RecipeTagContract.Columns.RECIPE_ID} = :recipeId)")
    suspend fun removeUnlinkedTags(recipeId: Long)

    @Query("DELETE FROM ${RecipeTagContract.TABLE_NAME} WHERE ${RecipeTagContract.Columns.RECIPE_ID} = :recipeId")
    suspend fun removeTagsByRecipe(recipeId: Long)
}
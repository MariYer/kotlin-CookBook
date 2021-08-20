package com.mariyer.cookbook.data

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.mariyer.cookbook.data.db.Database
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeTag
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import com.mariyer.cookbook.data.network.Network
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.nio.file.Paths

class CookRepository(
    val context: Context
) {
    private val recipeDao = Database.instance.recipeDao()
    private val recipeTagDao = Database.instance.recipeTagDao()

    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun observeRecipesByCategory(category: String): Flow<List<RecipeWithTags>> {
        return recipeDao.getRecipesWithTagsByCategoryFlow(category.lowercase().trim())
    }

    suspend fun getAllRecipeTags(): List<RecipeTag> {
        return recipeTagDao.getAllTags()
    }

    suspend fun getRecipesByCategory(category: String): List<RecipeWithTags> {
        return recipeDao.getRecipesWithTagsByCategory(category.lowercase().trim())
    }

    suspend fun getFavoriteRecipesByCategory(category: String): List<RecipeWithTags> {
        return recipeDao.getFavoriteRecipesByCategory(category.lowercase().trim())
    }

    suspend fun deleteRecipe(recipeId: Long) {
        recipeDao.removeRecipe(recipeId)
    }

    suspend fun getRecipeById(recipeId: Long): RecipeWithTags {
        return recipeDao.getRecipeById(recipeId)
    }

    suspend fun downloadImage(url: String, fileName: String): String {
        val folder = context.getExternalFilesDir("images")
        val file = File(folder!!, fileName)

        file.outputStream().use { fileOutputStream ->
            Network.api
                .getFile(url)
                .byteStream()
                .use { fileInputStream ->
                    fileInputStream.copyTo(fileOutputStream)
                }
        }
        return file.toURI().toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFileNames(url: String): Map<String, String> {
        val remoteFileName: String = Paths.get(url).getFileName().toString()
        val localFileName = remoteFileName

        return mapOf(
            "remote" to remoteFileName.orEmpty(),
            "local" to localFileName.orEmpty()
        )
    }

    suspend fun insertRecipe(recipe: Recipe): Long {
        return recipeDao.insertRecipe(recipe)
    }

    suspend fun insertTags(tags: List<RecipeTag>) {
        recipeTagDao.insertTags(tags)
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipe(recipe)
    }

    suspend fun updateTag(tag: RecipeTag) {
        recipeTagDao.updateRecipeTag(tag)
    }

    suspend fun updateTagsByRecipe(recipeId: Long, tags: List<RecipeTag>) {
        recipeTagDao.removeTagsByRecipe(recipeId)
        tags.map {
            it.recipeId = recipeId
        }
        recipeTagDao.insertTags(tags)
    }

    fun addTagToList(list: List<RecipeTag>, recipeTag: RecipeTag): List<RecipeTag> {
        return list + listOf(recipeTag)
    }

    suspend fun deleteUnlinkedTags(recipeId: Long) {
        recipeTagDao.removeUnlinkedTags(recipeId)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "loaded_files"
        private const val KEY_LAUNCH = "wasLauched"
    }
}
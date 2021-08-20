package com.mariyer.cookbook.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.mariyer.cookbook.data.CookRepository
import com.mariyer.cookbook.data.db.model.RecipeTag
import com.mariyer.cookbook.data.db.model.RecipeWithTags
import com.mariyer.cookbook.utils.debug
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = CookRepository(application)

    var currentCategory: String? = null
        private set

    var currentFavorite: Boolean? = null
        private set

    private val mutableRecipes = MutableLiveData<List<RecipeWithTags>>()
    val recipes: LiveData<List<RecipeWithTags>> = mutableRecipes

    private val mutableRecipe = MutableLiveData<RecipeWithTags?>()
    val recipe: LiveData<RecipeWithTags?> = mutableRecipe

    private val mutableLocalUrl = MutableLiveData<String?>()
    val localUrl: LiveData<String?> = mutableLocalUrl

    private val mutableAllTags = MutableLiveData<List<RecipeTag>>()
    val allTags: LiveData<List<RecipeTag>> = mutableAllTags

    val allRecipes: LiveData<List<RecipeWithTags>>
        get() = repository.observeRecipesByCategory(currentCategory ?: "none")
            .onEach {
                mutableRecipes.postValue(it)
            }
            .asLiveData(viewModelScope.coroutineContext)

    fun getAllTags() {
        viewModelScope.launch {
            try {
                mutableAllTags.postValue(repository.getAllRecipeTags())
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "getAllTags: ${t.message}")
            }
        }
    }

    fun deleteRecipe(recipeId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(recipeId)
                currentCategory?.let {cat ->
                    if (currentFavorite?: false) {
                        repository.getFavoriteRecipesByCategory(cat)
                    } else {
                        repository.getRecipesByCategory(cat)
                    }
                }
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "deleteRecipe: ${t.message}")
            }
        }
    }

    fun bind(queryFlow: Flow<String>, favoriteFlow: Flow<Boolean>) {
        combine(
            queryFlow,
            favoriteFlow,
            { category, favorite -> category to favorite }
        )
            .debounce(3000)
            .onEach { (category, favorite) ->
                debug("RecipeViewModel: bind", "start = $category $favorite")
            }
            .distinctUntilChanged()
            .mapLatest { (category, favorite) ->
                currentCategory = category
                currentFavorite = favorite
                debug("RecipeViewModel: bind", "category = $category, favorite = $favorite")
                if (favorite) {
                    repository.getFavoriteRecipesByCategory(category)
                } else {
                    repository.getRecipesByCategory(category)
                }
            }
            .catch {
                Log.e("RecipeViewModel", "getRecipesByCategory: $it")
                mutableRecipes.postValue(emptyList())
            }
            .onEach {
                debug("RecipeViewModel: bind", "onEach = $it")
                mutableRecipes.postValue(it)
            }
            .launchIn(viewModelScope)
    }

    fun getRecipeById(recipeId: Long) {
        viewModelScope.launch {
            try {
                var recipeTag = repository.getRecipeById(recipeId)
                if (recipeTag.recipe.localUrl.isNullOrEmpty()) {
                    recipeTag.recipe.remoteUrl?.let { url ->
                        if (url.isNotEmpty()) {
                            recipeTag.recipe.localUrl =
                                repository.downloadImage(
                                    url,
                                    repository.getFileNames(url)["local"]!!
                                )
                        }
                    }
                }
                mutableRecipe.postValue(recipeTag)
                mutableLocalUrl.postValue(recipeTag.recipe.localUrl)
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "getRecipeById: ${t.message}")
                mutableRecipe.postValue(null)
            }
        }
    }

    fun insertRecipe(recipeWithTags: RecipeWithTags) {
        viewModelScope.launch {
            try {
                val recipeId = repository.insertRecipe(recipeWithTags.recipe)
                repository.updateTagsByRecipe(recipeId, recipeWithTags.tags)
                repository.deleteUnlinkedTags(recipeId)
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "insertRecipe: ${t.message}")
            }
        }
    }

    fun updateRecipe(recipeWithTags: RecipeWithTags) {
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipeWithTags.recipe)
                repository.insertTags(recipeWithTags.tags)
                repository.updateTagsByRecipe(recipeWithTags.recipe.id, recipeWithTags.tags)
                currentCategory?.let {cat ->
                    if (currentFavorite?: false) {
                        repository.getFavoriteRecipesByCategory(cat)
                    } else {
                        repository.getRecipesByCategory(cat)
                    }
                }
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "updateRecipe: ${t.message}")
            }
        }
    }

    fun downloadImage(remoteUrl: String) {
        viewModelScope.launch {
            try {
                mutableLocalUrl.postValue(
                    repository.downloadImage(
                        remoteUrl,
                        repository.getFileNames(remoteUrl)["local"]!!
                    )
                )
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "downloadImage: ${t.message}")
                mutableLocalUrl.postValue(null)
            }
        }
    }

    fun addRecipeTag(recipeTag: RecipeTag) {
        viewModelScope.launch {
            try {
                val currentRecipe = recipe.value?.recipe
                val currentList = repository.addTagToList(recipe.value?.tags.orEmpty(), recipeTag)
                currentRecipe ?: currentList.map { it.recipeId = null }
                repository.insertTags(listOf(recipeTag))

                currentRecipe?.let {
                    mutableRecipe.postValue(
                        RecipeWithTags(
                            it,
                            currentList
                        )
                    )
                }
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "addRecipeTag: ${t.message}")
            }

        }
    }

    fun changeFavorite(recipeId: Long) {
        viewModelScope.launch {
            try {
                var recipe = repository.getRecipeById(recipeId).recipe
                recipe.isFavorite = (recipe.isFavorite ?: false).not()
                debug("RecipeViewModel", "new favorite = ${recipe.isFavorite} ($recipeId)")
                repository.updateRecipe(recipe)
            } catch (t: Throwable) {
                Log.e("RecipeViewModel", "addRecipeTag: ${t.message}")
            }
        }
    }
}
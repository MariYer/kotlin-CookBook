package com.mariyer.cookbook.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mariyer.cookbook.data.db.dao.RecipeDao
import com.mariyer.cookbook.data.db.dao.RecipeTagDao
import com.mariyer.cookbook.data.db.model.Recipe
import com.mariyer.cookbook.data.db.model.RecipeTag

@Database(
    entities = [Recipe::class, RecipeTag::class],
    version = CookDatabase.DB_VERSION
)
@TypeConverters(BooleanConverter::class)
abstract class CookDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun recipeTagDao(): RecipeTagDao

    companion object {
        const val DB_VERSION = 2
        const val DB_NAME = "cook-database"
    }
}
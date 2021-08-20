package com.mariyer.cookbook.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mariyer.cookbook.utils.debug

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        debug("migrations", "migration_1_2")
        database.execSQL("ALTER TABLE recipes ADD COLUMN is_favorite INTEGER")
    }
}
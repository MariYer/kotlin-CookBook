package com.mariyer.cookbook.data.db

import android.content.Context
import androidx.room.Room

object Database {
    lateinit var instance: CookDatabase
        private set

    fun init(context: Context) {
        instance = Room.databaseBuilder(
            context,
            CookDatabase::class.java,
            CookDatabase.DB_NAME
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    }

}
package com.mariyer.cookbook

import android.app.Application
import com.mariyer.cookbook.data.db.Database

class CookApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Database.init(this)
    }
}
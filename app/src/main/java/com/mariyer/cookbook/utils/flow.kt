package com.mariyer.cookbook.utils

import com.google.android.material.navigation.NavigationBarView
import com.mariyer.cookbook.R
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun NavigationBarView.setItemSelectedFlow(): Flow<String> {
    return callbackFlow<String> {
        val itemSelectedListener = NavigationBarView.OnItemSelectedListener { item ->
            debug("setItemSelectedFlow", "${item.itemId}")
            when (item.itemId) {
                R.id.salads -> {
                    debug("setItemSelectedFlow", "salads")
                    sendBlocking("salads")
                    true
                }
                R.id.soups -> {
                    debug("setItemSelectedFlow", "soups")
                    sendBlocking("soups")
                    true
                }
                R.id.secondDishes -> {
                    debug("setItemSelectedFlow", "second_dishes")
                    sendBlocking("second_dishes")
                    true
                }
                R.id.desserts -> {
                    debug("setItemSelectedFlow", "desserts")
                    sendBlocking("desserts")
                    true
                }
                R.id.bake -> {
                    debug("setItemSelectedFlow", "bake")
                    sendBlocking("bake")
                    true
                }
                R.id.drinks -> {
                    debug("setItemSelectedFlow", "drinks")
                    sendBlocking("drinks")
                    true
                }
                else -> false
            }
        }

        setOnItemSelectedListener(itemSelectedListener)

        awaitClose {
            setOnItemSelectedListener(null)
        }
    }
}

package com.mariyer.cookbook.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.mariyer.cookbook.data.db.contracts.RecipeContract
import com.mariyer.cookbook.data.db.contracts.RecipeTagContract

@Entity(
    tableName = RecipeTagContract.TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = [RecipeContract.Columns.ID],
        childColumns = [RecipeTagContract.Columns.RECIPE_ID],
        onDelete = ForeignKey.SET_NULL
    )]
)
class RecipeTag(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RecipeTagContract.Columns.ID)
    val id: Long,
    @ColumnInfo(name = RecipeTagContract.Columns.RECIPE_ID)
    var recipeId: Long?,
    @ColumnInfo(name = RecipeTagContract.Columns.TAG)
    val tag: String
)

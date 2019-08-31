package com.restaurantlist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "restaurants")
data class Restaurant @JvmOverloads constructor(
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "status") var status: String = "",
    @ColumnInfo(name = "favourite") var favourite: Boolean = false,
    @ColumnInfo(name = "cost") var cost: Float,
    @ColumnInfo(name = "ratingAverage") var ratingAverage: Float,
    @ColumnInfo(name = "distance") var distance: Int,
    @PrimaryKey @ColumnInfo(name = "entryid") var id: String = UUID.randomUUID().toString()
)
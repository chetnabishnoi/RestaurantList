package com.restaurantlist.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.restaurantlist.data.Restaurant

@Database(entities = [Restaurant::class], version = 1, exportSchema = false)
abstract class RestaurantsDatabase : RoomDatabase() {
    abstract fun restaurantsDao(): RestaurantDao
}

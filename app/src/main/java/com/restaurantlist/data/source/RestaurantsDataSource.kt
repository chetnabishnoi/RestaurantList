package com.restaurantlist.data.source

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result

/**
 * Main entry point for accessing restaurant data.
 */
interface RestaurantsDataSource {

    suspend fun getRestaurants(): Result<List<Restaurant>>

    suspend fun getRestaurant(restaurantId: String): Result<Restaurant>

    suspend fun saveRestaurant(restaurant: Restaurant)

    suspend fun favouriteRestaurant(restaurant: Restaurant)

    suspend fun removefavouriteRestaurant(restaurant: Restaurant)

    suspend fun deleteAllRestaurants()
}

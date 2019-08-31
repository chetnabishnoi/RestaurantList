package com.restaurantlist.data.source

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result

interface RestaurantRepository {
    suspend fun getRestaurants(forceUpdate: Boolean = false): Result<List<Restaurant>>

    suspend fun favouriteRestaurant(restaurant: Restaurant)

    suspend fun removeFavouriteRestaurant(restaurant: Restaurant)
}
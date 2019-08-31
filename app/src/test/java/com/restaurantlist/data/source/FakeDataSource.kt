package com.restaurantlist.data.source

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.Result.Error

class FakeDataSource(var restaurants: MutableList<Restaurant>? = mutableListOf()) :
    RestaurantsDataSource {

    override suspend fun getRestaurants(): Result<List<Restaurant>> {
        restaurants?.let { return Result.Success(it) }
        return Error(
            Exception("Restaurants not found")
        )
    }

    override suspend fun getRestaurant(restaurantId: String): Result<Restaurant> {
        restaurants?.firstOrNull { it.id == restaurantId }?.let {
            return Result.Success(
                it
            )
        }
        return Error(Exception("Restaurant not found"))
    }

    override suspend fun saveRestaurant(restaurant: Restaurant) {
        restaurants?.add(restaurant)
    }

    override suspend fun favouriteRestaurant(restaurant: Restaurant) {
        restaurants?.firstOrNull { it.id == restaurant.id }?.let { it.favourite = true }
    }

    override suspend fun removefavouriteRestaurant(restaurant: Restaurant) {
        restaurants?.firstOrNull { it.id == restaurant.id }?.let { it.favourite = false }
    }

    override suspend fun deleteAllRestaurants() {
        restaurants?.clear()
    }
}

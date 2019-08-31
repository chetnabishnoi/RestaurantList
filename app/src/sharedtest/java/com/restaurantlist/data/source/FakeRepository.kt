package com.restaurantlist.data.source

import androidx.annotation.VisibleForTesting
import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.Result.Error
import com.restaurantlist.data.Result.Success
import java.util.*

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository : RestaurantRepository {

    var restaurantServiceData: LinkedHashMap<String, Restaurant> = LinkedHashMap()

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getRestaurants(forceUpdate: Boolean): Result<List<Restaurant>> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        return Success(restaurantServiceData.values.toList())
    }

    override suspend fun favouriteRestaurant(restaurant: Restaurant) {
        val favroriteRestaurant = Restaurant(
            restaurant.name,
            restaurant.status,
            true,
            restaurant.cost,
            restaurant.ratingAverage,
            restaurant.distance
        )
        restaurantServiceData[restaurant.id] = favroriteRestaurant
    }

    override suspend fun removeFavouriteRestaurant(restaurant: Restaurant) {
        val removedRestaurant = Restaurant(
            restaurant.name,
            restaurant.status,
            false,
            restaurant.cost,
            restaurant.ratingAverage,
            restaurant.distance
        )
        restaurantServiceData[restaurant.id] = removedRestaurant
    }


    @VisibleForTesting
    fun addRestaurants(vararg restaurants: Restaurant) {
        for (restaurant in restaurants) {
            restaurantServiceData[restaurant.id] = restaurant
        }
    }
}

package com.restaurantlist.data.source.remote

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.Result.Success
import com.restaurantlist.data.source.RestaurantsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as from remote.
 */

class RestaurantsRemoteDataSource internal constructor(
    private val restaurantService: RestaurantService
) : RestaurantsDataSource {

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getRestaurants(): Result<List<Restaurant>> =
        withContext(ioDispatcher) {
            return@withContext try {
                val restaurants = restaurantService.getRestaurants()
                val list = restaurants.map { item ->
                    Restaurant(
                        item.name,
                        item.status,
                        false,
                        item.sortingValues.minCost,
                        item.sortingValues.ratingAverage,
                        item.sortingValues.distance
                    )
                }.toList()
                Success(list)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getRestaurant(restaurantId: String): Result<Restaurant> {
        // Not needed here
        return Result.Error(Exception("Get Restaurant not available on remote"))
    }

    override suspend fun favouriteRestaurant(restaurant: Restaurant) {
        //Not needed here as favourite status is maintained locally
    }

    override suspend fun removefavouriteRestaurant(restaurant: Restaurant) {
        //Not needed here as favourite status is maintained locally
    }

    override suspend fun saveRestaurant(restaurant: Restaurant) {
        // Not needed here
    }

    override suspend fun deleteAllRestaurants() {
        // Not needed here
    }

}
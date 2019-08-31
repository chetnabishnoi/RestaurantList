package com.restaurantlist.data.source.local

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.Result.Error
import com.restaurantlist.data.Result.Success
import com.restaurantlist.data.source.RestaurantsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation of a data source as a db.
 */
class RestaurantsLocalDataSource internal constructor(
    private val restaurantDao: RestaurantDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RestaurantsDataSource {

    override suspend fun getRestaurants(): Result<List<Restaurant>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(restaurantDao.getRestaurants())
        } catch (e: java.lang.Exception) {
            Error(e)
        }
    }

    override suspend fun getRestaurant(restaurantId: String): Result<Restaurant> =
        withContext(ioDispatcher) {
            try {
                val restaurant = restaurantDao.getRestaurantById(restaurantId)
                if (restaurant != null) {
                    return@withContext Success(restaurant)
                } else {
                    return@withContext Error(Exception("Restaurant not found!"))
                }
            } catch (e: Exception) {
                return@withContext Error(e)
            }
        }

    override suspend fun saveRestaurant(restaurant: Restaurant) {
        restaurantDao.insertRestaurant(restaurant)
    }

    override suspend fun favouriteRestaurant(restaurant: Restaurant) {
        restaurantDao.updateFavourite(restaurant.id, true)
    }

    override suspend fun removefavouriteRestaurant(restaurant: Restaurant) {
        restaurantDao.updateFavourite(restaurant.id, false)
    }

    override suspend fun deleteAllRestaurants() = withContext(ioDispatcher) {
        restaurantDao.deleteRestaurants()
    }

}

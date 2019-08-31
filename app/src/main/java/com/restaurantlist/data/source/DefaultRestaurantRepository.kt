package com.restaurantlist.data.source

import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.Result.Error
import com.restaurantlist.data.Result.Success
import com.restaurantlist.di.module.ApplicationModule.RestaurantLocalDataSource
import com.restaurantlist.di.module.ApplicationModule.RestaurantRemoteDataSource
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class DefaultRestaurantRepository @Inject constructor(
    @RestaurantRemoteDataSource private val restaurantRemoteDataSource: RestaurantsDataSource,
    @RestaurantLocalDataSource private val restaurantLocalDataSource: RestaurantsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RestaurantRepository {

    override suspend fun getRestaurants(forceUpdate: Boolean): Result<List<Restaurant>> {
        return withContext(ioDispatcher) {
            val restaurants = fetchRestaurantsFromRemoteOrLocal(forceUpdate)
            (restaurants as? Success)?.let {
                return@withContext Success(it.data)
            }
            return@withContext Error(Exception("Illegal state"))
        }
    }

    private suspend fun fetchRestaurantsFromRemoteOrLocal(forceUpdate: Boolean): Result<List<Restaurant>> {
        if (!forceUpdate) {
            // Remote if local fails
            val localRestaurants = restaurantLocalDataSource.getRestaurants()
            if (localRestaurants is Success && localRestaurants.data.isNotEmpty()) return localRestaurants
        }

        when (val remoteRestaurants = restaurantRemoteDataSource.getRestaurants()) {
            is Error -> Timber.w("Remote data source fetch failed")
            is Success -> {
                refreshLocalDataSource(remoteRestaurants.data)
                return remoteRestaurants
            }
            else -> throw IllegalStateException()
        }
        return Error(Exception("Error fetching from remote and local"))
    }

    override suspend fun favouriteRestaurant(restaurant: Restaurant) {
        restaurant.favourite = true
        coroutineScope {
            launch { restaurantLocalDataSource.favouriteRestaurant(restaurant) }
        }
    }

    override suspend fun removeFavouriteRestaurant(restaurant: Restaurant) {
        restaurant.favourite = false
        coroutineScope {
            launch { restaurantLocalDataSource.removefavouriteRestaurant(restaurant) }
        }
    }

    private suspend fun refreshLocalDataSource(restaurants: List<Restaurant>) {
        restaurantLocalDataSource.deleteAllRestaurants()
        for (restaurant in restaurants) {
            restaurantLocalDataSource.saveRestaurant(restaurant)
        }
    }


}
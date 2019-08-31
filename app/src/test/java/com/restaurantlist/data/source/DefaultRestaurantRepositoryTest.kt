package com.example.android.architecture.blueprints.todoapp.data.source

import com.google.common.truth.Truth.assertThat
import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.source.DefaultRestaurantRepository
import com.restaurantlist.data.source.FakeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
class DefaultRestaurantRepositoryTest {

    private val restaurant1 = Restaurant("Name1", "open", true, 25f, 3f, 30)
    private val restaurant2 = Restaurant("Name2", "closed", false, 30f, 3.5f, 12)
    private val remoteRestaurants = listOf(restaurant1, restaurant2).sortedBy { it.id }
    private val localRestaurants = listOf(restaurant1, restaurant2).sortedBy { it.id }
    private lateinit var restaurantRemoteSource: FakeDataSource
    private lateinit var restaurantLocalSource: FakeDataSource

    // Class under test
    private lateinit var restaurantRepository: DefaultRestaurantRepository

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        restaurantRemoteSource = FakeDataSource(remoteRestaurants.toMutableList())
        restaurantLocalSource = FakeDataSource(localRestaurants.toMutableList())
        // Get a reference to the class under test
        restaurantRepository = DefaultRestaurantRepository(
            restaurantRemoteSource, restaurantLocalSource, Dispatchers.Unconfined
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getRestaurants_emptyRepositoryAndUninitializedCache() = runBlockingTest {
        val emptySource = FakeDataSource()
        val restaurantRepository = DefaultRestaurantRepository(
            emptySource, emptySource, Dispatchers.Unconfined
        )

        assertThat(restaurantRepository.getRestaurants() is Result.Success).isTrue()
    }

    @Test
    fun getRestaurants_repositoryCachesAfterFirstApiCall() = runBlockingTest {
        // Trigger the repository to load data, which loads from remote
        val initial = restaurantRepository.getRestaurants()

        restaurantRemoteSource.restaurants = remoteRestaurants.toMutableList()

        val second = restaurantRepository.getRestaurants()

        // Initial and second should match because we didn't force a refresh
        assertThat(second).isEqualTo(initial)
    }

    @Test
    fun getRestaurant_requestsAllRestaurantsFromRemoteDataSource() = runBlockingTest {
        // When restaurants are requested from the restaurants repository
        val restaurants = restaurantRepository.getRestaurants() as Result.Success

        // Then restaurants are loaded from the remote data source
        assertThat(restaurants.data).isEqualTo(remoteRestaurants)
    }


    @Test
    fun getRestaurants_withNoLocal_restaurantsAreRetrievedFromRemote() = runBlockingTest {
        // Make local data source unavailable
        restaurantLocalSource.restaurants = null

        // First call returns from REMOTE
        val restaurants = restaurantRepository.getRestaurants()

        // Result should be an error
        assertThat(restaurants).isInstanceOf(Result.Success::class.java)
    }


    @Test
    fun getRestaurants_WithBothDataSourcesUnavailable_returnsError() = runBlockingTest {
        // When both sources are unavailable
        restaurantRemoteSource.restaurants = null
        restaurantLocalSource.restaurants = null

        // The repository returns an error
        assertThat(restaurantRepository.getRestaurants()).isInstanceOf(Result.Error::class.java)
    }

}


package com.restaurantlist.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.restaurantlist.MainCoroutineRule
import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result
import com.restaurantlist.data.source.RestaurantsDataSource
import com.restaurantlist.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration test for the [RestaurantsDataSource].
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RestaurantsLocalDataSourceTest {

    private lateinit var localDataSource: RestaurantsLocalDataSource
    private lateinit var database: RestaurantsDatabase


    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RestaurantsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = RestaurantsLocalDataSource(database.restaurantsDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveRestaurant_retrievesRestaurant() = runBlockingTest {
        // GIVEN - a new restaurant saved in the database
        val newRestaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        localDataSource.saveRestaurant(newRestaurant)

        // WHEN  - Restaurant retrieved by ID
        val result = localDataSource.getRestaurant(newRestaurant.id)

        // THEN - Same restaurant is returned
        assertThat(result.succeeded, `is`(true))
        result as Result.Success
        assertThat(result.data.name, `is`("Name"))
        assertThat(result.data.status, `is`("open"))
        assertThat(result.data.favourite, `is`(false))
        assertThat(result.data.cost, `is`(23f))
        assertThat(result.data.ratingAverage, `is`(4.5f))
        assertThat(result.data.distance, `is`(20))
    }

    @Test
    fun favouriteRestaurant_retrievedRestaurantIsFavourite() = runBlockingTest {
        // Given a new restaurant in the persistent repository
        val newRestaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        localDataSource.saveRestaurant(newRestaurant)

        // When favourited in the persistent repository
        localDataSource.favouriteRestaurant(newRestaurant)
        val result = localDataSource.getRestaurant(newRestaurant.id)

        // Then the restaurant can be retrieved from the persistent repository and is favorite
        assertThat(result.succeeded, `is`(true))
        result as Result.Success

        assertThat(result.data.name, `is`(newRestaurant.name))
        assertThat(result.data.favourite, `is`(true))
    }

    @Test
    fun removeFavourite_retrievedRestaurantIsNotFavvourite() = runBlockingTest {
        // Given a new restaurant in the persistent repository
        val newRestaurant = Restaurant("Name", "open", true, 23f, 4.5f, 20)
        localDataSource.saveRestaurant(newRestaurant)

        localDataSource.removefavouriteRestaurant(newRestaurant)

        // Then the restaurant can be retrieved from the persistent repository and is not favorite
        val result = localDataSource.getRestaurant(newRestaurant.id)

        assertThat(result.succeeded, `is`(true))
        result as Result.Success

        assertThat(result.data.name, `is`("Name"))
        assertThat(result.data.favourite, `is`(false))
    }


    @Test
    fun deleteAllRestaurants_emptyListOfRetrievedRestaurants() = runBlockingTest {
        // Given a new restaurant in the persistent repository and a mocked callback
        val newRestaurant = Restaurant("Name", "open", true, 23f, 4.5f, 20)

        localDataSource.saveRestaurant(newRestaurant)

        // When all restaurants are deleted
        localDataSource.deleteAllRestaurants()

        // Then the retrieved restaurants is an empty list
        val result = localDataSource.getRestaurants() as Result.Success
        assertThat(result.data.isEmpty(), `is`(true))

    }

    @Test
    fun getRestaurants_retrieveSavedRestaurants() = runBlockingTest {
        // Given 2 new restaurant in the persistent repository
        val newRestaurant1 = Restaurant("Name", "open", true, 23f, 4.5f, 20)
        val newRestaurant2 = Restaurant("Name1", "closed", false, 23f, 4.5f, 20)

        localDataSource.saveRestaurant(newRestaurant1)
        localDataSource.saveRestaurant(newRestaurant2)
        // Then the restaurants can be retrieved from the persistent repository
        val results = localDataSource.getRestaurants() as Result.Success<List<Restaurant>>
        val restaurants = results.data
        assertThat(restaurants.size, `is`(2))
    }
}

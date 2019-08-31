package com.restaurantlist.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.restaurantlist.MainCoroutineRule
import com.restaurantlist.data.Restaurant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class RestaurantDaoTest {
    private lateinit var database: RestaurantsDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RestaurantsDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertRestaurantAndGetById() = runBlockingTest {
        // GIVEN - insert a restaurant
        val restaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        database.restaurantsDao().insertRestaurant(restaurant)

        // WHEN - Get the restaurant by id from the database
        val loaded = database.restaurantsDao().getRestaurantById(restaurant.id)

        // THEN - The loaded data contains the expected values
        assertThat(loaded as Restaurant, CoreMatchers.notNullValue())

        assertThat(loaded.id, `is`(restaurant.id))
        assertThat(loaded.name, `is`(restaurant.name))
        assertThat(loaded.status, `is`(restaurant.status))
        assertThat(loaded.favourite, `is`(restaurant.favourite))
        assertThat(loaded.cost, `is`(restaurant.cost))
        assertThat(loaded.ratingAverage, `is`(restaurant.ratingAverage))
        assertThat(loaded.distance, `is`(restaurant.distance))
    }

    @Test
    fun insertRestaurantReplacesOnConflict() = runBlockingTest {
        // Given that a restaurant is inserted
        val restaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        database.restaurantsDao().insertRestaurant(restaurant)

        // When a restaurant with the same id is inserted
        val newRestaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        database.restaurantsDao().insertRestaurant(newRestaurant)

        // THEN - The loaded data contains the expected values
        val loaded = database.restaurantsDao().getRestaurantById(restaurant.id)
        assertThat(loaded?.id, `is`(restaurant.id))
        assertThat(loaded?.name, `is`(restaurant.name))
        assertThat(loaded?.status, `is`(restaurant.status))
        assertThat(loaded?.favourite, `is`(restaurant.favourite))
        assertThat(loaded?.cost, `is`(restaurant.cost))
        assertThat(loaded?.ratingAverage, `is`(restaurant.ratingAverage))
        assertThat(loaded?.distance, `is`(restaurant.distance))
    }


    @Test
    fun insertRestaurantAndGetRestaurants() = runBlockingTest {
        // GIVEN - insert a restaurant
        val restaurant = Restaurant(
            "Name", "open",
            false, 23f, 4.5f, 20
        )
        database.restaurantsDao().insertRestaurant(restaurant)

        // WHEN - Get restaurant from the database
        val restaurants = database.restaurantsDao().getRestaurants()

        // THEN - There is only 1 restaurant in the database, and contains the expected values
        assertThat(restaurants.size, `is`(1))
        assertThat(restaurants[0].id, `is`(restaurant.id))
        assertThat(restaurants[0].name, `is`(restaurant.name))
        assertThat(restaurants[0].status, `is`(restaurant.status))
        assertThat(restaurants[0].favourite, `is`(restaurant.favourite))
        assertThat(restaurants[0].cost, `is`(restaurant.cost))
        assertThat(restaurants[0].ratingAverage, `is`(restaurant.ratingAverage))
        assertThat(restaurants[0].distance, `is`(restaurant.distance))
    }


    @Test
    fun updateFavouriteAndGetById() = runBlockingTest {
        // When inserting a restaurant
        val restaurant = Restaurant(
            "Name", "closed",
            false, 23f, 4.5f, 20
        )
        database.restaurantsDao().insertRestaurant(restaurant)

        // When the restaurant is updated
        database.restaurantsDao().updateFavourite(restaurant.id, true)

        // THEN - The loaded data contains the expected values
        val loaded = database.restaurantsDao().getRestaurantById(restaurant.id)
        assertThat(loaded?.id, `is`(restaurant.id))
        assertThat(loaded?.name, `is`(restaurant.name))
        assertThat(loaded?.status, `is`(restaurant.status))
        assertThat(loaded?.favourite, `is`(true))
        assertThat(loaded?.cost, `is`(restaurant.cost))
        assertThat(loaded?.ratingAverage, `is`(restaurant.ratingAverage))
        assertThat(loaded?.distance, `is`(restaurant.distance))
    }


    @Test
    fun deleteRestaurantssAndGettingRestaurantss() = runBlockingTest {
        // Given a restaurant inserted
        database.restaurantsDao().insertRestaurant(
            Restaurant(
                "Name", "closed",
                false, 23f, 4.5f, 20
            )
        )

        // When deleting all restaurants
        database.restaurantsDao().deleteRestaurants()

        // THEN - The list is empty
        val restaurants = database.restaurantsDao().getRestaurants()
        assertThat(restaurants.isEmpty(), `is`(true))
    }


}

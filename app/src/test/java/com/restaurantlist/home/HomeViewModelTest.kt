package com.restaurantlist.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.restaurantlist.LiveDataTestUtil
import com.restaurantlist.MainCoroutineRule
import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [HomeViewModel]
 */
@ExperimentalCoroutinesApi
class HomeViewModelTest {

    // Subject under test
    private lateinit var homeViewModel: HomeViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var restaurantRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the restaurant to 3, with favourite active and two not favourite
        restaurantRepository = FakeRepository()
        val restaurant1 = Restaurant("Name", "open", false, 23f, 5f, 30)
        val restaurant2 = Restaurant("Name2", "open", false, 13f, 3f, 10)
        val restaurant3 = Restaurant("Name3", "order ahead", true, 43f, 4.5f, 250)
        restaurantRepository.addRestaurants(restaurant1, restaurant2, restaurant3)

        homeViewModel = HomeViewModel(restaurantRepository)
    }

    @Test
    fun loadAllRestaurantsFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Given an initialized HomeViewModell with initialized restaurants
        // When loading of Restaurants is requested
        homeViewModel.setSorting(RestaurantSortingType.DEFAULT)

        // Trigger loading of restaurants
        homeViewModel.loadRestaurants(true)

        // Then progress indicator is shown
        assertThat(LiveDataTestUtil.getValue(homeViewModel.dataLoading)).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(homeViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        assertThat(LiveDataTestUtil.getValue(homeViewModel.items)).hasSize(3)
    }

    @Test
    fun loadRestaurantsSortedByDistanceFromRepositoryAndLoadIntoView() {
        // Given an initialized HomeViewModel with initialized restaurants
        // When loading of Restaurant is requested
        homeViewModel.setSorting(RestaurantSortingType.DISTANCE)

        // Load Restaurants
        homeViewModel.loadRestaurants(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(homeViewModel.dataLoading)).isFalse()

        // And data correctly loaded
        val restaurants = LiveDataTestUtil.getValue(homeViewModel.items)
        assertThat(restaurants).hasSize(3)
        assertThat(restaurants.get(0).distance).isEqualTo(250)
        assertThat(restaurants.get(1).distance).isEqualTo(10)
    }


    @Test
    fun loadRestaurants_error() {
        // Make the repository return errors
        restaurantRepository.setReturnError(true)

        // Load Restaurants
        homeViewModel.loadRestaurants(true)

        // Then progress indicator is hidden
        assertThat(LiveDataTestUtil.getValue(homeViewModel.dataLoading)).isFalse()

        // And the list of items is empty
        assertThat(LiveDataTestUtil.getValue(homeViewModel.items)).isEmpty()
    }


    @Test
    fun favouriteRestaurant_dataUpdated() {
        // With a repository that has a not favourite restaurant
        val restaurant = Restaurant("Name", "open", false, 30f, 4f, 50)
        restaurantRepository.addRestaurants(restaurant)

        // Favourite restaurant
        homeViewModel.favouriteRestaurant(restaurant, true)

        // Verify the restaurant is favourite
        assertThat(restaurantRepository.restaurantServiceData[restaurant.id]?.favourite).isTrue()

    }

    @Test
    fun unfavouriteRestaurant_dataUpdated() {
        // With a repository that has a not favourite restaurant
        val restaurant = Restaurant("Name", "open", false, 30f, 4f, 50)
        restaurantRepository.addRestaurants(restaurant)

        // Favourite restaurant
        homeViewModel.favouriteRestaurant(restaurant, false)

        // Verify the restaurant is not favourite
        assertThat(restaurantRepository.restaurantServiceData[restaurant.id]?.favourite).isFalse()
    }
}

package com.restaurantlist.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurantlist.data.Restaurant
import com.restaurantlist.data.Result.Success
import com.restaurantlist.data.source.RestaurantRepository
import com.restaurantlist.util.defaultSorting
import com.restaurantlist.util.sortByAverageRating
import com.restaurantlist.util.sortByCost
import com.restaurantlist.util.sortByDistance
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Restaurant>>().apply { value = emptyList() }
    val items: LiveData<List<Restaurant>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private var _currentSorting = RestaurantSortingType.DEFAULT

    private val _isDataLoadingError = MutableLiveData<Boolean>()
    val isDataLoadingError: LiveData<Boolean> = _isDataLoadingError


    /**
     * Sets the current restaurant sorting type.
     *
     * @param requestType Can be [RestaurantSortingType.DEFAULT],
     * [RestaurantSortingType.COST], or
     * [RestaurantSortingType.AVERAGE_RATING] or
     * [RestaurantSortingType.DISTANCE]
     */
    fun setSorting(requestType: RestaurantSortingType) {
        _currentSorting = requestType
    }

    /**
     * @param forceUpdate  Pass in true to refresh the data in the [RestaurantsDataSource]
     */

    fun loadRestaurants(forceUpdate: Boolean) {
        _dataLoading.value = true
        viewModelScope.launch {
            val restaurantResult = restaurantRepository.getRestaurants(forceUpdate)

            if (restaurantResult is Success) {
                val restaurants = restaurantResult.data
                val sortedRestaurant: List<Restaurant>

                // We sort the restaurants based on the requestType
                when (_currentSorting) {

                    RestaurantSortingType.DEFAULT ->
                        sortedRestaurant = defaultSorting(restaurants)

                    RestaurantSortingType.COST ->
                        sortedRestaurant = sortByCost(restaurants)

                    RestaurantSortingType.AVERAGE_RATING ->
                        sortedRestaurant = sortByAverageRating(restaurants)

                    RestaurantSortingType.DISTANCE ->
                        sortedRestaurant = sortByDistance(restaurants)
                }
                _isDataLoadingError.value = false
                _items.value = sortedRestaurant
            } else {
                _isDataLoadingError.value = true
                _items.value = emptyList()
            }
            _dataLoading.value = false
        }
    }

    fun favouriteRestaurant(restaurant: Restaurant, favourite: Boolean) =
        viewModelScope.launch {
            if (favourite) {
                restaurantRepository.favouriteRestaurant(restaurant)
            } else {
                restaurantRepository.removeFavouriteRestaurant(restaurant)
            }
            // Refresh list to show the new state
            loadRestaurants(false)
        }
}
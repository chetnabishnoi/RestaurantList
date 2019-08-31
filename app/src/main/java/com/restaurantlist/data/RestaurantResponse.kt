package com.restaurantlist.data

data class RestaurantResponse(
    val name: String,
    val status: String,
    val sortingValues: SortingValues
)

data class SortingValues(val minCost: Float, val ratingAverage: Float, val distance: Int)
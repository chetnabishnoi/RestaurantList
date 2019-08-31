package com.restaurantlist.home

enum class RestaurantSortingType {

    /**
     * Default sorting by favorite and open status
     */
    DEFAULT,

    /**
     * Sorts on the basis of cost
     */
    COST,

    /**
     * Sorts on the basis of average rating
     */
    AVERAGE_RATING,

    /**
     * Sorts on the basis of distance
     */
    DISTANCE
}

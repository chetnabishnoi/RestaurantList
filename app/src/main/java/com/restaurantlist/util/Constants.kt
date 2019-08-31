package com.restaurantlist.util

import com.restaurantlist.data.Restaurant

const val BASE_URL = "http://demo7500685.mockable.io"

fun defaultSorting(restaurants: List<Restaurant>): List<Restaurant> {
    return restaurants
        .sortedWith(
            compareBy(
                { !it.favourite },
                {
                    when (it.status) {
                        "open" -> -1
                        "order ahead" -> 0
                        "closed" -> 1
                        else -> 2
                    }
                })
        )
}

fun sortByCost(restaurants: List<Restaurant>): List<Restaurant> {
    return restaurants
        .sortedWith(compareBy(
            { !it.favourite },
            {
                when (it.status) {
                    "open" -> -1
                    "order ahead" -> 0
                    "closed" -> 1
                    else -> 2
                }
            }, { it.cost })
        )
}

fun sortByAverageRating(restaurants: List<Restaurant>): List<Restaurant> {
    return restaurants
        .sortedWith(compareBy<Restaurant> { !it.favourite }
            .thenBy {
                when (it.status) {
                    "open" -> -1
                    "order ahead" -> 0
                    "closed" -> 1
                    else -> 2
                }
            }
            .thenByDescending { it.ratingAverage })
}

fun sortByDistance(restaurants: List<Restaurant>): List<Restaurant> {
    return restaurants
        .sortedWith(compareBy({ !it.favourite },
            {
                when (it.status) {
                    "open" -> -1
                    "order ahead" -> 0
                    "closed" -> 1
                    else -> 2
                }
            }, { it.distance })
        )
}


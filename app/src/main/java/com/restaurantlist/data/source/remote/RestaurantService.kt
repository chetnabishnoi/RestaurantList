package com.restaurantlist.data.source.remote

import com.restaurantlist.data.RestaurantResponse
import retrofit2.http.GET

interface RestaurantService {

    @GET("/api/v1/get")
    suspend fun getRestaurants(): List<RestaurantResponse>
}
package com.restaurantlist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.restaurantlist.data.Restaurant

@Dao
interface RestaurantDao {

    /**
     * Select all restaurants from the restaurants table.
     *
     * @return all restaurants.
     */
    @Query("SELECT * FROM Restaurants")
    suspend fun getRestaurants(): List<Restaurant>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRestaurant(restaurant: Restaurant)

    /**
     * Select a restaurant by id.
     *
     * @param restaurantId the restaurantId.
     * @return the restaurant with restaurantId.
     */
    @Query("SELECT * FROM Restaurants WHERE entryid = :restaurantId")
    suspend fun getRestaurantById(restaurantId: String): Restaurant?

    /**
     * Update the favourite status of a restaurant
     *
     * @param restaurantId id of the restaurant
     * @param favourite status to be updated
     */
    @Query("UPDATE restaurants SET favourite = :favourite WHERE entryid = :restaurantId")
    suspend fun updateFavourite(restaurantId: String, favourite: Boolean)

    /**
     * Delete all restaurants.
     */
    @Query("DELETE FROM Restaurants")
    suspend fun deleteRestaurants()
}
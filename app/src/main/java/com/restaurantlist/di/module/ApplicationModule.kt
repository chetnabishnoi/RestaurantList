package com.restaurantlist.di.module

import android.content.Context
import androidx.room.Room
import com.restaurantlist.data.source.DefaultRestaurantRepository
import com.restaurantlist.data.source.RestaurantRepository
import com.restaurantlist.data.source.RestaurantsDataSource
import com.restaurantlist.data.source.local.RestaurantsDatabase
import com.restaurantlist.data.source.local.RestaurantsLocalDataSource
import com.restaurantlist.data.source.remote.RestaurantService
import com.restaurantlist.data.source.remote.RestaurantsRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RestaurantRemoteDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RestaurantLocalDataSource

    @JvmStatic
    @Singleton
    @RestaurantRemoteDataSource
    @Provides
    fun provideRestaurantRemoteDataSource(restaurantService: RestaurantService): RestaurantsDataSource {
        return RestaurantsRemoteDataSource(restaurantService)
    }

    @JvmStatic
    @Singleton
    @RestaurantLocalDataSource
    @Provides
    fun provideRestaurantLocalDataSource(
        database: RestaurantsDatabase,
        ioDispatcher: CoroutineDispatcher
    ): RestaurantsDataSource {
        return RestaurantsLocalDataSource(
            database.restaurantsDao(), ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideDataBase(context: Context): RestaurantsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RestaurantsDatabase::class.java,
            "Restaurants.db"
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

@Module
abstract class ApplicationModuleBinds {

    @Singleton
    @Binds
    abstract fun bindRepository(repo: DefaultRestaurantRepository): RestaurantRepository
}

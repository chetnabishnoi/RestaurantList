package com.restaurantlist.di.component

import android.content.Context
import com.restaurantlist.RestaurantListApplication
import com.restaurantlist.di.module.ApplicationModule
import com.restaurantlist.di.module.HomeModule
import com.restaurantlist.di.module.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        ApplicationModule::class,
        HomeModule::class,
        NetworkModule::class]
)
interface AppComponent : AndroidInjector<RestaurantListApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Context): AppComponent
    }
}
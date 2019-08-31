package com.restaurantlist.di.module

import androidx.lifecycle.ViewModel
import com.restaurantlist.di.ViewModelBuilder
import com.restaurantlist.di.ViewModelKey
import com.restaurantlist.home.HomeFragment
import com.restaurantlist.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger module for the restaurant list feature.
 */
@Module
abstract class HomeModule {

    @ContributesAndroidInjector(
        modules = [
            ViewModelBuilder::class
        ]
    )
    internal abstract fun homeFragment(): HomeFragment

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindViewModel(viewmodel: HomeViewModel): ViewModel
}

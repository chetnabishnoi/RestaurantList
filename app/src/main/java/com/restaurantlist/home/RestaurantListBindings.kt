package com.restaurantlist.home

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restaurantlist.data.Restaurant

@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Restaurant>) {
    (listView.adapter as RestaurantAdapter).submitList(items)
}

@BindingAdapter("android:text")
fun setFloat(view: TextView, value: Float) {
    if (value.isNaN()) view.text = ""
    else {
        view.text = value.toString()
    }
}

@BindingAdapter("android:text")
fun setInt(view: TextView, value: Int) {
    view.text = value.toString()
}
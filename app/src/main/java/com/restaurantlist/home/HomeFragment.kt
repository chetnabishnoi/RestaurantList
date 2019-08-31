package com.restaurantlist.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.restaurantlist.R
import com.restaurantlist.databinding.FragmentHomeBinding
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject


class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }

    private lateinit var viewDataBinding: FragmentHomeBinding
    private lateinit var listAdapter: RestaurantAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("Inside HomeFragment")
        viewDataBinding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupListAdapter()
        viewModel.loadRestaurants(false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_sort -> {
                showSortingPopUpMenu()
                true
            }
            else -> false
        }

    private fun showSortingPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.action_sort) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.sort_restaurants, menu)

            setOnMenuItemClickListener {
                viewModel.setSorting(
                    when (it.itemId) {
                        R.id.cost -> RestaurantSortingType.COST
                        R.id.average_rating -> RestaurantSortingType.AVERAGE_RATING
                        R.id.distance -> RestaurantSortingType.DISTANCE
                        else -> RestaurantSortingType.DEFAULT
                    }
                )
                viewModel.loadRestaurants(false)
                true
            }
            show()
        }
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = RestaurantAdapter(viewModel)
            viewDataBinding.restaurantList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }
}
package com.code.korti.seaofimages.presentation.collections_list

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.databinding.FragmentCollectionsListBinding
import com.code.korti.seaofimages.presentation.adapter.CollectionAdapter
import com.code.korti.seaofimages.presentation.adapter.CollectionAdapterListener
import com.code.korti.seaofimages.utils.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CollectionsListFragment : Fragment(R.layout.fragment_collections_list),
    CollectionAdapterListener {

    private val binding: FragmentCollectionsListBinding by viewBinding(
        FragmentCollectionsListBinding::bind
    )

    private val viewModel by viewModels<CollectionsListViewModel>()

    private var pageOfCollections = 1
    private var numberOfCollections = 20
    private var isLoadingPage = false
    private var isFirstLaunch = true

    private var collectionAdapter: CollectionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadPage(pageOfCollections, numberOfCollections)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        bindViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(pageOfCollections, numberOfCollections)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

        collectionAdapter = CollectionAdapter(this, requireContext())
        with(binding.collectionsList) {
            adapter = collectionAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            itemAnimator = null
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = linearLayoutManager.itemCount
                    isFirstLaunch = false

                    pageOfCollections = totalItemCount / numberOfCollections

                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()

                    if (!isLoadingPage && totalItemCount <=
                        lastVisibleItem + 2 && dy > 0
                    ) {
                        pageOfCollections += 1
                        viewModel.loadPage(pageOfCollections, numberOfCollections)
                    }
                }
            })
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, ::updateLoadingState)
        viewModel.collectionsList.observe(viewLifecycleOwner) {
            collectionAdapter?.items = it
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showSnackbar(it)
        }
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(binding.guideline, message, Snackbar.LENGTH_LONG)
            .apply { anchorView = binding.guideline }.show()
    }

    private fun updateLoadingState(isLoading: Boolean) {
        if (isFirstLaunch) {
            binding.progressBar.isVisible = isLoading
        } else {
            binding.progress.isVisible = isLoading
        }
        viewLifecycleOwner.lifecycleScope.launch {
            if (isLoading) {
                isLoadingPage = true
                delay(1000)
                isLoadingPage = false
            }
        }
    }

    override fun onCollectionClicked(cardView: View, collection: Collection) {
        val directions =
            CollectionsListFragmentDirections.actionCollectionsListFragmentToCollectionFragment(
                collection.id
            )
        findNavController().navigate(directions)
    }

    override fun onDestroy() {
        super.onDestroy()
        collectionAdapter = null
    }
}
package com.code.korti.seaofimages.presentation.home

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.databinding.FragmentHomeBinding
import com.code.korti.seaofimages.presentation.adapter.ImageAdapter
import com.code.korti.seaofimages.presentation.adapter.ImageAdapterListener
import com.code.korti.seaofimages.utils.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home), ImageAdapterListener {

    private val binding: FragmentHomeBinding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel by viewModels<HomeViewModel>()

    private var pageOfImages = 1
    private var numberOfImages = 20
    private var isLoadingPage = false
    private var isFirstLaunch = true

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadPage(pageOfImages, numberOfImages)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initList()
        bindViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadPage(pageOfImages, numberOfImages)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initList() {
        val gridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        imageAdapter = ImageAdapter(this, requireContext())
        with(binding.imageList) {
            adapter = imageAdapter
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            layoutManager = gridLayoutManager
            itemAnimator = null
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = gridLayoutManager.itemCount
                    isFirstLaunch = false

                    pageOfImages = totalItemCount / 20

                    val lastPositions = gridLayoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItem = Math.max(lastPositions[0], lastPositions[1])

                    if (!isLoadingPage && totalItemCount <=
                        lastVisibleItem + 2 && dy > 0
                    ) {
                        pageOfImages += 1
                        viewModel.loadPage(pageOfImages, numberOfImages)
                    }
                }
            })
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, ::updateLoadingState)
        viewModel.imageList.observe(viewLifecycleOwner) {
            imageAdapter?.items = it
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showSnackbar(it)
        }
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(binding.guideline, message, Snackbar.LENGTH_LONG)
            .apply { anchorView =  binding.guideline}.show()
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

    override fun onImageClicked(cardView: View, image: ImageFromList) {
        val noteCardDetailTransitionName = getString(R.string.image_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(cardView to noteCardDetailTransitionName)
        val directions =
            HomeFragmentDirections.actionHomeFragmentToDetailFragment(image.id)
        findNavController().navigate(directions, extras)
    }

    override fun onDestroy() {
        super.onDestroy()
        imageAdapter = null
    }
}

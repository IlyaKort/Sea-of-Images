package com.code.korti.seaofimages.presentation.search

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.databinding.FragmentSearchBinding
import com.code.korti.seaofimages.presentation.adapter.ImageAdapter
import com.code.korti.seaofimages.presentation.adapter.ImageAdapterListener
import com.code.korti.seaofimages.utils.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search), ImageAdapterListener {

    private val binding: FragmentSearchBinding by viewBinding(FragmentSearchBinding::bind)

    private val viewModel by viewModels<SearchViewModel>()

    private var pageOfImages = 1
    private var numberOfImages = 10
    private var isLoadingPage = false
    private var isFirstLaunch = true

    private val args: SearchFragmentArgs by navArgs()

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.search(args.query, pageOfImages, numberOfImages)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        initList()
        bindViewModel()
    }

    private fun initList() {
        val linearLayout = LinearLayoutManager(requireContext())

        imageAdapter = ImageAdapter(this, requireContext())
        with(binding.imageList) {
            adapter = imageAdapter
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            layoutManager = linearLayout
            itemAnimator = null
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = linearLayout.itemCount
                    val lastVisibleItem = linearLayout.findLastVisibleItemPosition()
                    isFirstLaunch = false

                    pageOfImages = totalItemCount / numberOfImages

                    if (!isLoadingPage && totalItemCount <= lastVisibleItem + 2 && dy > 0) {
                        pageOfImages += 1
                        viewModel.search(args.query, pageOfImages, numberOfImages)
                    }
                }
            })
        }
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(binding.guideline, message, Snackbar.LENGTH_LONG)
            .apply { anchorView = binding.guideline }.show()
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
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(image.id)
        findNavController().navigate(directions, extras)
    }

    override fun onDestroy() {
        super.onDestroy()
        imageAdapter = null
    }
}
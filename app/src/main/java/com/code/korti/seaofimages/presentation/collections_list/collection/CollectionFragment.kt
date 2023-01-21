package com.code.korti.seaofimages.presentation.collections_list.collection

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
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.Collection
import com.code.korti.seaofimages.data.models.CollectionPhotos
import com.code.korti.seaofimages.databinding.FragmentCollectionBinding
import com.code.korti.seaofimages.presentation.adapter.collection_photos.CollectionPhotosAdapter
import com.code.korti.seaofimages.presentation.adapter.collection_photos.CollectionPhotosAdapterListener
import com.code.korti.seaofimages.utils.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CollectionFragment : Fragment(R.layout.fragment_collection), CollectionPhotosAdapterListener {

    private val viewModel by viewModels<CollectionViewModel>()
    private val binding: FragmentCollectionBinding by viewBinding(FragmentCollectionBinding::bind)
    private val args: CollectionFragmentArgs by navArgs()
    private var photoAdapter: CollectionPhotosAdapter? = null

    private var pageOfPhotos = 1
    private var numberOfPhotos = 10
    private var totalPhotos = 1
    private var isLoadingPage = false
    private var isFirstLaunch = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadCollectionPhotos(args.collectionId, pageOfPhotos, numberOfPhotos)

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
            viewModel.loadCollectionPhotos(args.collectionId, pageOfPhotos, numberOfPhotos)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun bindViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, ::updateLoadingState)
        viewModel.collectionWithPhotos.observe(viewLifecycleOwner) {
            photoAdapter?.items = it.photo
            showInfoAboutCollection(it.collection)
            totalPhotos = it.collection.totalPhotos
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showSnackbar(it)
            requireActivity().onBackPressed()
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

    private fun initList() {
        val linearLayout = LinearLayoutManager(requireContext())

        photoAdapter = CollectionPhotosAdapter(this, requireContext())
        with(binding.imagesList) {
            adapter = photoAdapter
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

                    pageOfPhotos = totalItemCount / numberOfPhotos

                    if (!isLoadingPage && totalItemCount <=
                        lastVisibleItem + 2 && totalItemCount != totalPhotos && dy > 0
                    ) {
                        pageOfPhotos += 1
                        viewModel.loadCollectionPhotos(
                            args.collectionId,
                            pageOfPhotos,
                            numberOfPhotos
                        )
                    }
                }
            })
        }
    }

    private fun showInfoAboutCollection(collection: Collection) {
        binding.titleTextView.text = collection.title
        binding.descriptionTextView.text = collection.description
        binding.totalPhotosTextView.text = collection.totalPhotos.toString() + " photo"
        binding.nicknameTextView.text = "@" + collection.user.nickname

        Glide.with(requireContext())
            .load(collection.coverPhoto.urls.thumb)
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.collectionImageView)
    }

    override fun onDestroy() {
        super.onDestroy()
        photoAdapter = null
    }

    override fun onImageClicked(cardView: View, photo: CollectionPhotos) {
        val noteCardDetailTransitionName = getString(R.string.image_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(cardView to noteCardDetailTransitionName)
        val directions = CollectionFragmentDirections.actionCollectionFragmentToDetailFragment(
            photo.id
        )
        findNavController().navigate(directions, extras)
    }
}

package com.code.korti.seaofimages.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.ImageFromList
import com.code.korti.seaofimages.data.models.Profile
import com.code.korti.seaofimages.databinding.FragmentProfileBinding
import com.code.korti.seaofimages.presentation.adapter.ImageAdapter
import com.code.korti.seaofimages.presentation.adapter.ImageAdapterListener
import com.code.korti.seaofimages.utils.ItemOffsetDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile), ImageAdapterListener {

    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)

    private val viewModel by viewModels<ProfileViewModel>()

    private var pageOfImages = 1
    private var numberOfImages = 10
    private var isLoadingPage = false
    private var username: String = ""
    private var isFirstLaunch = true

    private var imageAdapter: ImageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.loadProfile()

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        bindViewModel()
        initList()
    }

    private fun bindViewModel() {
        viewModel.profile.observe(viewLifecycleOwner, ::showProfile)
        viewModel.isLoading.observe(viewLifecycleOwner, ::updateLoadingState)
        viewModel.imageList.observe(viewLifecycleOwner) { imagesList ->
            if(imagesList.isEmpty()){
                binding.noImageTextView.visibility = View.VISIBLE
            } else {
                binding.noImageTextView.visibility = View.GONE
                imageAdapter?.items = imagesList
            }
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showSnackbar(it)
            binding.appBarLayout.visibility = View.GONE
            binding.listOfLikedPhotos.visibility = View.GONE
        }
    }

    private fun showSnackbar(message: Int) {
        val snackbar = Snackbar.make(binding.constraintLayout, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun initList() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

        imageAdapter = ImageAdapter(this, requireContext())
        with(binding.listOfLikedPhotos) {
            adapter = imageAdapter
            addItemDecoration(ItemOffsetDecoration(requireContext()))
            layoutManager = linearLayoutManager
            itemAnimator = null
            setHasFixedSize(true)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val totalItemCount = linearLayoutManager.itemCount
                    isFirstLaunch = false

                    pageOfImages = totalItemCount / numberOfImages

                    val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                    if (!isLoadingPage && totalItemCount <=
                        lastVisibleItem + 2 && dy > 0 && totalItemCount % numberOfImages == 0
                    ) {
                        pageOfImages++
                        viewModel.loadPage(username, pageOfImages, numberOfImages)
                    }
                }
            })
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        if (isFirstLaunch) {
            binding.constraintLayout3.isVisible = !isLoading
            binding.appBarLayout.isVisible = !isLoading
            binding.progressBar.isVisible = isLoading
        } else {
            binding.constraintLayout3.isVisible = true
            binding.appBarLayout.isVisible = true
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

    private fun showProfile(profile: Profile) {

        username = profile.nickname
        viewModel.loadPage(profile.nickname, pageOfImages, numberOfImages)

        binding.name.text = profile.firstName + " " + profile.lastName
        binding.nickname.text = "@" + profile.nickname

        binding.location.text = profile.location

        if (profile.location == null) {
            binding.locationLayout.isVisible = false
        }

        Glide.with(requireContext())
            .load(profile.profileUrls?.small)
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.avatar)
    }

    override fun onImageClicked(cardView: View, image: ImageFromList) {
        val noteCardDetailTransitionName = getString(R.string.image_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(cardView to noteCardDetailTransitionName)
        val directions =
            ProfileFragmentDirections.actionProfileFragmentToDetailFragment(image.id)
        findNavController().navigate(directions, extras)
    }
}
package com.code.korti.seaofimages.presentation.detail

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.work.WorkInfo
import androidx.work.WorkManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.data.models.Image
import com.code.korti.seaofimages.databinding.FragmentDetailBinding
import com.code.korti.seaofimages.presentation.download.DownloadWorker
import com.code.korti.seaofimages.presentation.download.NotificationChannels
import com.code.korti.seaofimages.utils.DrawableColor
import com.code.korti.seaofimages.utils.toDp
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialSharedAxis

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val viewModel by viewModels<DetailViewModel>()

    private val binding: FragmentDetailBinding by viewBinding(FragmentDetailBinding::bind)
    private var click = false
    private var downloadLink: String = ""
    private var currentImageId: String = ""
    private var firstOpening = true

    private val args: DetailFragmentArgs by navArgs()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)

        transition()

        initPermissionResultListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(DownloadWorker.DOWNLOAD_WORK_ID)
            .observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    handleWorkInfo(it.first())
                }
            }

        viewModel.bind(args.imageId)
        bindViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.bind(args.imageId)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.downloadLayout.setOnClickListener {
            firstOpening = false
            if (hasPermission().not()) {
                requestPermissions()
            } else {
                viewModel.downloadImage(downloadLink)
            }
        }

        binding.favoriteImageView.setOnClickListener {
            when (click) {
                true -> {
                    viewModel.deleteLike(args.imageId)
                    binding.favoriteImageView
                        .setImageDrawable(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.ic_favorite_border
                            )
                        )
                    click = false
                }
                false -> {
                    viewModel.putLike(args.imageId)
                    binding.favoriteImageView
                        .setImageDrawable(
                            ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite)
                        )
                    click = true
                }
            }
        }
    }

    private fun showNotificationProgress() {
        val notification =
            NotificationCompat.Builder(requireContext(), NotificationChannels.DOWNLOAD_CHANNEL_ID)
                .setContentTitle(requireContext().resources.getString(R.string.downloading))
                .setSmallIcon(R.drawable.ic_download)
                .setProgress(0, 0, true)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(requireContext())
            .notify(1423, notification)
    }

    private fun showNotification(title: Int) {
        val galleryIntent = openingGalleryIntent()

        val pendingIntent =
            PendingIntent.getActivity(context, 1235, galleryIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(
            requireContext(),
            NotificationChannels.DOWNLOAD_CHANNEL_ID
        )
            .setContentTitle(requireContext().resources.getString(title))
            .setSmallIcon(R.drawable.ic_download)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(requireContext())
            .notify(1423, notification)
    }

    private fun bindViewModel() {
        viewModel.image.observe(viewLifecycleOwner) {
            initImage(it)
            downloadLink = it.urls.raw
            currentImageId = it.id
        }
        viewModel.isLoadingImage.observe(viewLifecycleOwner, ::updateLoadingImageState)
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            binding.scrollView.visibility = View.VISIBLE
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            showSnackbar(it, false)
            binding.scrollView.visibility = View.GONE
        }
    }

    private fun updateLoadingImageState(isLoading: Boolean) {
        binding.detailLayout.isVisible = !isLoading
        binding.progressBar.isVisible = isLoading
    }

    private fun initImage(image: Image) {

        val drawableColor = DrawableColor(requireContext())

        val shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.intrinsicHeight = image.height
        shapeDrawable.intrinsicWidth = image.width
        shapeDrawable.paint.color = drawableColor.getRandomDrawableColor().color

        if (image.likedByUser) {
            binding.favoriteImageView
                .setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.ic_favorite)
                )
        }
        click = image.likedByUser

        binding.name.text = image.user.name
        binding.nickname.text = "@" + image.user.nickname
        binding.likes.text = image.likes.toString()

        when {
            image.location.city == null &&
                    image.location.country == null -> {
                binding.locationLayout.visibility = View.GONE
            }
            image.location.city == null -> {
                binding.location.text = image.location.country
            }
            image.location.country == null -> {
                binding.location.text = image.location.city
            }
            else -> binding.location.text = "${image.location.city}, ${image.location.country}"
        }

        if (image.tags.isEmpty()) {
            binding.tags.visibility = GONE
        } else {
            binding.tags.text = image.tags.joinToString { "#${it.title}" }
        }

        if (image.exif.make == null &&
            image.exif.model == null &&
            image.exif.exposureTime == null &&
            image.exif.aperture == null &&
            image.exif.focalLength == null &&
            image.exif.iso == null
        ) {
            binding.linearLayout.visibility = GONE
            val set = ConstraintSet()
            set.clone(binding.detailLayout)
            set.clear(binding.biographyTextView.id, START)
            set.connect(
                binding.biographyTextView.id,
                START,
                PARENT_ID,
                START,
                16.toDp(requireContext())
            )
            set.applyTo(binding.detailLayout)
        } else {
            binding.makeTextView.text = "Made with: " + image.exif.make.orEmpty()
            binding.modelTextView.text = "Model: " + image.exif.model.orEmpty()
            binding.exposureTimeTextView.text = "Exposure: " + image.exif.exposureTime.orEmpty()
            binding.apertureTextView.text = "Aperture: " + image.exif.aperture.orEmpty()
            binding.focalLengthTextView.text = "Focal Length: " + image.exif.focalLength.orEmpty()
            binding.isoTextView.text = "ISO: " + image.exif.iso.orEmpty()
        }

        binding.biographyTextView.text = "About @" + image.user.nickname + ":\n" + image.user.bio

        binding.downloadTextView.paintFlags =
            binding.downloadTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.numberOfDownloads.text = "(${image.downloads})"

        val thumbnailRequest = Glide.with(requireContext())
            .load(image.urls.thumb)

        Glide.with(requireContext())
            .load(image.urls.regular)
            .thumbnail(thumbnailRequest)
            .placeholder(shapeDrawable)
            .into(binding.imageView)

        Glide.with(requireContext())
            .load(image.user.profileImage.medium)
            .placeholder(R.drawable.ic_account_circle)
            .into(binding.avatar)
    }

    private fun transition() {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragmentContainerView
            duration = 300L
            scrimColor = Color.TRANSPARENT
        }
    }

    private fun openingGalleryIntent(): Intent {
        val volume = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        } else {
            MediaStore.VOLUME_EXTERNAL
        }
        val imageUri = MediaStore.Images.Media.getContentUri(volume)

        return Intent(Intent.ACTION_VIEW).apply {
            type = "image/*"
            data = imageUri
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    private fun showSnackbar(message: Int, openGallery: Boolean) {
        val snackbar = Snackbar.make(binding.guideline, message, Snackbar.LENGTH_LONG)
            .apply { anchorView = binding.guideline }
        if (openGallery) {
            snackbar.setAction(R.string.open) {

                val galleryIntent = openingGalleryIntent()

                if (galleryIntent.resolveActivity(requireContext().packageManager) != null) {
                    startActivity(galleryIntent)
                } else {
                    Toast.makeText(requireContext(), "Фигу вам", Toast.LENGTH_LONG).show()
                }
            }.show()
        } else {
            snackbar.show()
        }
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {
        if (!firstOpening) {
            if (workInfo.state == WorkInfo.State.RUNNING) {
                showNotificationProgress()
                showSnackbar(R.string.execution_has_begun, false)
            }

            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                viewModel.trackPhotoDownload(currentImageId)
                showNotification(R.string.image_downloaded)
                showSnackbar(R.string.image_downloaded, true)
            }

            if (workInfo.state == WorkInfo.State.FAILED) {
                showNotification(R.string.download_error)
                showSnackbar(R.string.download_error, false)
            }
        }
    }

    //Permission
    private fun hasPermission(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initPermissionResultListener() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionToGrantedMap: Map<String, Boolean> ->
            if (permissionToGrantedMap.values.all { it }) {
                viewModel.permissionsGranted(downloadLink)
            } else {
                viewModel.permissionsDenied()
            }
        }
    }

    private fun requestPermissions() {
        requestPermissionLauncher.launch(*PERMISSIONS.toTypedArray())
    }

    companion object {
        private val PERMISSIONS = listOfNotNull(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                .takeIf { Build.VERSION.SDK_INT < Build.VERSION_CODES.Q }
        )
    }
}
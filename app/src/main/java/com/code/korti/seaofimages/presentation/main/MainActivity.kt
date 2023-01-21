package com.code.korti.seaofimages.presentation.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.code.korti.seaofimages.R
import com.code.korti.seaofimages.databinding.ActivityMainBinding
import com.code.korti.seaofimages.presentation.detail.DetailFragmentArgs
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(),
    Toolbar.OnMenuItemClickListener,
    NavController.OnDestinationChangedListener {

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind)

    private var currentImageId = "-1"

    private var backPressedTime: Long = 0

    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var currentDestination: NavDestination

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setImageIdFromIntentAndOpenDetailFragment()

        setUpBottomNavigation()

        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    Navigation.findNavController(this, R.id.fragmentContainerView)
                        .navigate(R.id.homeFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.page_2 -> {
                    Navigation.findNavController(this, R.id.fragmentContainerView)
                        .navigate(R.id.collectionsListFragment)
                    return@setOnItemSelectedListener true
                }
                R.id.page_3 -> {
                    Navigation.findNavController(this, R.id.fragmentContainerView)
                        .navigate(R.id.profileFragment)
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun setUpBottomNavigation() {
        binding.run {
            findNavController(R.id.fragmentContainerView).addOnDestinationChangedListener(
                this@MainActivity
            )
        }
        binding.appBarLayout.apply {
            setOnMenuItemClickListener(this@MainActivity)
        }
    }

    @MenuRes
    private fun getAppBarMenuForDestination(destination: NavDestination? = null): Int {
        val dest = destination ?: findNavController(R.id.fragmentContainerView).currentDestination
        return when (dest?.id) {
            R.id.homeFragment -> {
                currentDestination = dest
                R.menu.app_bar_home_menu
            }
            R.id.collectionsListFragment -> {
                currentDestination = dest
                R.menu.app_bar_home_menu
            }
            R.id.detailFragment -> {
                currentDestination = dest
                R.menu.app_bar_detail_menu
            }
            R.id.profileFragment -> {
                currentDestination = dest
                R.menu.add_bar_profile_menu
            }
            R.id.collectionFragment -> {
                currentDestination = dest
                R.menu.app_bar_home_menu
            }
            R.id.exitDialogFragment -> {
                currentDestination = dest
                R.menu.add_bar_profile_menu
            }
            else -> {
                R.menu.app_bar_home_menu
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                setBottomAppBarForDetail(getAppBarMenuForDestination(destination))
            }
            R.id.detailFragment -> {
                currentImageId =
                    if (arguments == null) "-1" else DetailFragmentArgs.fromBundle(arguments).imageId
                setBottomAppBarForDetail(getAppBarMenuForDestination(destination))
            }
            R.id.profileFragment -> {
                setBottomAppBarForDetail(getAppBarMenuForDestination(destination))
            }
            else -> {
                setBottomAppBarForDetail(getAppBarMenuForDestination(destination))
            }
        }
    }

    private fun showDialog() {
        Navigation.findNavController(this, R.id.fragmentContainerView)
            .navigate(R.id.exitDialogFragment)
    }

    private fun setBottomAppBarForDetail(@MenuRes menuRes: Int) {
        binding.run {
            bottomNavigation.visibility = View.VISIBLE
            appBarLayout.performShow()
            appBarLayout.visibility = View.VISIBLE
            appBarLayout.replaceMenu(menuRes)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_share -> {
                val imageIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "https://unsplash.com/photos/$currentImageId")
                }
                if (imageIntent.resolveActivity(packageManager) != null) {
                    startActivity(imageIntent)
                } else {
                    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_LONG).show()
                }
            }
            R.id.menu_search -> {
                searchItem = binding.appBarLayout.menu.findItem(R.id.menu_search)
                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                        binding.bottomNavigation.visibility = View.GONE
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                        binding.bottomNavigation.visibility = View.VISIBLE
                        return true
                    }
                })

                searchView = searchItem.actionView as SearchView
                searchView.queryHint = this.resources.getString(R.string.image_search)
                searchView.setOnQueryTextListener(
                    object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            searchView.clearFocus()
                            val args = Bundle()
                            args.putString("query", query)
                            Navigation.findNavController(
                                this@MainActivity,
                                R.id.fragmentContainerView
                            ).navigate(R.id.searchFragment, args)
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            return false
                        }
                    })
            }
            R.id.menu_exit -> {
                showDialog()
            }
        }
        return true
    }

    private fun setImageIdFromIntentAndOpenDetailFragment() {
        val imageIntent = intent
        val action = imageIntent.action
        val data = imageIntent.dataString

        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            val imageId = data.split("/").last()
            val arg = Bundle()
            arg.putString("imageId", imageId)
            findNavController(R.id.fragmentContainerView).navigate(R.id.detailFragment, arg)
        }
    }

    private fun showSnackbar(message: Int) {
        Snackbar.make(binding.bottomNavigation, message, Snackbar.LENGTH_LONG)
            .apply { anchorView =  binding.bottomNavigation}.show()
    }

    override fun onBackPressed() {
        if (this::searchView.isInitialized) {
            if (!searchView.isIconified) {
                searchView.isIconified = true
                searchItem.collapseActionView()
            } else {
                clickBackOnBottomNavigation()
            }
        } else {
            clickBackOnBottomNavigation()
        }
    }

    private fun clickBackOnBottomNavigation() {
        when (currentDestination.id) {
            R.id.homeFragment -> {
                if (backPressedTime + 3000 > System.currentTimeMillis()) {
                    super.onBackPressed()
                    finish()
                } else {
                    showSnackbar(R.string.click_again_to_exit)
                }
                backPressedTime = System.currentTimeMillis()
            }
            R.id.collectionsListFragment -> {
                binding.bottomNavigation.selectedItemId = R.id.page_1
            }
            R.id.profileFragment -> {
                binding.bottomNavigation.selectedItemId = R.id.page_1
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}
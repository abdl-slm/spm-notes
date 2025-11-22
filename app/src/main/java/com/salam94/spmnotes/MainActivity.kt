package com.salam94.spmnotes

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.salam94.spmnotes.ui.bookmark.BookMarkFragment
import com.salam94.spmnotes.ui.main.MainFragment
import com.salam94.spmnotes.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment
    private lateinit var bookmarkFragment: BookMarkFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Enable Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // --- ADD THIS BLOCK ---
        // Set Status Bar Color to Blue
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.colorPrimary) // Or R.color.blue

        // Make Status Bar Icons White (False = Light icons on Dark background)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        // ----------------------

        setContentView(R.layout.main_activity)
        MobileAds.initialize(this) {}

        // 2. Setup Safe Area Handling
        setupSafeArea()

        // Initialize Fragments
        // Check if fragments already exist (rotation) to avoid overlapping
        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance()
            bookmarkFragment = BookMarkFragment.newInstance()
            settingsFragment = SettingsFragment.newInstance()

            val fragmentManager = supportFragmentManager.beginTransaction()
            fragmentManager.add(R.id.container, mainFragment, "main")
            fragmentManager.add(R.id.container, bookmarkFragment, "bookmark") // Fixed tag
            fragmentManager.add(R.id.container, settingsFragment, "settings")

            fragmentManager.hide(bookmarkFragment)
            fragmentManager.hide(settingsFragment)
            fragmentManager.commit()
        } else {
            // Restore references to avoid creating new instances on rotation
            mainFragment = supportFragmentManager.findFragmentByTag("main") as MainFragment
            bookmarkFragment =
                supportFragmentManager.findFragmentByTag("bookmark") as BookMarkFragment
            settingsFragment =
                supportFragmentManager.findFragmentByTag("settings") as SettingsFragment
        }

        setupBottomBar()
    }

    private fun setupSafeArea() {
        val container = findViewById<View>(R.id.container)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Apply insets to the root view or specific views
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // Apply top padding (Status Bar) to the content container
            container.updatePadding(
                top = bars.top
            )

            // Apply bottom padding (Navigation Bar) to the BottomNav
            // We add the original padding if needed, but usually just setting it is enough
            bottomNav.updatePadding(
                bottom = bars.bottom
            )

            insets
        }
    }

    private fun setupBottomBar() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Setup Colors (Optional - usually done in styles.xml/themes.xml now)
        // But if you need to force it programmatically like before:
        // bottomNavigation.setBackgroundColor(resources.getColor(R.color.colorAccent))
        // bottomNavigation.itemIconTintList = ... (requires ColorStateList)

        bottomNavigation.setOnItemSelectedListener { item ->
            val ft = supportFragmentManager.beginTransaction()

            when (item.itemId) {
                R.id.nav_past_year -> {
                    ft.show(mainFragment)
                    ft.hide(bookmarkFragment)
                    ft.hide(settingsFragment)
                }

                R.id.nav_bookmark -> {
                    ft.hide(mainFragment)
                    ft.show(bookmarkFragment)
                    ft.hide(settingsFragment)
                }

                R.id.nav_settings -> {
                    ft.hide(mainFragment)
                    ft.hide(bookmarkFragment)
                    ft.show(settingsFragment)
                }
            }
            ft.commit()
            true
        }
    }
}
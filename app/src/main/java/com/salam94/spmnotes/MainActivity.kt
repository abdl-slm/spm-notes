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
import com.salam94.spmnotes.ui.pomodoro.PomodoroFragment // <--- IMPORT THIS
import com.salam94.spmnotes.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mainFragment: MainFragment
    private lateinit var bookmarkFragment: BookMarkFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var pomodoroFragment: PomodoroFragment // <--- 1. DECLARE VARIABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = androidx.core.content.ContextCompat.getColor(this, R.color.colorPrimary)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        setContentView(R.layout.main_activity)
        MobileAds.initialize(this) {}

        setupSafeArea()

        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance()
            bookmarkFragment = BookMarkFragment.newInstance()
            settingsFragment = SettingsFragment.newInstance()
            pomodoroFragment = PomodoroFragment() // <--- 2. INITIALIZE

            val fragmentManager = supportFragmentManager.beginTransaction()
            fragmentManager.add(R.id.container, mainFragment, "main")
            fragmentManager.add(R.id.container, bookmarkFragment, "bookmark")
            fragmentManager.add(R.id.container, settingsFragment, "settings")

            // <--- 3. ADD AND HIDE POMODORO
            fragmentManager.add(R.id.container, pomodoroFragment, "pomodoro")
            fragmentManager.hide(pomodoroFragment)
            // ---------------------------------

            fragmentManager.hide(bookmarkFragment)
            fragmentManager.hide(settingsFragment)
            fragmentManager.commit()
        } else {
            mainFragment = supportFragmentManager.findFragmentByTag("main") as MainFragment
            bookmarkFragment = supportFragmentManager.findFragmentByTag("bookmark") as BookMarkFragment
            settingsFragment = supportFragmentManager.findFragmentByTag("settings") as SettingsFragment

            // <--- 4. RESTORE ON ROTATION
            pomodoroFragment = supportFragmentManager.findFragmentByTag("pomodoro") as PomodoroFragment
        }

        setupBottomBar()
    }

    private fun setupSafeArea() {
        val container = findViewById<View>(R.id.container)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // <--- NEW: Set the background behind the status bar to Blue
        // Since we pad the container down, the "exposed" top padding shows this color.
        container.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(this, R.color.colorPrimaryDark)
        )

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView.rootView) { _, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )

            // The content is pushed down, revealing the Blue container background at the top
            container.updatePadding(
                top = bars.top
            )

            bottomNav.updatePadding(
                bottom = bars.bottom
            )

            insets
        }
    }

    private fun setupBottomBar() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            val ft = supportFragmentManager.beginTransaction()

            // <--- 5. ADD LOGIC TO HIDE/SHOW POMODORO
            when (item.itemId) {
                R.id.nav_past_year -> {
                    ft.show(mainFragment)
                    ft.hide(bookmarkFragment)
                    ft.hide(settingsFragment)
                    ft.hide(pomodoroFragment) // Hide Pomodoro
                }

                R.id.nav_bookmark -> {
                    ft.hide(mainFragment)
                    ft.show(bookmarkFragment)
                    ft.hide(settingsFragment)
                    ft.hide(pomodoroFragment) // Hide Pomodoro
                }

                // NEW CASE FOR TIMER
                R.id.nav_pomodoro -> {
                    ft.hide(mainFragment)
                    ft.hide(bookmarkFragment)
                    ft.hide(settingsFragment)
                    ft.show(pomodoroFragment)
                }

                R.id.nav_settings -> {
                    ft.hide(mainFragment)
                    ft.hide(bookmarkFragment)
                    ft.hide(pomodoroFragment) // Hide Pomodoro
                    ft.show(settingsFragment)
                }
            }
            ft.commit()
            true
        }
    }
}
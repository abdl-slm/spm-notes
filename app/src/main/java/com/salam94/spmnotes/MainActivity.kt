package com.salam94.spmnotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.gms.ads.MobileAds
import com.salam94.spmnotes.ui.bookmark.BookMarkFragment
import com.salam94.spmnotes.ui.main.MainFragment
import com.salam94.spmnotes.ui.pastyear.PastYearFragment
import com.salam94.spmnotes.ui.settings.SettingsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        MobileAds.initialize(this) {}
        val fragmentManager = supportFragmentManager.beginTransaction()
        val infoFragment = BookMarkFragment.newInstance()
        val mainFragment = MainFragment.newInstance()
        val settingsFragment = SettingsFragment.newInstance()
        if (savedInstanceState == null) {
            fragmentManager.add(R.id.container, mainFragment, "main")
            fragmentManager.add(R.id.container, infoFragment, "settings")
            fragmentManager.add(R.id.container, settingsFragment, "settings")
            fragmentManager.hide(infoFragment)
            fragmentManager.hide(settingsFragment)
            fragmentManager.commit()
        }
        setupBottomBar(mainFragment, infoFragment, settingsFragment)
    }

    private fun setupBottomBar(mainFragment: MainFragment, pastYearFragment: BookMarkFragment, settingsFragment: SettingsFragment){

        val bottomNavigation = findViewById<AHBottomNavigation>(R.id.bottom_navigation)

        val pastYear =
            AHBottomNavigationItem(
                "Past Year",
                R.drawable.ic_description_white_24dp,
                android.R.color.white
            )
        val bookmark =
            AHBottomNavigationItem(
                "Bookmark",
                R.drawable.ic_description_white_24dp,
                android.R.color.white
            )
        val settings = AHBottomNavigationItem(
            "Settings",
            R.drawable.ic_settings_white_24dp,
            android.R.color.white
        )

        bottomNavigation.addItem(pastYear)
        bottomNavigation.addItem(bookmark)
        bottomNavigation.addItem(settings)

        bottomNavigation.defaultBackgroundColor = resources.getColor(R.color.colorAccent)

        bottomNavigation.accentColor = resources.getColor(android.R.color.white)
        bottomNavigation.inactiveColor = resources.getColor(android.R.color.darker_gray)

        bottomNavigation.setOnTabSelectedListener { position, _ ->

            val ft = supportFragmentManager.beginTransaction()

            when (position) {
                0 -> {
                    ft.show(mainFragment)
                    ft.hide(pastYearFragment)
                    ft.hide(settingsFragment)
                    ft.commit()
                }
                1 -> {
                    ft.hide(settingsFragment)
                    ft.show(pastYearFragment)
                    ft.hide(mainFragment)
                    ft.commit()
                }
                2 -> {
                    ft.show(settingsFragment)
                    ft.hide(pastYearFragment)
                    ft.hide(mainFragment)
                    ft.commit()
                }
            }

            true
        }

    }
}
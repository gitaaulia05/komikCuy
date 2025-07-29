package com.example.uts

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

object buttonNavigation {
    fun setup(bottomNav: BottomNavigationView, activity: Activity, currentMenuId: Int) {
        bottomNav.selectedItemId = currentMenuId

        bottomNav.setOnItemSelectedListener { item ->
            // mencegah navigasi ulang ke halaman yang sama
            if (currentMenuId == item.itemId) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {
                R.id.menu_home -> {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    true
                }

                R.id.menu_profile -> {
                    val intent = Intent(activity, Profile::class.java)
                    activity.startActivity(intent)
                    true
                }

                R.id.menu_cs -> {
                    val intent = Intent(activity, CustomerSupport::class.java)
                    activity.startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}
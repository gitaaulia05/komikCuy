package com.example.uts

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

    object buttonNavigation {
        fun setup(bottomNav: BottomNavigationView, activity: Activity, currentMenuId: Int) {
            bottomNav.selectedItemId = currentMenuId

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_home -> {
                        if (currentMenuId != R.id.menu_home) {
                            val intent = Intent(activity, MainActivity::class.java)
                            activity.startActivity(intent)

                        }

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

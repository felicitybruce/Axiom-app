package com.example.axiom

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class FragmentUtils {
    companion object {
        fun navigateScreen(activity: AppCompatActivity, fragment: Fragment) {

            val nav = activity as FragmentNavigation
            nav.navigateFrag(fragment, false)
        }
    }
}

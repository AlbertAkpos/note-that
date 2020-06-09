package me.alberto.notethat.util

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import me.alberto.notethat.R
import me.alberto.notethat.ScrollChildSwipeRefreshLayout

fun Fragment.setupRefreshLayout(
    refreshLayout: ScrollChildSwipeRefreshLayout,
    scrollUpChild: View? = null
) {
    refreshLayout.setColorSchemeColors(
        ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
        ContextCompat.getColor(requireActivity(), R.color.colorAccent),
        ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
    )

    //Set the scrolling view in the custom SwipeRefreshLayout
    scrollUpChild?.let {
        refreshLayout.scrollupChild = it
    }
}
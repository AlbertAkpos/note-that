package me.alberto.notethat

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ScrollChildSwipeRefreshLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : SwipeRefreshLayout(context, attrs) {
    var scrollupChild: View? = null

    override fun canChildScrollUp(): Boolean {
        return scrollupChild?.canScrollVertically(-1) ?: super.canChildScrollUp()
    }
}

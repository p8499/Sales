package test.sales.common

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.roundToInt

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()
val Int.sp: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).roundToInt()

class SquareFrameLayout : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}

abstract class StateChangedListener : AppBarLayout.OnOffsetChangedListener {
    companion object {
        enum class State { EXPANDED, COLLAPSED, IDLE }
    }

    private var mCurrentState = State.IDLE
    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: State)
    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != State.EXPANDED)
                    onStateChanged(appBarLayout, State.EXPANDED)
                mCurrentState = State.EXPANDED
            }
            Math.abs(i) >= appBarLayout.totalScrollRange -> {
                if (mCurrentState != State.COLLAPSED)
                    onStateChanged(appBarLayout, State.COLLAPSED)
                mCurrentState = State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.IDLE)
                    onStateChanged(appBarLayout, State.IDLE)
                mCurrentState = State.IDLE
            }
        }
    }
}
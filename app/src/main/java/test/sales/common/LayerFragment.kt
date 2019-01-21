package test.sales.common

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.DialogFragment
import test.sales.R

abstract class LayerFragmentWrapper<T : LayerFragment> {
    abstract val layer: T?
    var place: ((layer: T) -> Unit) = { Unit }
        set(value) {
            field = value
            value.apply { layer?.also(value)?.refresh() }
        }
}

abstract class LayerFragment : DialogFragment() {
    companion object {
        val HORIZONTAL_LOCATE_CENTER = 0x0
        val HORIZONTAL_LOCATE_LEFT = 0x01
        val HORIZONTAL_LOCATE_RIGHT = 0x02
        val VERTICAL_LOCATE_CENTER = 0x00
        val VERTICAL_LOCATE_ABOVE = 0x01
        val VERTICAL_LOCATE_UNDER = 0x02
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.PopupTheme)
    }

    fun refresh() {
        view?.post {
            val window = dialog?.window
            val position = intArrayOf(arguments?.getInt("x") ?: 0, arguments?.getInt("y") ?: 0)
            val size = intArrayOf(window?.decorView?.width ?: 0, window?.decorView?.height ?: 0)
            val offset = intArrayOf(arguments?.getInt("offset_x") ?: 0, arguments?.getInt("offset_y") ?: 0)
            val locate = intArrayOf(
                    arguments?.getInt("horizontal_locate") ?: HORIZONTAL_LOCATE_CENTER,
                    arguments?.getInt("vertical_locate") ?: VERTICAL_LOCATE_CENTER)
            val x = when (locate[0]) {
                HORIZONTAL_LOCATE_LEFT -> position[0] - size[0] + offset[0]
                HORIZONTAL_LOCATE_RIGHT -> position[0] + offset[0]
                else -> position[0] - size[0] / 2 + offset[0]
            }
            val y = when (locate[1]) {
                VERTICAL_LOCATE_ABOVE -> position[1] - size[1] + offset[1]
                VERTICAL_LOCATE_UNDER -> position[1] + offset[1]
                else -> position[1] - size[1] / 2 + offset[1]
            }
            val attributes = window?.attributes
            attributes?.x = x
            attributes?.y = y
            window?.attributes = attributes
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return LayerDialog(activity!!, theme)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(width, height)
            setGravity(Gravity.START or Gravity.TOP)
        }
    }

    fun horizontal(x: Int, locate: Int) {
        arguments = (arguments ?: Bundle()).apply {
            putInt("x", x)
            putInt("horizontal_locate", locate)
        }
    }

    fun vertical(y: Int, locate: Int) {
        arguments = (arguments ?: Bundle()).apply {
            putInt("y", y)
            putInt("vertical_locate", locate)
        }
    }

    fun offset(x: Int, y: Int) {
        arguments = (arguments ?: Bundle()).apply {
            putInt("offset_x", x)
            putInt("offset_y", y)
        }
    }

    fun toLeftOf(anchor: View) {
        anchor.post { horizontal(IntArray(2).apply { anchor.getLocationInWindow(this) }[0], HORIZONTAL_LOCATE_LEFT) }
    }

    fun alignLeftWith(anchor: View) {
        anchor.post { horizontal(IntArray(2).apply { anchor.getLocationInWindow(this) }[0], HORIZONTAL_LOCATE_RIGHT) }
    }

    fun alignHorizontalCenter(anchor: View) {
        anchor.post { horizontal(IntArray(2).apply { anchor.getLocationInWindow(this) }[0] + anchor.width / 2, HORIZONTAL_LOCATE_CENTER) }
    }

    fun alignRightWith(anchor: View) {
        anchor.post { horizontal(IntArray(2).apply { anchor.getLocationInWindow(this) }[0] + anchor.width, HORIZONTAL_LOCATE_LEFT) }
    }

    fun toRightOf(anchor: View) {
        anchor.post { horizontal(IntArray(2).apply { anchor.getLocationInWindow(this) }[0] + anchor.width, HORIZONTAL_LOCATE_RIGHT) }
    }

    fun above(anchor: View) {
        anchor.post { vertical(IntArray(2).apply { anchor.getLocationInWindow(this) }[1] - statusBarHeight, VERTICAL_LOCATE_ABOVE) }
    }

    fun alignTopWith(anchor: View) {
        anchor.post { vertical(IntArray(2).apply { anchor.getLocationInWindow(this) }[1] - statusBarHeight, VERTICAL_LOCATE_UNDER) }
    }

    fun alignVerticalCenter(anchor: View) {
        anchor.post { vertical(IntArray(2).apply { anchor.getLocationInWindow(this) }[1] - statusBarHeight + anchor.height / 2, VERTICAL_LOCATE_CENTER) }
    }

    fun alignBottomWith(anchor: View) {
        anchor.post { vertical(IntArray(2).apply { anchor.getLocationInWindow(this) }[1] - statusBarHeight + anchor.height, VERTICAL_LOCATE_ABOVE) }
    }

    fun under(anchor: View) {
        anchor.post { vertical(IntArray(2).apply { anchor.getLocationInWindow(this) }[1] - statusBarHeight + anchor.height, VERTICAL_LOCATE_UNDER) }
    }

    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
        }

    abstract val width: Int
    abstract val height: Int
    open fun onTouchOutside(): Boolean = false
}

//always closeable and do not consider touch slop
private class LayerDialog : Dialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(
            context,
            cancelable,
            cancelListener
    )

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isShowing && shouldCloseOnTouch(event)) {
            cancel()
            return true
        } else
            false
    }

    private fun shouldCloseOnTouch(event: MotionEvent): Boolean {
        val isOutside = event.action == MotionEvent.ACTION_DOWN && isOutOfBounds(event) || event.action == MotionEvent.ACTION_OUTSIDE
        return window?.peekDecorView() != null && isOutside
    }

    private fun isOutOfBounds(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val decorView = window?.decorView
        return (x < 0 || y < 0
                || x > decorView?.width ?: 0
                || y > decorView?.height ?: 0)
    }
}
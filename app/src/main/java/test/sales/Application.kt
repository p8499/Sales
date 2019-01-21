package test.sales

import android.app.Application
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

enum class Status { IDLE, LOADING, CONTENT, ERROR }
enum class Mode { ADD, INSPECT, UPDATE, COPY }
interface PartialViewState
interface ViewState

class SalesApplication : Application() {
    override fun onCreate() {
        clearCacheDir()
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
        clearCacheDir()
    }

    fun clearCacheDir() {
        clearDir(cacheDir)
    }

    fun clearDir(dir: File) {
        if (dir.isDirectory) dir.list().forEach { clearDir(File(dir, it)) }
        dir.delete()
    }
}

abstract class Screen<V : MvpView, P : MviPresenter<V, out ViewState>> : MviActivity<V, P>() {
    var onReturn: (() -> Unit)? = null

    override fun onResume() {
        super.onResume()
        if (onReturn != null) {
            onReturn?.invoke()
            onReturn = null
        }
    }
}

private val atomicInteger = AtomicInteger()
fun nextInt() = atomicInteger.getAndIncrement()
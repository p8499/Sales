package test.sales.navigator

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import test.sales.Mode
import test.sales.Screen
import test.sales.ViewState
import test.sales.nextInt
import test.sales.screen.employee.EmployeeScreen

private val REQUEST_ADD_EMPLOYEE = nextInt()
private val REQUEST_INSPECT_EMPLOYEE = nextInt()
private val REQUEST_UPDATE_EMPLOYEE = nextInt()
private val REQUEST_COPY_EMPLOYEE = nextInt()

fun Activity.addEmployee() =
        startActivityForResult(Intent(this, EmployeeScreen::class.java)
                .putExtra("mode", Mode.ADD.ordinal)
                .putExtra("emidRef", 0), REQUEST_ADD_EMPLOYEE)


fun Activity.inspectEmployee(emidRef: Int) =
        startActivityForResult(Intent(this, EmployeeScreen::class.java)
                .putExtra("mode", Mode.INSPECT.ordinal)
                .putExtra("emidRef", emidRef), REQUEST_INSPECT_EMPLOYEE)

fun Activity.updateEmployee(emidRef: Int, sharedElement: View?) {
    sharedElement?.transitionName = "employee"
    startActivityForResult(
            Intent(this, EmployeeScreen::class.java)
                    .putExtra("mode", Mode.UPDATE.ordinal)
                    .putExtra("emidRef", emidRef),
            REQUEST_UPDATE_EMPLOYEE,
            sharedElement?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, sharedElement.transitionName).toBundle() })
}

fun Activity.copyEmployee(emidRef: Int) =
        startActivityForResult(Intent(this, EmployeeScreen::class.java)
                .putExtra("mode", Mode.COPY.ordinal)
                .putExtra("emidRef", emidRef), REQUEST_COPY_EMPLOYEE)

fun Activity.finishEmployee(emid: Int) {
    setResult(Activity.RESULT_OK, Intent().putExtra("emid", emid))
    finishAfterTransition()
}

fun <V, P, S> S.employeeReturn(requestCode: Int, resultCode: Int, data: Intent?, callbackAdd: ((Int) -> Unit)? = null, callbackInspect: ((Int) -> Unit)? = null, callbackUpdate: ((Int) -> Unit)? = null, callbackCopy: ((Int) -> Unit)? = null): Boolean where V : MvpView, P : MviPresenter<V, out ViewState>, S : Screen<V, P> =
        when (requestCode) {
            REQUEST_ADD_EMPLOYEE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.extras?.getInt("emid")?.let { callbackAdd?.invoke(it) } }
                true
            }
            REQUEST_INSPECT_EMPLOYEE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.extras?.getInt("emid")?.let { callbackInspect?.invoke(it) } }
                true
            }
            REQUEST_UPDATE_EMPLOYEE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.extras?.getInt("emid")?.let { callbackUpdate?.invoke(it) } }
                true
            }
            REQUEST_COPY_EMPLOYEE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.extras?.getInt("emid")?.let { callbackCopy?.invoke(it) } }
                true
            }
            else -> false
        }
package test.sales.navigator

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import com.hannesdorfmann.mosby3.mvi.MviPresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import com.yalantis.ucrop.UCrop
import test.sales.Screen
import test.sales.ViewState
import test.sales.nextInt
import test.sales.screen.image.ImageScreen

private val REQUEST_IMAGE = nextInt()
private val REQUEST_CAPTURE_IMAGE = nextInt()
private val REQUEST_PICK_IMAGE = nextInt()

fun Activity.image(image: Uri, sharedElement: View?) {
    sharedElement?.transitionName = "image"
    startActivityForResult(
            Intent(this, ImageScreen::class.java).setData(image),
            REQUEST_IMAGE,
            sharedElement?.let { ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, sharedElement.transitionName).toBundle() })
}

fun Activity.finishImage(image: Uri) {
    setResult(Activity.RESULT_OK, Intent().setData(image))
    finishAfterTransition()
}

fun <V, P, S> S.imageReturn(requestCode: Int, resultCode: Int, data: Intent?, callback: ((Uri) -> Unit)? = null): Boolean where V : MvpView, P : MviPresenter<V, out ViewState>, S : Screen<V, P> =
        when (requestCode) {
            REQUEST_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.data?.let { callback?.invoke(it) } }
                true
            }
            else -> false
        }

fun Activity.capture(output: Uri) =
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT, output)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION), REQUEST_CAPTURE_IMAGE)

fun <V, P, S> S.captureReturn(requestCode: Int, resultCode: Int, data: Intent?, callback: (() -> Unit)? = null): Boolean where V : MvpView, P : MviPresenter<V, out ViewState>, S : Screen<V, P> =
        when (requestCode) {
            REQUEST_CAPTURE_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = callback
                true
            }
            else -> false
        }

fun Activity.pick() =
        startActivityForResult(Intent(Intent.ACTION_PICK)
                .setType("image/*"), REQUEST_PICK_IMAGE)

fun <V, P, S> S.pickReturn(requestCode: Int, resultCode: Int, data: Intent?, callback: ((Uri) -> Unit)? = null): Boolean where V : MvpView, P : MviPresenter<V, out ViewState>, S : Screen<V, P> =
        when (requestCode) {
            REQUEST_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) onReturn = { data?.data?.let { callback?.invoke(it) } }
                true
            }
            else -> false
        }

fun Activity.crop(input: Uri, output: Uri) =
        UCrop.of(input, output)
                .withAspectRatio(1f, 1f)
                .start(this);

fun <V, P, S> S.cropReturn(requestCode: Int, resultCode: Int, data: Intent?, callback: (() -> Unit)? = null): Boolean where V : MvpView, P : MviPresenter<V, out ViewState>, S : Screen<V, P> =
        when (requestCode) {
            UCrop.REQUEST_CROP -> {
                if (resultCode == Activity.RESULT_OK) onReturn = callback
                true
            }
            else -> false
        }
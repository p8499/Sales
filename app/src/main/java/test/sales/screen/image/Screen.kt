package test.sales.screen.image

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.screen_image.*
import test.sales.R
import test.sales.Screen
import test.sales.layer.image_menu.ImageMenuLayer
import test.sales.navigator.*

class ImageScreen : Screen<ImageView, ImagePresenter>(), ImageView {
    private val imageMenu: ImageMenuLayer? get() = supportFragmentManager.findFragmentByTag("imageMenu") as? ImageMenuLayer
    private fun ImageMenuLayer.place() = findViewById<View>(R.id.image)?.let {
        alignTopWith(it)
        alignRightWith(it)
    }

    override fun createPresenter(): ImagePresenter = ImagePresenter(baseContext, this::capture, this::pick, this::crop, this::finishImage)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_image)
        image.setOnViewTapListener { v, x, y ->
            draftMenuIntent.onNext("")
        }
        image.post { imageMenu?.place() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        captureReturn(requestCode, resultCode, data) { cropIntent.onNext(source) } || pickReturn(requestCode, resultCode, data, cropIntent::onNext) || cropReturn(requestCode, resultCode, data) { confirmIntent.onNext(cropped) }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            imageMenu -> (fragment as ImageMenuLayer).place()
        }
    }

    override fun loadIntent(): Observable<Uri> = Observable.just(intent?.data ?: Uri.EMPTY)
    override val setImageIntent: Subject<Uri> = PublishSubject.create()
    override val draftMenuIntent: Subject<String> = PublishSubject.create()
    override val closeMenuIntent: Subject<Unit> = PublishSubject.create()
    override val captureIntent: Subject<Unit> = PublishSubject.create()
    override val pickIntent: Subject<Unit> = PublishSubject.create()
    override val cropIntent: Subject<Uri> = PublishSubject.create()
    override val confirmIntent: Subject<Uri> = PublishSubject.create()
    override val onDraft: ((String) -> Unit)?
        get() = {
            draftMenuIntent.onNext(it)
        }
    override val onCapture: (() -> Unit)?
        get() = {
            captureIntent.onNext(Unit)
        }
    override val onPick: (() -> Unit)?
        get() = {
            pickIntent.onNext(Unit)
        }
    override val onCloseMenu: (() -> Unit)?
        get() = {
            closeMenuIntent.onNext(Unit)
        }

    override fun render(viewState: ImageViewState) {
        source = viewState.source
        cropped = viewState.cropped
        //image
        if (image.tag != viewState.image) {
            image.tag = viewState.image
            if (viewState.image == Uri.EMPTY) image.setImageResource(R.drawable.ic_portrait_grey_320dp) else image.setImageURI(viewState.image)
        }
        //layer
        var imageMenu = imageMenu
        if (imageMenu == null && viewState.menuOpen)
            imageMenu = ImageMenuLayer().also { it.showNow(supportFragmentManager, "imageMenu") }
        else if (imageMenu != null && !viewState.menuOpen)
            imageMenu.dismiss()
        imageMenu?.render(viewState.menuDraft)
    }

    private var source: Uri = Uri.EMPTY
    private var cropped: Uri = Uri.EMPTY
}
package test.sales.screen.image

import android.net.Uri
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import test.sales.layer.image_menu.ImageMenuEvents

interface ImageView : MvpView, ImageMenuEvents {
    fun loadIntent(): Observable<Uri>
    val setImageIntent: Subject<Uri>
    val draftMenuIntent: Subject<String>
    val closeMenuIntent: Subject<Unit>
    val captureIntent: Subject<Unit>
    val pickIntent: Subject<Unit>
    val cropIntent: Subject<Uri>
    val confirmIntent: Subject<Uri>
    fun render(viewState: ImageViewState)
}
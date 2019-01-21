package test.sales.screen.image

import android.content.Context
import android.net.Uri
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import test.sales.common.createTempFile
import test.sales.common.createTempUri


class ImagePresenter(
        private val baseContext: Context,
        var capture: ((Uri) -> Unit)?,
        var pick: (() -> Unit)?,
        var crop: ((Uri, Uri) -> Unit)?,
        var finish: ((Uri) -> Unit)?) : MviBasePresenter<ImageView, ImageViewState>() {
    lateinit var compositeDisposable: CompositeDisposable
    override fun bindIntents() {
        compositeDisposable = CompositeDisposable()
        val allIntents = Observable.mergeArray(
                intent(ImageView::loadIntent).mapLoad(),
                intent(ImageView::setImageIntent).mapSetImage(),
                intent(ImageView::captureIntent).mapCapture(baseContext),
                intent(ImageView::pickIntent).mapPick(),
                intent(ImageView::cropIntent).mapCrop(baseContext),
                intent(ImageView::confirmIntent).mapConfirm(baseContext),
                intent(ImageView::draftMenuIntent).mapDraftMenu(),
                intent(ImageView::closeMenuIntent).mapCloseMenu())
        val stateObservable = allIntents.scan(initialImageViewState(), ::reduceImageViewState).share().observeOn(AndroidSchedulers.mainThread())
        stateObservable
                .filter { it.navigation != Navigation.None }
                .subscribe {
                    when (it.navigation) {
                        is Navigation.Capture -> capture?.invoke(it.navigation.uri)
                        is Navigation.Pick -> pick?.invoke()
                        is Navigation.Crop -> crop?.invoke(it.navigation.input, it.navigation.output)
                        is Navigation.Finish -> finish?.invoke(it.image)
                    }
                }.addTo(compositeDisposable)
        subscribeViewState(stateObservable.distinctUntilChanged(), ImageView::render)
    }

    override fun unbindIntents() {
        compositeDisposable.dispose()
        capture = null
        pick = null
        crop = null
        finish = null
        super.unbindIntents()
    }
}


fun Observable<Uri>.mapLoad(): Observable<ImagePartialViewState> = map { ImagePartialViewState.SetImage(it) }

fun Observable<Uri>.mapSetImage(): Observable<ImagePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(ImagePartialViewState.SetImage(it)),
                    Observable.just(ImagePartialViewState.SetCache(Uri.EMPTY, Uri.EMPTY)))
        }

fun Observable<String>.mapDraftMenu(): Observable<ImagePartialViewState> = map { ImagePartialViewState.DraftMenu(it) }

fun Observable<Unit>.mapCloseMenu(): Observable<ImagePartialViewState> = map { ImagePartialViewState.CloseMenu }

fun Observable<Unit>.mapCapture(baseContext: Context): Observable<ImagePartialViewState> =
        flatMap {
            baseContext.createTempUri(".jpg").let { uri ->
                Observable.concat(
                        Observable.just(ImagePartialViewState.SetCache(uri, Uri.EMPTY)),
                        Observable.just(ImagePartialViewState.Capture(uri)))
            }
        }

fun Observable<Unit>.mapPick(): Observable<ImagePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(ImagePartialViewState.SetCache(Uri.EMPTY, Uri.EMPTY)),
                    Observable.just(ImagePartialViewState.Pick))
        }

fun Observable<Uri>.mapCrop(baseContext: Context): Observable<ImagePartialViewState> =
        flatMap { source ->
            Uri.fromFile(baseContext.createTempFile(".png")).let {
                Observable.concat(
                        Observable.just(ImagePartialViewState.SetCache(source, it)),
                        Observable.just(ImagePartialViewState.Crop(source, it)))
            }
        }

fun Observable<Uri>.mapConfirm(baseContext: Context): Observable<ImagePartialViewState> = flatMap {
    Observable.concat(
            Observable.just(ImagePartialViewState.SetImage(it)),
            Observable.just(ImagePartialViewState.Finish))
}

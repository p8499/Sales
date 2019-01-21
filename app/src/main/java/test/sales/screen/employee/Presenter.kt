package test.sales.screen.employee

import android.content.Context
import android.net.Uri
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import okhttp3.ResponseBody
import retrofit2.Response
import test.sales.Mode
import test.sales.common.Struct2
import test.sales.common.Struct5
import test.sales.common.bytes
import test.sales.gen.bean.Employee
import test.sales.gen.mask.EmployeeMask
import test.sales.gen.stub.EmployeeStub
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection

class EmployeePresenter(
        private val baseContext: Context,
        var portrait: ((Uri) -> Unit)?,
        var finish: ((Int) -> Unit)?) : MviBasePresenter<EmployeeView, EmployeeViewState>() {
    lateinit var compositeDisposable: CompositeDisposable
    override fun bindIntents() {
        compositeDisposable = CompositeDisposable()
        val allIntents = Observable.mergeArray(
                intent(EmployeeView::loadIntent).mapLoad(baseContext),
                intent(EmployeeView::reloadIntent).mapReLoad(baseContext),
                intent(EmployeeView::refreshIntent).mapRefresh(baseContext),
                intent(EmployeeView::changePortraitIntent).mapChangePortrait(baseContext),
                intent(EmployeeView::changeEmidIntent).mapChangeEmid(),
                intent(EmployeeView::changeEmstatusIntent).mapChangeEmstatus(),
                intent(EmployeeView::draftEmgenderIntent).mapDraftEmgender(),
                intent(EmployeeView::changeEmgenderIntent).mapChangeEmgender(),
                intent(EmployeeView::closeEmgenderIntent).mapCloseEmgender(),
                intent(EmployeeView::changeEmnameIntent).mapChangeEmname(),
                intent(EmployeeView::portraitIntent).mapPortrait(),
                intent(EmployeeView::addIntent).mapAdd(baseContext),
                intent(EmployeeView::updateIntent).mapUpdate(baseContext))
        val stateObservable = allIntents.scan(initialEmployeeViewState(), ::reduceEmployeeViewState).share().observeOn(AndroidSchedulers.mainThread())
        stateObservable
                .filter { it.navigation != Navigation.None }
                .subscribe {
                    when (it.navigation) {
                        is Navigation.Portrait -> portrait?.invoke(it.navigation.uri)
                        is Navigation.Finish -> finish?.invoke(it.emid.toIntOrNull() ?: 0)
                    }
                }.addTo(compositeDisposable)
        subscribeViewState(stateObservable.distinctUntilChanged(), EmployeeView::render)
    }

    override fun unbindIntents() {
        compositeDisposable.dispose()
        finish = null
        super.unbindIntents()
    }
}


fun Observable<Load>.mapLoad(baseContext: Context): Observable<EmployeePartialViewState> =
        flatMap { load ->
            Observable.concatArray(
                    Observable.just(EmployeePartialViewState.SetMode(load.mode)),
                    Observable.just(EmployeePartialViewState.SetEmidRef(load.emidRef)),
                    Observable.just(load as Query).mapReLoad(baseContext))
        }

fun Observable<Query>.mapReLoad(baseContext: Context): Observable<EmployeePartialViewState> =
        flatMap { query ->
            when (query.mode) {
                Mode.ADD -> observableLoadFromBlank()
                Mode.INSPECT, Mode.UPDATE -> observableLoadFromServer(baseContext, query, false)
                Mode.COPY -> observableLoadFromServer(baseContext, query, true)
            }
        }

fun Observable<Query>.mapRefresh(baseContext: Context): Observable<EmployeePartialViewState> =
        flatMap { query ->
            when (query.mode) {
                Mode.INSPECT, Mode.UPDATE -> observableRefresh(baseContext, query)
                Mode.ADD, Mode.COPY -> Observable.never<EmployeePartialViewState>()
            }
        }

fun Observable<Uri>.mapChangePortrait(baseContext: Context): Observable<EmployeePartialViewState> = map { EmployeePartialViewState.ContentSetPortrait(it) }

fun Observable<String>.mapChangeEmid(): Observable<EmployeePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(EmployeePartialViewState.ContentSetEmid(it)),
                    boCheckEmid(it).second)
        }

fun Observable<Boolean>.mapChangeEmstatus(): Observable<EmployeePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(EmployeePartialViewState.ContentSetEmstatus(it)),
                    boCheckEmstatus(it).second)
        }

fun Observable<String>.mapDraftEmgender(): Observable<EmployeePartialViewState> = map { EmployeePartialViewState.ContentDraftEmgender(it) }

fun Observable<String>.mapChangeEmgender(): Observable<EmployeePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(EmployeePartialViewState.ContentSetEmgender(it)),
                    boCheckEmgender(it).second)
        }

fun Observable<Unit>.mapCloseEmgender(): Observable<EmployeePartialViewState> = map { EmployeePartialViewState.ContentCloseEmgender }

fun Observable<String>.mapChangeEmname(): Observable<EmployeePartialViewState> =
        flatMap {
            Observable.concat(
                    Observable.just(EmployeePartialViewState.ContentSetEmname(it)),
                    boCheckEmname(it).second)
        }

fun Observable<Unit>.mapPortrait(): Observable<EmployeePartialViewState> = map { EmployeePartialViewState.Portrait }

fun Observable<Struct5<Uri, String, Boolean, String, String>>.mapAdd(baseContext: Context): Observable<EmployeePartialViewState> =
        flatMap { struct ->
            val checkEmid = boCheckEmid(struct.member2)
            val checkEmstatus = boCheckEmstatus(struct.member3)
            val checkEmgender = boCheckEmgender(struct.member4)
            val checkEmname = boCheckEmname(struct.member5)
            Observable.concatArray(checkEmid.second, checkEmstatus.second, checkEmgender.second, checkEmname.second).let {
                if (checkEmid.first && checkEmstatus.first && checkEmgender.first && checkEmname.first)
                    it.concatWith(observableAdd(baseContext, struct.member1, struct.member2, struct.member3, struct.member4, struct.member5))
                else
                    it
            }
        }

fun Observable<Struct5<Uri, String, Boolean, String, String>>.mapUpdate(baseContext: Context): Observable<EmployeePartialViewState> =
        flatMap { struct ->
            val checkEmid = boCheckEmid(struct.member2)
            val checkEmstatus = boCheckEmstatus(struct.member3)
            val checkEmgender = boCheckEmgender(struct.member4)
            val checkEmname = boCheckEmname(struct.member5)
            Observable.concatArray(checkEmid.second, checkEmstatus.second, checkEmgender.second, checkEmname.second).let {
                if (checkEmid.first && checkEmstatus.first && checkEmgender.first && checkEmname.first)
                    Observable.concat(it, observableUpdate(baseContext, struct.member1, struct.member2, struct.member3, struct.member4, struct.member5))
                else it
            }
        }

private fun observableLoadFromBlank() = Observable.just(EmployeePartialViewState.ContentInit())

private fun observableLoadFromServer(baseContext: Context, query: Query, skipKey: Boolean) =
        Observable.zip(
                EmployeeStub.getInstance(baseContext).get(query.emidRef, EmployeeMask().apply { emid = true; emstatus = true; emgender = true; emname = true }).toObservable(),
                EmployeeStub.getInstance(baseContext).downloadAttachment(query.emidRef, "portrait.png").toObservable(),
                BiFunction { resp1: Response<Employee>, resp2: Response<ResponseBody> -> Struct2(resp1, resp2) })
                .map { responses ->
                    when {
                        responses.member1.code() == HttpsURLConnection.HTTP_OK ->
                            //发送ContentInit
                            EmployeePartialViewState.ContentInit(
                                    responses.member2.body()?.writeImage(baseContext)?.let { it } ?: Uri.EMPTY,
                                    if (skipKey) "" else responses.member1.body()!!.emid.toString(),
                                    responses.member1.body()!!.emstatus == Employee.EMSTATUS_VALID,
                                    responses.member1.body()!!.emgender,
                                    responses.member1.body()!!.emname ?: "")
                        responses.member1.code() == HttpsURLConnection.HTTP_UNAUTHORIZED ->
                            //发送Error
                            EmployeePartialViewState.Error("Unauthorized")
                        else ->
                            //发送Error
                            EmployeePartialViewState.Error("Error")
                    }
                }
                //预发送Loading
                .startWith(EmployeePartialViewState.Loading)
                //发送Error
                .onErrorReturn { error -> EmployeePartialViewState.Error(error.message ?: "") }

private fun observableRefresh(baseContext: Context, query: Query) =
        Observable.zip(
                EmployeeStub.getInstance(baseContext).get(query.emidRef, EmployeeMask().apply { emid = true; emstatus = true; emgender = true; emname = true }).toObservable(),
                EmployeeStub.getInstance(baseContext).downloadAttachment(query.emidRef, "portrait.png").toObservable(),
                BiFunction { resp1: Response<Employee>, resp2: Response<ResponseBody> -> Struct2(resp1, resp2) })
                .flatMap { responses ->
                    when {
                        responses.member1.code() == HttpsURLConnection.HTTP_OK ->
                            //发送ContentInit
                            Observable.just(EmployeePartialViewState.ContentInit(
                                    responses.member2.body()?.writeImage(baseContext)?.let { it } ?: Uri.EMPTY,
                                    responses.member1.body()!!.emid.toString(),
                                    responses.member1.body()!!.emstatus == Employee.EMSTATUS_VALID,
                                    responses.member1.body()!!.emgender,
                                    responses.member1.body()!!.emname ?: ""))
                        else ->
                            System.currentTimeMillis().let { time ->
                                Observable.timer(2, TimeUnit.SECONDS)
                                        //发送Popup(null)
                                        .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                        //预发送Popup(文字)
                                        .startWith(when {
                                            responses.member1.code() == HttpsURLConnection.HTTP_UNAUTHORIZED -> EmployeePartialViewState.ContentPopup("Unauthorized", time)
                                            else -> EmployeePartialViewState.ContentPopup("Error", time)
                                        })
                            }
                    }
                }
                //预发送Waiting
                .startWith(EmployeePartialViewState.ContentWaiting)
                .onErrorResumeNext { error: Throwable ->
                    System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(EmployeePartialViewState.ContentPopup(error.message ?: "", time))
                    }
                }

private fun boCheckEmid(emid: String): Pair<Boolean, Observable<EmployeePartialViewState>> {
    return when {
        emid.toIntOrNull() == null -> false to Observable.just(EmployeePartialViewState.ContentSetEmidError("Invalid Value") as EmployeePartialViewState)
        emid.toInt().let { it < Employee.CONSTRAINT_EMID_MIN || it > Employee.CONSTRAINT_EMID_MAX } -> false to Observable.just(EmployeePartialViewState.ContentSetEmidError("Out of Range") as EmployeePartialViewState)
        else -> true to Observable.just(EmployeePartialViewState.ContentSetEmidError("") as EmployeePartialViewState)
    }
}

private fun boCheckEmstatus(emstatus: Boolean): Pair<Boolean, Observable<EmployeePartialViewState>> {
    return true to Observable.just(EmployeePartialViewState.ContentSetEmstatusError("") as EmployeePartialViewState)
}

private fun boCheckEmgender(emgender: String): Pair<Boolean, Observable<EmployeePartialViewState>> {
    return if (emgender == Employee.EMGENDER_MALE || emgender == Employee.EMGENDER_FEMALE) true to Observable.just(EmployeePartialViewState.ContentSetEmgenderError("") as EmployeePartialViewState)
    else false to Observable.just(EmployeePartialViewState.ContentSetEmgenderError("Invalid Value") as EmployeePartialViewState)
}

private fun boCheckEmname(emname: String): Pair<Boolean, Observable<EmployeePartialViewState>> {
    return when {
        emname.isBlank() -> false to Observable.just(EmployeePartialViewState.ContentSetEmnameError("Name Is Required") as EmployeePartialViewState)
        emname.length > Employee.CONSTRAINT_EMNAME_LENGTH_STRING -> false to Observable.just(EmployeePartialViewState.ContentSetEmnameError("Name too long") as EmployeePartialViewState)
        else -> true to Observable.just(EmployeePartialViewState.ContentSetEmnameError("") as EmployeePartialViewState)
    }
}

private fun observableAdd(baseContext: Context, portrait: Uri, emid: String, emstatus: Boolean, emgender: String, emname: String): Observable<EmployeePartialViewState> {
    val success = { key: Int ->
        Observable.concat(Observable.just(
                EmployeePartialViewState.ContentDone),
                Observable.just(EmployeePartialViewState.Finish(key)))
    }
    val uploadPortrait = { key: Int ->
        EmployeeStub.getInstance(baseContext).uploadAttachment(key, "portrait.png", portrait.bytes(baseContext)).toObservable()
                .flatMap { response ->
                    if (response.code() == HttpsURLConnection.HTTP_OK)
                        success(key)
                    else System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(when {
                                    response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED -> EmployeePartialViewState.ContentPopup("Unauthorized", time)
                                    else -> EmployeePartialViewState.ContentPopup("Error", time)
                                })
                    }
                }
    }
    val addBean = {
        EmployeeStub.getInstance(baseContext).add(Employee(emid.toIntOrNull() ?: 0, if (emstatus) Employee.EMSTATUS_VALID else Employee.EMSTATUS_INVALID, emgender, emname, null)).toObservable()
                .flatMap { response ->
                    if (response.code() == HttpsURLConnection.HTTP_OK)
                        Observable.concat(
                                //发送ContentInit（更新主键UI）
                                Observable.just(EmployeePartialViewState.ContentSetEmid(response.body()!!.emid.toString()) as EmployeePartialViewState),
                                uploadPortrait(response.body()!!.emid))
                    else System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(when {
                                    response.code() == HttpsURLConnection.HTTP_BAD_REQUEST -> EmployeePartialViewState.ContentPopup("Bad Request", time)
                                    response.code() == HttpsURLConnection.HTTP_CONFLICT -> EmployeePartialViewState.ContentPopup("Conflict", time)
                                    else -> EmployeePartialViewState.ContentPopup("Error", time)
                                })
                    }
                }
    }
    return addBean()
            //预发送Waiting
            .startWith(EmployeePartialViewState.ContentWaiting)
            .onErrorResumeNext { error: Throwable ->
                System.currentTimeMillis().let { time ->
                    Observable.timer(2, TimeUnit.SECONDS)
                            //发送Popup(null)
                            .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                            //预发送Popup(文字)
                            .startWith(EmployeePartialViewState.ContentPopup(error.message ?: "", time))
                }
            }
}

private fun observableUpdate(baseContext: Context, portrait: Uri, emid: String, emstatus: Boolean, emgender: String, emname: String): Observable<EmployeePartialViewState> {
    val success = { key: Int ->
        Observable.concat(Observable.just(
                EmployeePartialViewState.ContentDone),
                Observable.just(EmployeePartialViewState.Finish(key)))
    }
    val uploadPortrait = { key: Int ->
        EmployeeStub.getInstance(baseContext).uploadAttachment(key, "portrait.png", portrait.bytes(baseContext)).toObservable()
                .flatMap { response ->
                    if (response.code() == HttpsURLConnection.HTTP_OK)
                        success(key)
                    else System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(when {
                                    response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED -> EmployeePartialViewState.ContentPopup("Unauthorized", time)
                                    else -> EmployeePartialViewState.ContentPopup("Error", time)
                                })
                    }
                }
    }
    val deletePortrait = { key: Int ->
        EmployeeStub.getInstance(baseContext).deleteAttachment(key, "portrait.png").toObservable()
                .flatMap { response ->
                    if (response.code() == HttpsURLConnection.HTTP_OK || response.code() == HttpsURLConnection.HTTP_NO_CONTENT)
                        success(key)
                    else System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(when {
                                    response.code() == HttpsURLConnection.HTTP_UNAUTHORIZED -> EmployeePartialViewState.ContentPopup("Unauthorized", time)
                                    else -> EmployeePartialViewState.ContentPopup("Error", time)
                                })
                    }
                }
    }
    val updateBean = {
        EmployeeStub.getInstance(baseContext).update(employee(emid, emstatus, emgender, emname), EmployeeMask().apply { this.emid = true; this.emstatus = true; this.emgender = true; this.emname = true }).toObservable()
                .flatMap { response ->
                    if (response.code() == HttpsURLConnection.HTTP_OK)
                        if (portrait != Uri.EMPTY) uploadPortrait(response.body()!!.emid) else deletePortrait(response.body()!!.emid)
                    else System.currentTimeMillis().let { time ->
                        Observable.timer(2, TimeUnit.SECONDS)
                                //发送Popup(null)
                                .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                                //预发送Popup(文字)
                                .startWith(when {
                                    response.code() == HttpsURLConnection.HTTP_BAD_REQUEST -> EmployeePartialViewState.ContentPopup("Bad Request", time)
                                    else -> EmployeePartialViewState.ContentPopup("Error", time)
                                })
                    }
                }
    }
    return updateBean()
            //预发送Waiting
            .startWith(EmployeePartialViewState.ContentWaiting)
            .onErrorResumeNext { error: Throwable ->
                System.currentTimeMillis().let { time ->
                    Observable.timer(2, TimeUnit.SECONDS)
                            //发送Popup(null)
                            .map { EmployeePartialViewState.ContentClearPopup(time) as EmployeePartialViewState }
                            //预发送Popup(文字)
                            .startWith(EmployeePartialViewState.ContentPopup(error.message ?: "", time))
                }
            }
}

private fun employee(emid: String, emstatus: Boolean, emgender: String, emname: String): Employee {
    return Employee(emid.toIntOrNull() ?: 0, if (emstatus) Employee.EMSTATUS_VALID else Employee.EMSTATUS_INVALID, emgender, emname, null)
}

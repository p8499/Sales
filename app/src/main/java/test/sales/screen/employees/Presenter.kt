package test.sales.screen.employees

import android.content.Context
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import test.sales.gen.stub.EmployeeStub
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class EmployeesPresenter(
        private val baseContext: Context,
        var add: (() -> Unit)?,
        var update: ((Int) -> Unit)?) : MviBasePresenter<EmployeesView, EmployeesViewState>() {

    lateinit var compositeDisposable: CompositeDisposable
    override fun bindIntents() {
        compositeDisposable = CompositeDisposable()
        val allIntents = Observable.mergeArray(
                intent(EmployeesView::loadIntent).mapLoad(baseContext),
                intent(EmployeesView::reloadIntent).mapReload(baseContext),
                intent(EmployeesView::draftKeywordIntent).mapDraftKeyword(),
                intent(EmployeesView::changeKeywordIntent).mapChangeKeyword(),
                intent(EmployeesView::draftFilterStatusListIntent).mapDraftFilterStatusList(),
                intent(EmployeesView::changeFilterStatusListIntent).mapChangeFilterStatusList(),
                intent(EmployeesView::closeFilterStatusListIntent).mapCloseFilterStatusList(),
                intent(EmployeesView::draftFilterGenderListIntent).mapDraftFilterGenderList(),
                intent(EmployeesView::changeFilterGenderListIntent).mapChangeFilterGenderList(),
                intent(EmployeesView::closeFilterGenderListIntent).mapCloseFilterGenderList(),
                intent(EmployeesView::draftResultSortIntent).mapDraftResultSort(),
                intent(EmployeesView::changeResultSortIntent).mapChangeResultSort(),
                intent(EmployeesView::closeResultSortIntent).mapCloseResultSort(),
                intent(EmployeesView::refreshIntent).mapRefresh(baseContext),
                intent(EmployeesView::refreshSingleIntent).mapRefreshSingle(baseContext),
                intent(EmployeesView::loadMoreIntent).mapLoadMore(baseContext),
                intent(EmployeesView::setSelectionIntent).mapSetSelection(),
                intent(EmployeesView::clearSelectionIntent).mapClearSelection(),
                intent(EmployeesView::deleteIntent).mapDelete(baseContext),
                intent(EmployeesView::addIntent).mapAdd(),
                intent(EmployeesView::updateIntent).mapUpdate())
        val stateObservable = allIntents.scan(initialEmployeesViewState(), ::reduceEmployeesViewState).share().observeOn(AndroidSchedulers.mainThread())
        stateObservable
                .filter { it.navigation != Navigation.None }
                .subscribe {
                    when (it.navigation) {
                        is Navigation.Add -> add?.invoke()
                        is Navigation.Update -> update?.invoke(it.navigation.emid)
                    }
                }.addTo(compositeDisposable)
        subscribeViewState(stateObservable.distinctUntilChanged(), EmployeesView::render)
    }

    override fun unbindIntents() {
        compositeDisposable.dispose()
        add = null
        update = null
        super.unbindIntents()
    }
}


fun Observable<Load>.mapLoad(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { load ->
            Observable.concatArray(
                    Observable.just(EmployeesPartialViewState.ChangeKeyword(load.keyword)),
                    Observable.just(EmployeesPartialViewState.ChangeFilterStatusList(load.filterStatusList)),
                    Observable.just(EmployeesPartialViewState.ChangeFilterGenderList(load.filterGenderList)),
                    Observable.just(EmployeesPartialViewState.ChangeResultSort(load.resultSort)),
                    Observable.just(EmployeesPartialViewState.ChangePageSize(load.pageSize)),
                    Observable.just(load as Query).mapReload(baseContext))
        }

fun Observable<Query>.mapReload(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { query ->
            EmployeeStub.getInstance(baseContext).query(toFilterLogicExpr(query.keyword, query.filterStatusList, query.filterGenderList), toOrderByListExpr(query.resultSort), toRangeExpr(0, query.pageSize), null).toObservable()
                    //发送ContentFirstPage
                    .map { response -> EmployeesPartialViewState.ContentRefresh(response.body() ?: emptyList()) as EmployeesPartialViewState }
                    //预发送Loading
                    .startWith(EmployeesPartialViewState.Loading)
                    //发送Error
                    .onErrorReturn { error -> EmployeesPartialViewState.Error(error.message ?: "") }
        }

fun Observable<String>.mapDraftKeyword(): Observable<EmployeesPartialViewState> = map { keyword -> EmployeesPartialViewState.DraftKeyword(keyword) }
fun Observable<String>.mapChangeKeyword(): Observable<EmployeesPartialViewState> = map { keyword -> EmployeesPartialViewState.ChangeKeyword(keyword) }

fun Observable<List<Int>>.mapDraftFilterStatusList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.DraftFilterStatusList(it) }
fun Observable<List<Int>>.mapChangeFilterStatusList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.ChangeFilterStatusList(it) }
fun Observable<Unit>.mapCloseFilterStatusList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.CloseFilterStatusList }

fun Observable<List<String>>.mapDraftFilterGenderList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.DraftFilterGenderList(it) }
fun Observable<List<String>>.mapChangeFilterGenderList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.ChangeFilterGenderList(it) }
fun Observable<Unit>.mapCloseFilterGenderList(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.CloseFilterGenderList }

fun Observable<Pair<String, Boolean>>.mapDraftResultSort(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.DraftResultSort(it) }
fun Observable<Pair<String, Boolean>>.mapChangeResultSort(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.ChangeResultSort(it) }
fun Observable<Unit>.mapCloseResultSort(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.CloseResultSort }

fun Observable<Query>.mapRefresh(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { query ->
            EmployeeStub.getInstance(baseContext).query(toFilterLogicExpr(query.keyword, query.filterStatusList, query.filterGenderList), toOrderByListExpr(query.resultSort), toRangeExpr(0, query.pageSize), null).toObservable()
                    //发送ContentFirstPage
                    .map { response -> EmployeesPartialViewState.ContentRefresh(response.body() ?: emptyList()) as EmployeesPartialViewState }
                    //预发送ContentRefreshing
                    .startWith(EmployeesPartialViewState.ContentRefreshing)
                    .onErrorResumeNext { error: Throwable ->
                        System.currentTimeMillis().let { time ->
                            Observable.timer(2, TimeUnit.SECONDS)
                                    //发送Popup(null)
                                    .map { EmployeesPartialViewState.ContentClearPopup(time) as EmployeesPartialViewState }
                                    //预发送Popup(文字)
                                    .startWith(EmployeesPartialViewState.ContentNextPagePopup(error.message ?: "", time))
                        }
                    }
        }

fun Observable<Int>.mapRefreshSingle(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { emid ->
            EmployeeStub.getInstance(baseContext).get(emid, null).toObservable()
                    //发送ContentRefreshSingle
                    .flatMap { response -> response.body()?.let { Observable.just(EmployeesPartialViewState.ContentRefreshSingle(it)) } ?: Observable.empty<EmployeesPartialViewState>() }
                    //预发送ContentRefreshing
                    .startWith(EmployeesPartialViewState.ContentRefreshing)
                    .onErrorResumeNext { error: Throwable ->
                        System.currentTimeMillis().let { time ->
                            Observable.timer(2, TimeUnit.SECONDS)
                                    //发送Popup(null)
                                    .map { EmployeesPartialViewState.ContentClearPopup(time) as EmployeesPartialViewState }
                                    //预发送Popup(文字)
                                    .startWith(EmployeesPartialViewState.ContentNextPagePopup(error.message ?: "", time))
                        }
                    }
        }

fun Observable<QueryContinue>.mapLoadMore(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { query ->
            EmployeeStub.getInstance(baseContext).query(toFilterLogicExpr(query.keyword, query.filterStatusList, query.filterGenderList), toOrderByListExpr(query.resultSort), toRangeExpr(query.from, query.pageSize), null).toObservable()
                    //发送ContentNextPage
                    .map { response -> EmployeesPartialViewState.ContentNextPage(response.body() ?: emptyList()) as EmployeesPartialViewState }
                    //预发送ContentLoadingMore
                    .startWith(EmployeesPartialViewState.ContentLoadingMore)
                    .onErrorResumeNext { error: Throwable ->
                        System.currentTimeMillis().let { time ->
                            Observable.timer(2, TimeUnit.SECONDS)
                                    //发送Popup(null)
                                    .map { EmployeesPartialViewState.ContentClearPopup(time) as EmployeesPartialViewState }
                                    //预发送Popup(文字)
                                    .startWith(EmployeesPartialViewState.ContentNextPagePopup(error.message ?: "", time))
                        }
                    }
        }

fun Observable<List<Int>>.mapSetSelection(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.ContentSetSelection(it) }
fun Observable<Unit>.mapClearSelection(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.ContentClearSelection }

fun Observable<List<Int>>.mapDelete(baseContext: Context): Observable<EmployeesPartialViewState> =
        flatMap { ids ->
            Observable.fromIterable(ids)
                    .flatMap({ emid -> EmployeeStub.getInstance(baseContext).delete(emid).toObservable() }, { emid, response -> Pair(emid, response) }, true)
                    .collect({ mutableListOf<Pair<Int, Int>>() }, { result, pair -> result += pair.first to pair.second.code() })
                    .flatMapObservable { result ->
                        Observable.mergeArray(
                                //发送ContentDelete
                                Observable.just(EmployeesPartialViewState.ContentDelete(result.filter { it.second == HttpURLConnection.HTTP_OK }.map { it.first })),
                                *mutableListOf<Observable<EmployeesPartialViewState>>().apply {
                                    result.count { it.second != HttpURLConnection.HTTP_OK }.takeIf { it > 0 }?.let { count ->
                                        add(System.currentTimeMillis().let { time ->
                                            Observable.timer(2, TimeUnit.SECONDS)
                                                    //发送Popup(null)
                                                    .map { EmployeesPartialViewState.ContentClearPopup(time) as EmployeesPartialViewState }
                                                    //预发送Popup(总结)
                                                    .startWith(
                                                            if (count == 1)
                                                                when (result.first { it.second != HttpURLConnection.HTTP_OK }.second) {
                                                                    403 -> EmployeesPartialViewState.ContentDeletePopup("Delete not permitted", time)
                                                                    else -> EmployeesPartialViewState.ContentDeletePopup("Error", time)
                                                                }
                                                            else
                                                                EmployeesPartialViewState.ContentDeletePopup("${result.count { it.second != HttpURLConnection.HTTP_OK }} items failed", time))
                                        })
                                    }
                                }.toTypedArray())
                    }
                    //预发送ContentDeleting
                    .startWith(EmployeesPartialViewState.ContentDeleting)
                    .onErrorResumeNext { error: Throwable ->
                        System.currentTimeMillis().let { time ->
                            Observable.timer(2, TimeUnit.SECONDS)
                                    //发送Popup(null)
                                    .map { EmployeesPartialViewState.ContentClearPopup(time) as EmployeesPartialViewState }
                                    //预发送Popup(文字)
                                    .startWith(EmployeesPartialViewState.ContentDeletePopup(error.message ?: "", time) as EmployeesPartialViewState)
                        }
                    }
        }

fun Observable<Unit>.mapAdd(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.Add }

fun Observable<Int>.mapUpdate(): Observable<EmployeesPartialViewState> = map { EmployeesPartialViewState.Update(it) }

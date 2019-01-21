package test.sales.screen.employees

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import test.sales.layer.emgender_multiselect.EmgenderMultiselectEvents
import test.sales.layer.emsort_singleselect.EmsortSingleselectEvents
import test.sales.layer.emstatus_multiselect.EmstatusMultiselectEvents

interface EmployeesView : MvpView, EmstatusMultiselectEvents, EmgenderMultiselectEvents, EmsortSingleselectEvents {
    fun loadIntent(): Observable<Load>
    val reloadIntent: Subject<Query>
    val draftKeywordIntent: Subject<String>
    val changeKeywordIntent: Subject<String>
    val draftFilterStatusListIntent: Subject<List<Int>>
    val changeFilterStatusListIntent: Subject<List<Int>>
    val closeFilterStatusListIntent: Subject<Unit>
    val draftFilterGenderListIntent: Subject<List<String>>
    val changeFilterGenderListIntent: Subject<List<String>>
    val closeFilterGenderListIntent: Subject<Unit>
    val draftResultSortIntent: Subject<Pair<String, Boolean>>
    val changeResultSortIntent: Subject<Pair<String, Boolean>>
    val closeResultSortIntent: Subject<Unit>
    val refreshIntent: Subject<Query>
    val refreshSingleIntent: Subject<Int>
    val loadMoreIntent: Subject<QueryContinue>
    val setSelectionIntent: Subject<List<Int>>
    val clearSelectionIntent: Subject<Unit>
    val deleteIntent: Subject<List<Int>>
    val addIntent: Subject<Unit>
    val updateIntent: Subject<Int>
    fun render(viewState: EmployeesViewState)
}
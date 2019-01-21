package test.sales.screen.employee

import android.net.Uri
import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import io.reactivex.subjects.Subject
import test.sales.common.Struct5
import test.sales.layer.emgender_singleselect.EmgenderSingleselectEvents

interface EmployeeView : MvpView, EmgenderSingleselectEvents {
    fun loadIntent(): Observable<Load>
    val reloadIntent: Subject<Query>
    val refreshIntent: Subject<Query>
    val changePortraitIntent: Subject<Uri>
    val changeEmidIntent: Subject<String>
    val changeEmstatusIntent: Subject<Boolean>
    val draftEmgenderIntent: Subject<String>
    val changeEmgenderIntent: Subject<String>
    val closeEmgenderIntent: Subject<Unit>
    val changeEmnameIntent: Subject<String>
    val portraitIntent: Subject<Unit>
    val addIntent: Subject<Struct5<Uri, String, Boolean, String, String>>
    val updateIntent: Subject<Struct5<Uri, String, Boolean, String, String>>
    fun render(viewState: EmployeeViewState)
}
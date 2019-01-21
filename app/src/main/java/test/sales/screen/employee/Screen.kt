package test.sales.screen.employee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.screen_employee.*
import test.sales.Mode
import test.sales.R
import test.sales.Screen
import test.sales.Status
import test.sales.common.StateChangedListener
import test.sales.common.Struct5
import test.sales.gen.bean.Employee
import test.sales.layer.emgender_singleselect.EmgenderSingleselectLayer
import test.sales.navigator.finishEmployee
import test.sales.navigator.image
import test.sales.navigator.imageReturn

class EmployeeScreen : Screen<EmployeeView, EmployeePresenter>(), EmployeeView {
    private val sharedElementPortrait: View get() = portrait
    private val emgenderSingleselect: EmgenderSingleselectLayer? get() = supportFragmentManager.findFragmentByTag("emgenderSingleselect") as? EmgenderSingleselectLayer
    private fun EmgenderSingleselectLayer.place() = findViewById<View>(R.id.emgender)?.let { under(it) }

    override fun createPresenter(): EmployeePresenter = EmployeePresenter(baseContext, { image(it, sharedElementPortrait) }, this::finishEmployee)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_employee)
        refresh.isEnabled = false
        appbar.addOnOffsetChangedListener(object : StateChangedListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: Companion.State) {
                emgenderSingleselect?.place()
            }
        })
        portrait.setOnClickListener { portraitIntent.onNext(Unit) }
        toolbar.apply {
            inflateMenu(R.menu.layer_employee)
            menu.findItem(R.id.refresh).setOnMenuItemClickListener {
                refreshIntent.onNext(Query(mode, emidRef))
                true
            }
            menu.findItem(R.id.save).setOnMenuItemClickListener {
                if (mode == Mode.ADD || mode == Mode.COPY)
                    addIntent.onNext(Struct5(portrait.tag as? Uri ?: Uri.EMPTY, emid.text.toString(), emstatus.isChecked, emgender.tag.toString(), emname.text.toString()))
                else if (mode == Mode.UPDATE)
                    updateIntent.onNext(Struct5(portrait.tag as? Uri ?: Uri.EMPTY, emid.text.toString(), emstatus.isChecked, emgender.tag.toString(), emname.text.toString()))
                true
            }
        }
        emid.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                changeEmidIntent.onNext(s.toString())
            }
        })
        emstatus.setOnCheckedChangeListener { _, isChecked ->
            changeEmstatusIntent.onNext(isChecked)
        }
        emgender_area.setOnClickListener { draftEmgenderIntent.onNext(emgender.tag.toString()) }
        emgender.post { emgenderSingleselect?.place() }
        emname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                changeEmnameIntent.onNext(s.toString())
            }
        })
        error.setOnClickListener { reloadIntent.onNext(Query(mode, emidRef)) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageReturn(requestCode, resultCode, data, changePortraitIntent::onNext)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            emgenderSingleselect -> (fragment as EmgenderSingleselectLayer).place()
        }
    }

    override fun loadIntent(): Observable<Load> {
        val extras = intent?.extras
        return Observable.just(Load(
                extras?.getInt("mode")?.let { mode -> Mode.values().firstOrNull { it.ordinal == mode } } ?: Mode.ADD,
                extras?.getInt("emidRef") ?: 0))
    }

    override val reloadIntent: Subject<Query> = PublishSubject.create()
    override val refreshIntent: Subject<Query> = PublishSubject.create()
    override val changePortraitIntent: Subject<Uri> = PublishSubject.create()
    override val changeEmidIntent: Subject<String> = PublishSubject.create()
    override val changeEmstatusIntent: Subject<Boolean> = PublishSubject.create()
    override val draftEmgenderIntent: Subject<String> = PublishSubject.create()
    override val changeEmgenderIntent: Subject<String> = PublishSubject.create()
    override val closeEmgenderIntent: Subject<Unit> = PublishSubject.create()
    override val changeEmnameIntent: Subject<String> = PublishSubject.create()
    override val portraitIntent: Subject<Unit> = PublishSubject.create()
    override val addIntent: Subject<Struct5<Uri, String, Boolean, String, String>> = PublishSubject.create()
    override val updateIntent: Subject<Struct5<Uri, String, Boolean, String, String>> = PublishSubject.create()

    override val onDraftGender: ((String) -> Unit)?
        get() = {
            draftEmgenderIntent.onNext(it)
        }
    override val onConfirmGender: ((String) -> Unit)?
        get() = {
            changeEmgenderIntent.onNext(it)
        }
    override val onCloseGender: (() -> Unit)?
        get() = {
            closeEmgenderIntent.onNext(Unit)
        }

    override fun render(viewState: EmployeeViewState) {
        mode = viewState.mode
        emidRef = viewState.emidRef
        //lce
        loading.visibility = if (viewState.status == Status.LOADING) View.VISIBLE else View.GONE
        refresh.visibility = if (viewState.status == Status.CONTENT) View.VISIBLE else View.GONE
        error.visibility = if (viewState.status == Status.ERROR) View.VISIBLE else View.GONE
        //lce-content-菜单
        toolbar.menu.findItem(R.id.refresh).isVisible = (viewState.mode == Mode.INSPECT || viewState.mode == Mode.UPDATE) && !viewState.waiting
        toolbar.menu.findItem(R.id.save).isVisible = (viewState.mode == Mode.ADD || viewState.mode == Mode.UPDATE || viewState.mode == Mode.COPY) && !viewState.waiting
        //lce-content-等待条
        refresh.isRefreshing = viewState.waiting
        //lce-content-portrait
        if (portrait.tag != viewState.portrait) {
            portrait.tag = viewState.portrait
            if (viewState.portrait == Uri.EMPTY) portrait.setImageResource(R.drawable.ic_portrait_grey_320dp) else portrait.setImageURI(viewState.portrait)
        }
        //lce-content-emid
        emid.isEnabled = viewState.mode == Mode.ADD || viewState.mode == Mode.COPY
        if (emid.text.toString() != viewState.emid)
            emid.setText(viewState.emid)
        emid.error = if (viewState.emidError.isEmpty()) null else viewState.emidError
        //lce-content-emstatus
        emstatus.isEnabled = viewState.mode == Mode.ADD || viewState.mode == Mode.COPY || viewState.mode == Mode.UPDATE
        if (emstatus.isChecked != viewState.emstatus)
            emstatus.isChecked = viewState.emstatus
        emstatus.error = if (viewState.emstatusError.isEmpty()) null else viewState.emstatusError
        //lce-content-emgender
        emgender_area.isClickable = viewState.mode == Mode.ADD || viewState.mode == Mode.COPY || viewState.mode == Mode.UPDATE
        if (emgender.tag != viewState.emgender) {
            emgender.tag = viewState.emgender
            emgender.text = when {
                viewState.emgender == Employee.EMGENDER_MALE -> "Male"
                viewState.emgender == Employee.EMGENDER_FEMALE -> "Female"
                else -> ""
            }
        }
        emgender.error = if (viewState.emgenderError.isEmpty()) null else viewState.emgenderError
        //lce-content-emname
        emname.isEnabled = viewState.mode == Mode.ADD || viewState.mode == Mode.COPY || viewState.mode == Mode.UPDATE
        if (emname.text.toString() != viewState.emname) {
            emname.setText(viewState.emname)
        }
        emname.error = if (viewState.emnameError.isEmpty()) null else viewState.emnameError
        //lce-content-提示
        if (viewState.popupMessage.isNotEmpty())
            if (snackbar != null && snackbar?.isShown!!)
            //snackbar已有内容显示，改显示新的文字
                snackbar?.setText(viewState.popupMessage)
            else
            //snackbar不在，要显示文字
                snackbar = Snackbar.make(refresh, viewState.popupMessage, Snackbar.LENGTH_INDEFINITE).apply {
                    setText(viewState.popupMessage)
                    show()
                }
        else
            if (snackbar != null && snackbar?.isShown!!) {
                //snackbar已有内容显示，要去除
                snackbar?.dismiss()
                snackbar = null
            }
        //lce-error
        error.text = "${viewState.message}, Tab to retry"
        //layers
        var emgenderSingleselect = emgenderSingleselect
        if (emgenderSingleselect == null && viewState.emgenderOpen)
            emgenderSingleselect = EmgenderSingleselectLayer().also { it.showNow(supportFragmentManager, "emgenderSingleselect") }
        else if (emgenderSingleselect != null && !viewState.emgenderOpen)
            emgenderSingleselect.dismiss()
        emgenderSingleselect?.render(viewState.emgenderDraft)
    }

    private var mode: Mode = Mode.ADD
    private var emidRef: Int = 0
    private var snackbar: Snackbar? = null

}
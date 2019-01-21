package test.sales.screen.employees

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.screen_employees.*
import kotlinx.android.synthetic.main.screen_employees_item.view.*
import kotlinx.android.synthetic.main.screen_employees_option.view.*
import test.sales.R
import test.sales.Screen
import test.sales.Status
import test.sales.common.EndlessListAdapter
import test.sales.common.ListAdapter
import test.sales.common.StateChangedListener
import test.sales.common.TYPE_NORMAL
import test.sales.gen.bean.Employee
import test.sales.layer.emgender_multiselect.EmgenderMultiselectLayer
import test.sales.layer.emsort_singleselect.EmsortSingleselectLayer
import test.sales.layer.emstatus_multiselect.EmstatusMultiselectLayer
import test.sales.navigator.addEmployee
import test.sales.navigator.employeeReturn
import test.sales.navigator.updateEmployee
import java.util.*

class EmployeesScreen : Screen<EmployeesView, EmployeesPresenter>(), EmployeesView {
    private val keyword: String get() = ((options.adapter as OptionsAdapter).dataList.find { it is Option.Keyword } as Option.Keyword?)?.keyword ?: ""
    private val filterStatusList: List<Int> get() = ((options.adapter as OptionsAdapter).dataList.find { it is Option.FilterStatusList } as Option.FilterStatusList?)?.filterStatusList ?: listOf()
    private val filterGenderList: List<String> get() = ((options.adapter as OptionsAdapter).dataList.find { it is Option.FilterGenderList } as Option.FilterGenderList?)?.filterGenderList ?: listOf()
    private val emstatusMultiselect: EmstatusMultiselectLayer? get() = supportFragmentManager.findFragmentByTag("emstatusMultiselect") as? EmstatusMultiselectLayer
    private fun EmstatusMultiselectLayer.place() = findViewById<View>(R.id.toolbar)?.let { under(it) }
    private val emgenderMultiselect: EmgenderMultiselectLayer? get() = supportFragmentManager.findFragmentByTag("emgenderMultiselect") as? EmgenderMultiselectLayer
    private fun EmgenderMultiselectLayer.place() = findViewById<View>(R.id.toolbar)?.let { under(it) }
    private val emsortSingleselect: EmsortSingleselectLayer? get() = supportFragmentManager.findFragmentByTag("emsortSingleselect") as? EmsortSingleselectLayer
    private fun EmsortSingleselectLayer.place() = findViewById<View>(R.id.toolbar)?.let { under(it) }

    override fun createPresenter(): EmployeesPresenter = EmployeesPresenter(baseContext, this::addEmployee) { updateEmployee(it, sharedElementEmployee) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_employees)
        appbar.addOnOffsetChangedListener(object : StateChangedListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: Companion.State) {
                emstatusMultiselect?.place()
                emgenderMultiselect?.place()
                emsortSingleselect?.place()
            }
        })
        toolbar.post {
            emstatusMultiselect?.place()
            emgenderMultiselect?.place()
            emsortSingleselect?.place()
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                (newText ?: "").let { draftKeywordIntent.onNext(it) }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                (query ?: "").let {
                    changeKeywordIntent.onNext(it)
                    reloadIntent.onNext(Query(it, filterStatusList, filterGenderList, orderBy, pageSize))
                }
                return true
            }
        })
        status_area.setOnClickListener {
            draftFilterStatusListIntent.onNext(filterStatusList)
        }
        gender_area.setOnClickListener {
            draftFilterGenderListIntent.onNext(filterGenderList)
        }
        sort_area.setOnClickListener {
            draftResultSortIntent.onNext(orderBy)
        }
        options.apply {
            adapter = OptionsAdapter().apply {
                onKeywordClick = {
                    "".let {
                        changeKeywordIntent.onNext(it)
                        reloadIntent.onNext(Query(it, filterStatusList, filterGenderList, orderBy, pageSize))
                    }
                }
                onFilterStatusListClick = {
                    listOf<Int>().let {
                        changeFilterStatusListIntent.onNext(it)
                        reloadIntent.onNext(Query(keyword, it, filterGenderList, orderBy, pageSize))
                    }
                }
                onFilterGenderListClick = {
                    listOf<String>().let {
                        changeFilterGenderListIntent.onNext(it)
                        reloadIntent.onNext(Query(keyword, filterStatusList, it, orderBy, pageSize))
                    }
                }
            }
            layoutManager = ChipsLayoutManager.newBuilder(context).setChildGravity(Gravity.BOTTOM).build()
            addItemDecoration(OptionsItemDecoration())
        }
        refresh.setOnRefreshListener {
            refreshIntent.onNext(Query(keyword, filterStatusList, filterGenderList, orderBy, pageSize))
        }
        items.apply {
            adapter = ItemsAdapter().apply {
                onSelect = { _, employee ->
                    setSelectionIntent.onNext((if (selectedList.contains(employee)) selectedList - employee else selectedList + employee).map { it.emid })
                }
                onClick = { position, employee ->
                    sharedElementEmployee = items.findViewHolderForAdapterPosition(position)?.itemView
                    updateIntent.onNext(employee.emid)
                }
            }
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemsItemDecoration())
        }
        items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            val layoutManager = items.layoutManager as LinearLayoutManager
            val adapter = items.adapter as ItemsAdapter
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1
                        && adapter.hasMore
                        && !adapter.loadingMore)
                    loadMoreIntent.onNext(QueryContinue(keyword, filterStatusList, filterGenderList, orderBy, adapter.itemCount, pageSize))
            }
        })
        cancel.setOnClickListener {
            clearSelectionIntent.onNext(Unit)
        }
        delete.setOnClickListener {
            deleteIntent.onNext((items.adapter as ItemsAdapter).selectedList.map { it.emid })
        }
        error.setOnClickListener {
            reloadIntent.onNext(Query(keyword, filterStatusList, filterGenderList, orderBy, pageSize))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        employeeReturn(requestCode, resultCode, data, null, null, refreshSingleIntent::onNext, null)
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            emstatusMultiselect -> (fragment as EmstatusMultiselectLayer).place()
            emgenderMultiselect -> (fragment as EmgenderMultiselectLayer).place()
            emsortSingleselect -> (fragment as EmsortSingleselectLayer).place()
        }
    }

    override fun loadIntent(): Observable<Load> = Observable.just(Load("", listOf(), listOf(), Pair(Employee.FIELD_EMID, true), 20))
    override val reloadIntent: Subject<Query> = PublishSubject.create()
    override val draftKeywordIntent: Subject<String> = PublishSubject.create()
    override val changeKeywordIntent: Subject<String> = PublishSubject.create()
    override val draftFilterStatusListIntent: Subject<List<Int>> = PublishSubject.create()
    override val changeFilterStatusListIntent: Subject<List<Int>> = PublishSubject.create()
    override val closeFilterStatusListIntent: Subject<Unit> = PublishSubject.create()
    override val draftFilterGenderListIntent: Subject<List<String>> = PublishSubject.create()
    override val changeFilterGenderListIntent: Subject<List<String>> = PublishSubject.create()
    override val closeFilterGenderListIntent: Subject<Unit> = PublishSubject.create()
    override val draftResultSortIntent: Subject<Pair<String, Boolean>> = PublishSubject.create()
    override val changeResultSortIntent: Subject<Pair<String, Boolean>> = PublishSubject.create()
    override val closeResultSortIntent: Subject<Unit> = PublishSubject.create()
    override val refreshIntent: Subject<Query> = PublishSubject.create()
    override val refreshSingleIntent: Subject<Int> = PublishSubject.create()
    override val loadMoreIntent: Subject<QueryContinue> = PublishSubject.create()
    override val setSelectionIntent: Subject<List<Int>> = PublishSubject.create()
    override val clearSelectionIntent: Subject<Unit> = PublishSubject.create()
    override val deleteIntent: Subject<List<Int>> = PublishSubject.create()
    override val addIntent: Subject<Unit> = PublishSubject.create()
    override val updateIntent: Subject<Int> = PublishSubject.create()

    override val onDraftStatusList: ((List<Int>) -> Unit)?
        get() = {
            draftFilterStatusListIntent.onNext(it)
        }
    override val onConfirmStatusList: ((List<Int>) -> Unit)?
        get() = {
            changeFilterStatusListIntent.onNext(it)
            reloadIntent.onNext(Query(keyword, it, filterGenderList, orderBy, pageSize))
        }
    override val onCloseStatusList: (() -> Unit)?
        get() = {
            closeFilterStatusListIntent.onNext(Unit)
        }
    override val onDraftGenderList: ((List<String>) -> Unit)?
        get() = {
            draftFilterGenderListIntent.onNext(it)
        }
    override val onConfirmGenderList: ((List<String>) -> Unit)?
        get() = {
            changeFilterGenderListIntent.onNext(it)
            reloadIntent.onNext(Query(keyword, filterStatusList, it, orderBy, pageSize))
        }
    override val onCloseGenderList: (() -> Unit)?
        get() = {
            closeFilterGenderListIntent.onNext(Unit)
        }
    override val onDraftSort: ((Pair<String, Boolean>) -> Unit)?
        get() = {
            draftResultSortIntent.onNext(it)
        }
    override val onConfirmSort: ((Pair<String, Boolean>) -> Unit)?
        get() = {
            changeResultSortIntent.onNext(it)
            reloadIntent.onNext(Query(keyword, filterStatusList, filterGenderList, it, pageSize))
        }
    override val onCloseSort: (() -> Unit)?
        get() = {
            closeResultSortIntent.onNext(Unit)
        }

    override fun render(viewState: EmployeesViewState) {
        pageSize = viewState.pageSize
        orderBy = viewState.resultSort
        //bar
        if (search.query.toString() != viewState.keywordDraft)
            search.setQuery(viewState.keywordDraft, false)
        status_area.isActivated = viewState.filterStatusList.isNotEmpty()
        gender_area.isActivated = viewState.filterGenderList.isNotEmpty()
        status_area.isSelected = viewState.filterStatusListOpen
        gender_area.isSelected = viewState.filterGenderListOpen
        sort_area.isSelected = viewState.resultSortOpen
        //options
        (options.adapter as OptionsAdapter).dataList = mutableListOf<Option>().apply {
            if (viewState.keyword.isNotEmpty()) add(Option.Keyword(viewState.keyword))
            if (viewState.filterStatusList.isNotEmpty()) add(Option.FilterStatusList(viewState.filterStatusList))
            if (viewState.filterGenderList.isNotEmpty()) add(Option.FilterGenderList(viewState.filterGenderList))
        }
        //lce
        loading.visibility = if (viewState.status == Status.LOADING) View.VISIBLE else View.GONE
        refresh.visibility = if (viewState.status == Status.CONTENT) View.VISIBLE else View.GONE
        error.visibility = if (viewState.status == Status.ERROR) View.VISIBLE else View.GONE
        //lce-content-等待条
        refresh.isRefreshing = viewState.waiting
        //lce-content-列表
        (items.adapter as ItemsAdapter).apply {
            set(viewState.employeeList, viewState.hasMore, viewState.selecting, viewState.selectedList.map { Employee().apply { emid = it } })
            loadingMore = viewState.loadingMore
        }
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
        //action
        action.visibility = if (viewState.status == Status.CONTENT && viewState.selecting) View.VISIBLE else View.GONE
        hint.text = if (viewState.selectedList.size <= 1) "${viewState.selectedList.size} item selected" else "${viewState.selectedList.size} items selected"
        delete.isEnabled = viewState.selectedList.isNotEmpty() && !viewState.deleting
        //layers
        var emstatusMultiselect = emstatusMultiselect
        if (emstatusMultiselect == null && viewState.filterStatusListOpen)
            emstatusMultiselect = EmstatusMultiselectLayer().also { it.showNow(supportFragmentManager, "emstatusMultiselect") }
        else if (emstatusMultiselect != null && !viewState.filterStatusListOpen)
            emstatusMultiselect.dismiss()
        emstatusMultiselect?.render(viewState.filterStatusListDraft)
        var emgenderMultiselect = emgenderMultiselect
        if (emgenderMultiselect == null && viewState.filterGenderListOpen)
            emgenderMultiselect = EmgenderMultiselectLayer().also { it.showNow(supportFragmentManager, "emgenderMultiselect") }
        else if (emgenderMultiselect != null && !viewState.filterGenderListOpen)
            emgenderMultiselect.dismiss()
        emgenderMultiselect?.render(viewState.filterGenderListDraft)
        var emsortSingleselect = emsortSingleselect
        if (emsortSingleselect == null && viewState.resultSortOpen)
            emsortSingleselect = EmsortSingleselectLayer().also { it.showNow(supportFragmentManager, "emsortSingleselect") }
        else if (emsortSingleselect != null && !viewState.resultSortOpen)
            emsortSingleselect.dismiss()
        emsortSingleselect?.render(viewState.resultSortDraft)
    }

    private var sharedElementEmployee: View? = null
    private var orderBy: Pair<String, Boolean> = Pair("", true)
    private var pageSize: Int = 0
    private var snackbar: Snackbar? = null
}

private sealed class Option {
    class Keyword(val keyword: String) : Option()
    class FilterStatusList(val filterStatusList: List<Int>) : Option()
    class FilterGenderList(val filterGenderList: List<String>) : Option()
}

private class OptionsAdapter : ListAdapter<Option>() {
    var onKeywordClick: ((Option.Keyword) -> Unit)? = null
    var onFilterStatusListClick: ((Option.FilterStatusList) -> Unit)? = null
    var onFilterGenderListClick: ((Option.FilterGenderList) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.screen_employees_option, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val datum = dataList[position]
        holder.itemView.label.text = datum.let {
            when (it) {
                is Option.Keyword -> "keyword = ${it.keyword}"
                is Option.FilterStatusList -> "status = ${if (it.filterStatusList.size == 1) it.filterStatusList[0].toString() else it.filterStatusList.joinToString()}"
                is Option.FilterGenderList -> "gender = ${if (it.filterGenderList.size == 1) it.filterGenderList[0] else it.filterGenderList.joinToString()}"
            }
        }
        holder.itemView.setOnClickListener {
            when (datum) {
                is Option.Keyword -> onKeywordClick?.invoke(datum)
                is Option.FilterStatusList -> onFilterStatusListClick?.invoke(datum)
                is Option.FilterGenderList -> onFilterGenderListClick?.invoke(datum)
            }
        }
    }

    override fun areItemsTheSame(oldItem: Option, newItem: Option): Boolean = areContentsTheSame(oldItem, newItem)

    override fun areContentsTheSame(oldItem: Option, newItem: Option): Boolean {
        return oldItem is Option.Keyword && newItem is Option.Keyword && oldItem.keyword == newItem.keyword ||
                oldItem is Option.FilterStatusList && newItem is Option.FilterStatusList && oldItem.filterStatusList == newItem.filterStatusList ||
                oldItem is Option.FilterGenderList && newItem is Option.FilterGenderList && oldItem.filterGenderList == newItem.filterGenderList
    }
}

private class OptionsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(1, 1, 1, 1)
    }
}

private class ItemsAdapter : EndlessListAdapter<Employee>() {
    var onSelect: ((Int, Employee) -> Unit)? = null
    var onClick: ((Int, Employee) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_NORMAL -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.screen_employees_item, parent, false)) {}
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            val item = dataList[position]
            val view = holder.itemView
            view.isActivated = selectedList.contains(item)
            view.emid.text = String.format(Locale.getDefault(), "%04d", item.emid)
            view.emname.text = if (item.emname == null) "-" else item.emname
            view.emgender.setImageDrawable(when (item.emgender) {
                Employee.EMGENDER_MALE -> view.context.resources.getDrawable(R.drawable.ic_male_colored_24dp, view.context.theme)
                Employee.EMGENDER_FEMALE -> view.context.resources.getDrawable(R.drawable.ic_female_colored_24dp, view.context.theme)
                else -> null
            })
            view.emamount.text = String.format(Locale.getDefault(), "%.2f", item.emamount)
            view.setOnClickListener { if (selecting) onSelect?.invoke(position, item) else onClick?.invoke(position, item) }
            view.setOnLongClickListener { if (!selecting) onSelect?.invoke(position, item).let { true } else false }
        }
        super.onBindViewHolder(holder, position)
    }

    override fun areItemsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem.emid == newItem.emid
    }

    override fun areContentsTheSame(oldItem: Employee, newItem: Employee): Boolean {
        return oldItem == newItem
    }
}

private class ItemsItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.set(1, 1, 1, 1)
    }
}

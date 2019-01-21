package test.sales.screen.employees

import test.sales.PartialViewState
import test.sales.Status
import test.sales.ViewState
import test.sales.gen.bean.Employee

sealed class Navigation {
    object None : Navigation()
    object Add : Navigation()
    data class Update(val emid: Int = 0) : Navigation()
}

data class EmployeesViewState(
        //region header
        val keywordDraft: String,
        val keyword: String,
        val filterStatusListOpen: Boolean,
        val filterStatusListDraft: List<Int>,
        val filterStatusList: List<Int>,
        val filterGenderListOpen: Boolean,
        val filterGenderListDraft: List<String>,
        val filterGenderList: List<String>,
        val resultSortOpen: Boolean,
        val resultSortDraft: Pair<String, Boolean>,
        val resultSort: Pair<String, Boolean>,
        val pageSize: Int,
        //endregion
        //region lce
        val status: Status,
        //endregion
        //region lce-content
        val employeeList: List<Employee>,
        val hasMore: Boolean,
        val selecting: Boolean,
        val selectedList: List<Int>,
        val waiting: Boolean,
        val loadingMore: Boolean,
        val deleting: Boolean,
        val popupMessage: String,
        val popupTime: Long,
        //endregion
        //region lce-error
        val message: String,
        //endregion
        val navigation: Navigation
) : ViewState {
    class Builder(
            var keywordDraft: String = "",
            var keyword: String = "",
            var filterStatusListOpen: Boolean = false,
            var filterStatusListDraft: List<Int> = listOf(),
            var filterStatusList: List<Int> = listOf(),
            var filterGenderListOpen: Boolean = false,
            var filterGenderListDraft: List<String> = listOf(),
            var filterGenderList: List<String> = listOf(),
            var resultSortOpen: Boolean = false,
            var resultSortDraft: Pair<String, Boolean> = Pair("", true),
            var resultSort: Pair<String, Boolean> = Pair("", true),
            var pageSize: Int = 0,
            var status: Status = Status.IDLE,
            var Employees: List<Employee> = listOf(),
            var hasMore: Boolean = false,
            var selecting: Boolean = false,
            var selectedList: List<Int> = listOf(),
            var waiting: Boolean = false,
            var loadingMore: Boolean = false,
            var deleting: Boolean = false,
            var popupMessage: String = "",
            var popupTime: Long = 0,
            var message: String = "",
            var navigation: Navigation = Navigation.None) {
        fun build(): EmployeesViewState {
            return EmployeesViewState(
                    keywordDraft,
                    keyword,
                    filterStatusListOpen,
                    filterStatusListDraft,
                    filterStatusList,
                    filterGenderListOpen,
                    filterGenderListDraft,
                    filterGenderList,
                    resultSortOpen,
                    resultSortDraft,
                    resultSort,
                    pageSize,
                    status,
                    Employees,
                    hasMore,
                    selecting,
                    selectedList,
                    waiting,
                    loadingMore,
                    deleting,
                    popupMessage,
                    popupTime,
                    message,
                    navigation)
        }

        companion object {
            fun from(viewState: EmployeesViewState): Builder {
                return Builder(
                        viewState.keywordDraft,
                        viewState.keyword,
                        viewState.filterStatusListOpen,
                        viewState.filterStatusListDraft,
                        viewState.filterStatusList,
                        viewState.filterGenderListOpen,
                        viewState.filterGenderListDraft,
                        viewState.filterGenderList,
                        viewState.resultSortOpen,
                        viewState.resultSortDraft,
                        viewState.resultSort,
                        viewState.pageSize,
                        viewState.status,
                        viewState.employeeList,
                        viewState.hasMore,
                        viewState.selecting,
                        viewState.selectedList,
                        viewState.waiting,
                        viewState.loadingMore,
                        viewState.deleting,
                        viewState.popupMessage,
                        viewState.popupTime,
                        viewState.message,
                        Navigation.None)
            }
        }
    }
}

sealed class EmployeesPartialViewState : PartialViewState {
    class ChangePageSize(val pageSize: Int = 0) : EmployeesPartialViewState()
    class DraftKeyword(val keywordDraft: String = "") : EmployeesPartialViewState()
    class ChangeKeyword(val keyword: String = "") : EmployeesPartialViewState()
    class DraftFilterStatusList(val filterStatusList: List<Int> = listOf()) : EmployeesPartialViewState()
    class ChangeFilterStatusList(val filterStatusList: List<Int> = listOf()) : EmployeesPartialViewState()
    object CloseFilterStatusList : EmployeesPartialViewState()
    class DraftFilterGenderList(val filterGenderList: List<String> = listOf()) : EmployeesPartialViewState()
    class ChangeFilterGenderList(val filterGenderList: List<String> = listOf()) : EmployeesPartialViewState()
    object CloseFilterGenderList : EmployeesPartialViewState()
    class DraftResultSort(val resultSort: Pair<String, Boolean> = Pair("", true)) : EmployeesPartialViewState()
    class ChangeResultSort(val resultSort: Pair<String, Boolean> = Pair("", true)) : EmployeesPartialViewState()
    object CloseResultSort : EmployeesPartialViewState()
    object Loading : EmployeesPartialViewState()
    object ContentRefreshing : EmployeesPartialViewState()
    class ContentRefresh(val Employees: List<Employee> = listOf()) : EmployeesPartialViewState()
    class ContentRefreshSingle(val employee: Employee) : EmployeesPartialViewState()
    class ContentRefreshPopup(val popupMessage: String = "", val popupTime: Long = 0) : EmployeesPartialViewState()
    object ContentLoadingMore : EmployeesPartialViewState()
    class ContentNextPage(val Employees: List<Employee> = listOf()) : EmployeesPartialViewState()
    class ContentNextPagePopup(val popupMessage: String = "", val popupTime: Long = 0) : EmployeesPartialViewState()
    class ContentSetSelection(val selectedList: List<Int> = listOf()) : EmployeesPartialViewState()
    object ContentClearSelection : EmployeesPartialViewState()
    object ContentDeleting : EmployeesPartialViewState()
    class ContentDelete(val deletedList: List<Int> = listOf()) : EmployeesPartialViewState()
    class ContentDeletePopup(val popupMessage: String = "", val popupTime: Long = 0) : EmployeesPartialViewState()
    class ContentClearPopup(val popupTime: Long = 0) : EmployeesPartialViewState()
    class Error(val message: String = "") : EmployeesPartialViewState()
    object Add : EmployeesPartialViewState()
    class Update(val emid: Int = 0) : EmployeesPartialViewState()
}

fun reduceEmployeesViewState(viewState: EmployeesViewState, partial: EmployeesPartialViewState): EmployeesViewState {
    return when (partial) {
        is EmployeesPartialViewState.ChangePageSize ->
            //参数部分，变化了pageSize
            EmployeesViewState.Builder.from(viewState).apply {
                pageSize = partial.pageSize
            }.build()
        is EmployeesPartialViewState.DraftKeyword ->
            //参数部分，变化了keywordDraft
            EmployeesViewState.Builder.from(viewState).apply {
                keywordDraft = partial.keywordDraft
            }.build()
        is EmployeesPartialViewState.ChangeKeyword ->
            //参数部分，变化了keywordDraft,keyword
            EmployeesViewState.Builder.from(viewState).apply {
                keywordDraft = partial.keyword
                keyword = partial.keyword
            }.build()
        is EmployeesPartialViewState.DraftFilterStatusList ->
            //参数部分，变化了filterStatusListOpen，设置了filterStatusListDraft
            EmployeesViewState.Builder.from(viewState).apply {
                filterStatusListOpen = true
                filterStatusListDraft = partial.filterStatusList
            }.build()
        is EmployeesPartialViewState.ChangeFilterStatusList ->
            //参数部分，变化了filterStatusList
            EmployeesViewState.Builder.from(viewState).apply {
                filterStatusList = partial.filterStatusList
            }.build()
        is EmployeesPartialViewState.CloseFilterStatusList ->
            //参数部分，变化了filterStatusListOpen
            EmployeesViewState.Builder.from(viewState).apply {
                filterStatusListOpen = false
                filterStatusListDraft = listOf()
            }.build()
        is EmployeesPartialViewState.DraftFilterGenderList ->
            //参数部分，变化了filterGenderListOpen，设置了filterGenderListDraft
            EmployeesViewState.Builder.from(viewState).apply {
                filterGenderListOpen = true
                filterGenderListDraft = partial.filterGenderList
            }.build()
        is EmployeesPartialViewState.ChangeFilterGenderList ->
            //参数部分，变化了filterGenderList
            EmployeesViewState.Builder.from(viewState).apply {
                filterGenderList = partial.filterGenderList
            }.build()
        is EmployeesPartialViewState.CloseFilterGenderList ->
            //参数部分，变化了filterGenderListOpen
            EmployeesViewState.Builder.from(viewState).apply {
                filterGenderListOpen = false
                filterGenderListDraft = listOf()
            }.build()
        is EmployeesPartialViewState.DraftResultSort ->
            //参数部分，变化了resultSortOpen，设置了resultSortDraft
            EmployeesViewState.Builder.from(viewState).apply {
                resultSortOpen = true
                resultSortDraft = partial.resultSort
            }.build()
        is EmployeesPartialViewState.ChangeResultSort ->
            //参数部分，变化了sort
            EmployeesViewState.Builder.from(viewState).apply {
                resultSort = partial.resultSort
            }.build()
        is EmployeesPartialViewState.CloseResultSort ->
            //参数部分，变化了sortOpen
            EmployeesViewState.Builder.from(viewState).apply {
                resultSortOpen = false
                resultSortDraft = Pair("", false)
            }.build()
        is EmployeesPartialViewState.Loading ->
            //内容部分，status变为STATUS_LOADING
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.LOADING
            }.build()
        is EmployeesPartialViewState.ContentRefreshing ->
            //内容部分，status变为STATUS_CONTENT，waiting变为true
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = true
            }.build()
        is EmployeesPartialViewState.ContentRefresh ->
            //内容部分，status变为STATUS_CONTENT，waiting变为false，设置了Employees和hasMore，清空了select和selectedList
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                Employees = partial.Employees
                hasMore = partial.Employees.size >= viewState.pageSize
                selecting = false
                selectedList = listOf()
            }.build()
        is EmployeesPartialViewState.ContentRefreshSingle ->
            //内容部分，status变为STATUS_CONTENT，waiting变为false，更新了Employees
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                Employees = viewState.employeeList.toMutableList().apply {
                    val index = indexOfFirst { it.emid == partial.employee.emid }
                    if (index > -1) set(index, partial.employee)
                }.toList()
            }.build()
        is EmployeesPartialViewState.ContentRefreshPopup ->
            //内容部分，status变为STATUS_CONTENT，waiting变为false，设置了popupMessage和popupTime
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                popupMessage = partial.popupMessage
                popupTime = partial.popupTime
            }.build()
        is EmployeesPartialViewState.ContentLoadingMore ->
            //内容部分，status变为STATUS_CONTENT，loadingMore变为true
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                loadingMore = true
            }.build()
        is EmployeesPartialViewState.ContentNextPage ->
            //内容部分，status变为STATUS_CONTENT，loadingMore变为false，设置了Employees和hasMore
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                loadingMore = false
                Employees = viewState.employeeList + partial.Employees
                hasMore = partial.Employees.size >= viewState.pageSize
            }.build()
        is EmployeesPartialViewState.ContentNextPagePopup ->
            //内容部分，status变为STATUS_CONTENT，loadingMore变为false，设置了popupMessage和popupTime
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                loadingMore = false
                popupMessage = partial.popupMessage
                popupTime = partial.popupTime
            }.build()
        is EmployeesPartialViewState.ContentSetSelection ->
            //内容部分，status变为STATUS_CONTENT，设置了selecting和selectedList
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                selecting = true
                selectedList = partial.selectedList
            }.build()
        is EmployeesPartialViewState.ContentClearSelection ->
            //内容部分，status变为STATUS_CONTENT，清空了selecting和selectedList
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                selecting = false
                selectedList = listOf()
            }.build()
        is EmployeesPartialViewState.ContentDeleting ->
            //内容部分，status变为STATUS_CONTENT，设置了waiting和deleting
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = true
                deleting = true
            }.build()
        is EmployeesPartialViewState.ContentDelete ->
            //内容部分，status变为STATUS_CONTENT，清空了waiting和deleting，精简了Employees和selectedList
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                deleting = false
                Employees = viewState.employeeList.filter { employee -> partial.deletedList.none { it == employee.emid } }
                selectedList = viewState.selectedList.filter { employee -> partial.deletedList.none { it == employee } }
            }.build()
        is EmployeesPartialViewState.ContentDeletePopup ->
            //内容部分，status变为STATUS_CONTENT，waiting和deleting变为false，设置了popupMessage和popupTime
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                deleting = false
                popupMessage = partial.popupMessage
                popupTime = partial.popupTime
            }.build()
        is EmployeesPartialViewState.ContentClearPopup ->
            //内容部分，status变为STATUS_CONTENT，判断性地清空了popupMessage和popupTime
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                popupMessage = if (partial.popupTime >= viewState.popupTime) "" else viewState.popupMessage
                popupTime = if (partial.popupTime >= viewState.popupTime) 0 else viewState.popupTime
            }.build()
        is EmployeesPartialViewState.Error ->
            //内容部分，status变为STATUS_ERROR，改变了message
            EmployeesViewState.Builder.from(viewState).apply {
                status = Status.ERROR
                message = partial.message
            }.build()
        is EmployeesPartialViewState.Add ->
            //设置了Navigation
            EmployeesViewState.Builder.from(viewState).apply {
                navigation = Navigation.Add
            }.build()
        is EmployeesPartialViewState.Update ->
            //设置了Navigation
            EmployeesViewState.Builder.from(viewState).apply {
                navigation = Navigation.Update(partial.emid)
            }.build()
    }
}

fun initialEmployeesViewState(): EmployeesViewState = EmployeesViewState.Builder().build()

package test.sales.screen.employee

import android.net.Uri
import test.sales.Mode
import test.sales.PartialViewState
import test.sales.Status
import test.sales.ViewState

sealed class Navigation {
    data class Portrait(val uri: Uri) : Navigation()
    data class Finish(val emid: Int) : Navigation()
    object None : Navigation()
}

data class EmployeeViewState(
        //region parameter
        val mode: Mode,
        val emidRef: Int,
        //endregion
        //region lce
        val status: Status,
        //endregion
        //region lce-content
        val portrait: Uri,
        val emid: String,
        val emstatus: Boolean,
        val emgenderOpen: Boolean,
        val emgenderDraft: String,
        val emgender: String,
        val emname: String,
        val emidError: String,
        val emstatusError: String,
        val emgenderError: String,
        val emnameError: String,
        val waiting: Boolean,
        val popupMessage: String,
        val popupTime: Long,
        //endregion
        //region lce-error
        val message: String,
        //endregion
        val navigation: Navigation) : ViewState {
    class Builder(
            var mode: Mode = Mode.ADD,
            var emidRef: Int = 0,
            var status: Status = Status.IDLE,
            var portrait: Uri = Uri.EMPTY,
            var emid: String = "",
            var emstatus: Boolean = false,
            var emgenderOpen: Boolean = false,
            var emgenderDraft: String = "",
            var emgender: String = "",
            var emname: String = "",
            var emidError: String = "",
            var emstatusError: String = "",
            var emgenderError: String = "",
            var emnameError: String = "",
            var waiting: Boolean = false,
            var popupMessage: String = "",
            var popupTime: Long = 0,
            var message: String = "",
            var navigation: Navigation = Navigation.None) {
        fun build(): EmployeeViewState {
            return EmployeeViewState(
                    mode,
                    emidRef,
                    status,
                    portrait,
                    emid,
                    emstatus,
                    emgenderOpen,
                    emgenderDraft,
                    emgender,
                    emname,
                    emidError,
                    emstatusError,
                    emgenderError,
                    emnameError,
                    waiting,
                    popupMessage,
                    popupTime,
                    message,
                    navigation)
        }

        companion object {
            fun from(viewState: EmployeeViewState): Builder {
                return Builder(
                        viewState.mode,
                        viewState.emidRef,
                        viewState.status,
                        viewState.portrait,
                        viewState.emid,
                        viewState.emstatus,
                        viewState.emgenderOpen,
                        viewState.emgenderDraft,
                        viewState.emgender,
                        viewState.emname,
                        viewState.emidError,
                        viewState.emstatusError,
                        viewState.emgenderError,
                        viewState.emnameError,
                        viewState.waiting,
                        viewState.popupMessage,
                        viewState.popupTime,
                        viewState.message,
                        Navigation.None)
            }
        }
    }
}

sealed class EmployeePartialViewState : PartialViewState {
    class SetMode(val mode: Mode = Mode.ADD) : EmployeePartialViewState()
    class SetEmidRef(val emidRef: Int = 0) : EmployeePartialViewState()
    object Loading : EmployeePartialViewState()
    class ContentInit(val portrait: Uri = Uri.EMPTY, val emid: String = "", val emstatus: Boolean = false, val emgender: String = "", val emname: String = "", val emidError: String = "", val emstatusError: String = "", val emgenderError: String = "", val emnameError: String = "") : EmployeePartialViewState()
    class ContentSetPortrait(val portrait: Uri = Uri.EMPTY) : EmployeePartialViewState()
    class ContentSetEmid(val emid: String = "") : EmployeePartialViewState()
    class ContentSetEmstatus(val emstatus: Boolean = false) : EmployeePartialViewState()
    class ContentDraftEmgender(val emgender: String = "") : EmployeePartialViewState()
    class ContentSetEmgender(val emgender: String = "") : EmployeePartialViewState()
    object ContentCloseEmgender : EmployeePartialViewState()
    class ContentSetEmname(val emname: String = "") : EmployeePartialViewState()
    class ContentSetEmidError(val emidError: String = "") : EmployeePartialViewState()
    class ContentSetEmstatusError(val emstatusError: String = "") : EmployeePartialViewState()
    class ContentSetEmgenderError(val emgenderError: String = "") : EmployeePartialViewState()
    class ContentSetEmnameError(val emnameError: String = "") : EmployeePartialViewState()
    object ContentWaiting : EmployeePartialViewState()
    object ContentDone : EmployeePartialViewState()
    class ContentPopup(val popupMessage: String = "", val popupTime: Long = 0) : EmployeePartialViewState()
    class ContentClearPopup(val popupTime: Long = 0) : EmployeePartialViewState()
    class Error(val message: String = "") : EmployeePartialViewState()
    object Portrait : EmployeePartialViewState()
    class Finish(val emid: Int = 0) : EmployeePartialViewState()
}

fun reduceEmployeeViewState(viewState: EmployeeViewState, partial: EmployeePartialViewState): EmployeeViewState {
    return when (partial) {
        is EmployeePartialViewState.SetMode ->
            //参数部分，变化了mode
            EmployeeViewState.Builder.from(viewState).apply {
                mode = partial.mode
            }.build()
        is EmployeePartialViewState.SetEmidRef ->
            //参数部分，变化了emidRef
            EmployeeViewState.Builder.from(viewState).apply {
                emidRef = partial.emidRef
            }.build()
        is EmployeePartialViewState.Loading ->
            //内容部分，status变为STATUS_LOADING
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.LOADING
            }.build()
        is EmployeePartialViewState.ContentInit ->
            //内容部分，status变为STATUS_CONTENT，设置portrait、values、errors，waiting变为false
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                portrait = partial.portrait
                emid = partial.emid
                emstatus = partial.emstatus
                emgender = partial.emgender
                emname = partial.emname
                emidError = partial.emidError
                emstatusError = partial.emstatusError
                emgenderError = partial.emgenderError
                emnameError = partial.emnameError
                waiting = false
            }.build()
        is EmployeePartialViewState.ContentSetPortrait ->
            //内容部分，status变为STATUS_CONTENT，设置portrait
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                portrait = partial.portrait
            }.build()
        is EmployeePartialViewState.ContentSetEmid ->
            //内容部分，status变为STATUS_CONTENT，设置emid
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emid = partial.emid
            }.build()
        is EmployeePartialViewState.ContentSetEmstatus ->
            //内容部分，status变为STATUS_CONTENT，设置emstatus
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emstatus = partial.emstatus
            }.build()
        is EmployeePartialViewState.ContentDraftEmgender ->
            //内容部分，status变为STATUS_CONTENT，emgenderOpen变为true，设置emgenderDraft
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emgenderOpen = true
                emgenderDraft = partial.emgender
            }.build()
        is EmployeePartialViewState.ContentSetEmgender ->
            //内容部分，status变为STATUS_CONTENT，设置emgender
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emgender = partial.emgender
            }.build()
        is EmployeePartialViewState.ContentCloseEmgender ->
            //内容部分，status变为STATUS_CONTENT，emgenderOpen变为false
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emgenderOpen = false
                emgenderDraft = ""
            }.build()
        is EmployeePartialViewState.ContentSetEmname ->
            //内容部分，status变为STATUS_CONTENT，设置emname
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emname = partial.emname
            }.build()
        is EmployeePartialViewState.ContentSetEmidError ->
            //内容部分，status变为STATUS_CONTENT，设置emidError
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emidError = partial.emidError
            }.build()
        is EmployeePartialViewState.ContentSetEmstatusError ->
            //内容部分，status变为STATUS_CONTENT，设置emstatusError
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emstatusError = partial.emstatusError
            }.build()
        is EmployeePartialViewState.ContentSetEmgenderError ->
            //内容部分，status变为STATUS_CONTENT，设置emgenderError
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emgenderError = partial.emgenderError
            }.build()
        is EmployeePartialViewState.ContentSetEmnameError ->
            //内容部分，status变为STATUS_CONTENT，设置emnameError
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                emnameError = partial.emnameError
            }.build()
        is EmployeePartialViewState.ContentWaiting ->
            //内容部分，status变为STATUS_CONTENT，waiting变为true
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = true
            }.build()
        is EmployeePartialViewState.ContentDone ->
            //内容部分，status变为STATUS_CONTENT，waiting变为false
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
            }.build()
        is EmployeePartialViewState.ContentPopup ->
            //内容部分，status变为STATUS_CONTENT，waiting变为false，设置了popupMessage和popupTime
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                waiting = false
                popupMessage = partial.popupMessage
                popupTime = partial.popupTime
            }.build()
        is EmployeePartialViewState.ContentClearPopup ->
            //内容部分，status变为STATUS_CONTENT，判断性地清空了popupMessage和popupTime
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.CONTENT
                popupMessage = if (partial.popupTime >= viewState.popupTime) "" else viewState.popupMessage
                popupTime = if (partial.popupTime >= viewState.popupTime) 0 else viewState.popupTime
            }.build()
        is EmployeePartialViewState.Error ->
            //内容部分，status变为STATUS_ERROR，改变了message
            EmployeeViewState.Builder.from(viewState).apply {
                status = Status.ERROR
                message = partial.message
            }.build()
        is EmployeePartialViewState.Portrait ->
            //设置了Navigation
            EmployeeViewState.Builder.from(viewState).apply {
                navigation = Navigation.Portrait(portrait)
            }.build()
        is EmployeePartialViewState.Finish ->
            //设置了Navigation
            EmployeeViewState.Builder.from(viewState).apply {
                navigation = Navigation.Finish(partial.emid)
            }.build()
    }
}

fun initialEmployeeViewState(): EmployeeViewState = EmployeeViewState.Builder().build()

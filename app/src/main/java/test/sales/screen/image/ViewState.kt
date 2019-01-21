package test.sales.screen.image

import android.net.Uri
import test.sales.PartialViewState
import test.sales.ViewState

sealed class Navigation {
    data class Capture(val uri: Uri) : Navigation()
    object Pick : Navigation()
    data class Crop(val input: Uri, val output: Uri) : Navigation()
    object Finish : Navigation()
    object None : Navigation()
}

data class ImageViewState(
        val image: Uri,
        val source: Uri,
        val cropped: Uri,
        val menuOpen: Boolean,
        val menuDraft: String,
        val navigation: Navigation) : ViewState {
    class Builder(
            var image: Uri = Uri.EMPTY,
            var source: Uri = Uri.EMPTY,
            var cropped: Uri = Uri.EMPTY,
            var menuOpen: Boolean = false,
            var menuDraft: String = "",
            var navigation: Navigation = Navigation.None) {
        fun build(): ImageViewState {
            return ImageViewState(
                    image,
                    source,
                    cropped,
                    menuOpen,
                    menuDraft,
                    navigation)
        }

        companion object {
            fun from(viewState: ImageViewState): Builder {
                return Builder(
                        viewState.image,
                        viewState.source,
                        viewState.cropped,
                        viewState.menuOpen,
                        viewState.menuDraft,
                        Navigation.None)
            }
        }
    }
}

sealed class ImagePartialViewState : PartialViewState {
    class SetImage(val image: Uri = Uri.EMPTY) : ImagePartialViewState()
    class SetCache(val source: Uri = Uri.EMPTY, val cropped: Uri = Uri.EMPTY) : ImagePartialViewState()
    class DraftMenu(val menu: String = "") : ImagePartialViewState()
    object CloseMenu : ImagePartialViewState()
    class Capture(val uri: Uri = Uri.EMPTY) : ImagePartialViewState()
    object Pick : ImagePartialViewState()
    class Crop(val input: Uri = Uri.EMPTY, val output: Uri = Uri.EMPTY) : ImagePartialViewState()
    object Finish : ImagePartialViewState()
}

fun reduceImageViewState(viewState: ImageViewState, partial: ImagePartialViewState): ImageViewState {
    return when (partial) {
        is ImagePartialViewState.SetImage ->
            ImageViewState.Builder.from(viewState).apply {
                image = partial.image
            }.build()
        is ImagePartialViewState.SetCache ->
            ImageViewState.Builder.from(viewState).apply {
                source = partial.source
                cropped = partial.cropped
            }.build()
        is ImagePartialViewState.DraftMenu ->
            ImageViewState.Builder.from(viewState).apply {
                menuOpen = true
                menuDraft = partial.menu
            }.build()
        is ImagePartialViewState.CloseMenu ->
            ImageViewState.Builder.from(viewState).apply {
                menuOpen = false
                menuDraft = ""
            }.build()
        is ImagePartialViewState.Capture ->
            ImageViewState.Builder.from(viewState).apply {
                navigation = Navigation.Capture(partial.uri)
            }.build()
        is ImagePartialViewState.Pick ->
            ImageViewState.Builder.from(viewState).apply {
                navigation = Navigation.Pick
            }.build()
        is ImagePartialViewState.Crop ->
            ImageViewState.Builder.from(viewState).apply {
                navigation = Navigation.Crop(partial.input, partial.output)
            }.build()
        is ImagePartialViewState.Finish ->
            ImageViewState.Builder.from(viewState).apply {
                navigation = Navigation.Finish
            }.build()
    }
}

fun initialImageViewState(): ImageViewState = ImageViewState.Builder().build()

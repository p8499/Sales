package test.sales.layer.image_menu

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layer_image_menu.*
import kotlinx.android.synthetic.main.layer_image_menu.view.*
import kotlinx.android.synthetic.main.layer_image_menu_item.view.*
import test.sales.R
import test.sales.common.LayerFragment
import test.sales.common.SingleSelectableListAdapter
import test.sales.common.dp

class ImageMenuLayer : LayerFragment() {
    override val width: Int = 128.dp
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private val events: ImageMenuEvents? get() = activity as? ImageMenuEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layer_image_menu, container, false)
        view.items.apply {
            adapter = ItemsAdapter(listOf("capture" to "Capture", "pick" to "Pick"), "") {
                events?.onDraft?.invoke(it)
                dismiss()
                events?.onCloseMenu?.invoke()
                when (it) {
                    "capture" -> events?.onCapture?.invoke()
                    "pick" -> events?.onPick?.invoke()
                }
            }
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        events?.onCloseMenu?.invoke()
    }

    fun render(menu: String) {
        (items.adapter as ItemsAdapter).selected = menu
    }
}

interface ImageMenuEvents {
    val onDraft: ((String) -> Unit)?
    val onCapture: (() -> Unit)?
    val onPick: (() -> Unit)?
    val onCloseMenu: (() -> Unit)?
}

private class ItemsAdapter(
        itemList: List<Pair<String, String>> = listOf(), selected: String = "",
        val onChange: ((String) -> Unit)? = null) : SingleSelectableListAdapter<RecyclerView.ViewHolder, String>(itemList, selected) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layer_image_menu_item, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val datum = itemList[position]
        holder.itemView.item.text = itemList[position].second
        holder.itemView.setOnClickListener {
            selected = datum.first
            onChange?.invoke(datum.first)
        }
    }

    override fun updateSelectedStatus(holder: RecyclerView.ViewHolder, selected: Boolean) {
        holder.itemView.isActivated = selected
    }
}
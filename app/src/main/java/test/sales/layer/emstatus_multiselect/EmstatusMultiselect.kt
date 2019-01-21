package test.sales.layer.emstatus_multiselect

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layer_emstatus_multiselect.*
import kotlinx.android.synthetic.main.layer_emstatus_multiselect.view.*
import kotlinx.android.synthetic.main.layer_emstatus_multiselect_item.view.*
import test.sales.R
import test.sales.common.LayerFragment
import test.sales.common.MultipleSelectableListAdapter
import test.sales.gen.bean.Employee


class EmstatusMultiselectLayer : LayerFragment() {
    override val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private val events: EmstatusMultiselectEvents? get() = activity as? EmstatusMultiselectEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layer_emstatus_multiselect, container, false)
        view.items.apply {
            adapter = ItemsAdapter(
                    listOf(Employee.EMSTATUS_VALID to "Valid", Employee.EMSTATUS_INVALID to "Invalid"),
                    listOf()) { events?.onDraftStatusList?.invoke(it) }
            layoutManager = LinearLayoutManager(context)
        }
        view.ok.setOnClickListener {
            dismiss()
            events?.onConfirmStatusList?.invoke((items.adapter as ItemsAdapter).selectedList)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        events?.onCloseStatusList?.invoke()
    }

    fun render(statusList: List<Int>) {
        (items.adapter as ItemsAdapter).selectedList = statusList
    }
}

interface EmstatusMultiselectEvents {
    val onDraftStatusList: ((List<Int>) -> Unit)?
    val onConfirmStatusList: ((List<Int>) -> Unit)?
    val onCloseStatusList: (() -> Unit)?
}

private class ItemsAdapter(
        itemList: List<Pair<Int, String>> = listOf(), selectedList: List<Int> = listOf(),
        val onClick: ((List<Int>) -> Unit)?) : MultipleSelectableListAdapter<RecyclerView.ViewHolder, Int>(itemList, selectedList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layer_emstatus_multiselect_item, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val datum = itemList[position]
        holder.itemView.item.text = datum.second
        holder.itemView.setOnClickListener {
            selectedList = if (selectedList.contains(datum.first)) selectedList - datum.first else selectedList + datum.first
            onClick?.invoke(selectedList)
        }
    }

    override fun updateSelectedStatus(holder: RecyclerView.ViewHolder, selected: Boolean) {
        holder.itemView.isActivated = selected
    }
}

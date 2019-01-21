package test.sales.layer.emsort_singleselect

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layer_emsort_singleselect.*
import kotlinx.android.synthetic.main.layer_emsort_singleselect.view.*
import kotlinx.android.synthetic.main.layer_emsort_singleselect_item.view.*
import test.sales.R
import test.sales.common.LayerFragment
import test.sales.common.SingleSelectableListAdapter
import test.sales.gen.bean.Employee


class EmsortSingleselectLayer : LayerFragment() {
    override val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private val events: EmsortSingleselectEvents? get() = activity as? EmsortSingleselectEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layer_emsort_singleselect, container, false)
        view.items.apply {
            adapter = ItemsAdapter(
                    listOf(Pair(Employee.FIELD_EMID, true) to "ID Min", Pair(Employee.FIELD_EMID, false) to "ID Max", Pair(Employee.FIELD_EMAMOUNT, true) to "Amount Min", Pair(Employee.FIELD_EMAMOUNT, false) to "Amount Max"),
                    "" to false) {
                events?.onDraftSort?.invoke(it)
                dismiss()
                events?.onCloseSort?.invoke()
                events?.onConfirmSort?.invoke(it)
            }
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        events?.onCloseSort?.invoke()
    }

    fun render(sort: Pair<String, Boolean>) {
        (items.adapter as ItemsAdapter).selected = sort
    }
}

interface EmsortSingleselectEvents {
    val onDraftSort: ((Pair<String, Boolean>) -> Unit)?
    val onConfirmSort: ((Pair<String, Boolean>) -> Unit)?
    val onCloseSort: (() -> Unit)?
}

private class ItemsAdapter(
        itemList: List<Pair<Pair<String, Boolean>, String>> = listOf(), selected: Pair<String, Boolean>,
        val onChange: ((Pair<String, Boolean>) -> Unit)? = null) : SingleSelectableListAdapter<RecyclerView.ViewHolder, Pair<String, Boolean>>(itemList, selected) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layer_emsort_singleselect_item, parent, false)) {}
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
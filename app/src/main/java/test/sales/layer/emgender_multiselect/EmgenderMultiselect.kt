package test.sales.layer.emgender_multiselect

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layer_emgender_multiselect.*
import kotlinx.android.synthetic.main.layer_emgender_multiselect.view.*
import kotlinx.android.synthetic.main.layer_emgender_multiselect_item.view.*
import test.sales.R
import test.sales.common.LayerFragment
import test.sales.common.MultipleSelectableListAdapter
import test.sales.gen.bean.Employee

class EmgenderMultiselectLayer : LayerFragment() {
    override val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private val events: EmgenderMultiselectEvents? get() = activity as? EmgenderMultiselectEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layer_emgender_multiselect, container, false)
        view.items.apply {
            adapter = ItemsAdapter(
                    listOf(Employee.EMGENDER_MALE to "Male", Employee.EMGENDER_FEMALE to "Female"),
                    listOf()) { events?.onDraftGenderList?.invoke(it) }
            layoutManager = LinearLayoutManager(context)
        }
        view.ok.setOnClickListener {
            dismiss()
            events?.onConfirmGenderList?.invoke((items.adapter as ItemsAdapter).selectedList)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        events?.onCloseGenderList?.invoke()
    }

    fun render(genderList: List<String>) {
        (items.adapter as ItemsAdapter).selectedList = genderList
    }
}

interface EmgenderMultiselectEvents {
    val onDraftGenderList: ((List<String>) -> Unit)?
    val onConfirmGenderList: ((List<String>) -> Unit)?
    val onCloseGenderList: (() -> Unit)?
}

private class ItemsAdapter(
        itemList: List<Pair<String, String>> = listOf(), selectedList: List<String> = listOf(),
        var onClick: ((List<String>) -> Unit)? = null) : MultipleSelectableListAdapter<RecyclerView.ViewHolder, String>(itemList, selectedList) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layer_emgender_multiselect_item, parent, false)) {}
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
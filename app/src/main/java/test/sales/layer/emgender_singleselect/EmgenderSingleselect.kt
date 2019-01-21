package test.sales.layer.emgender_singleselect

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layer_emgender_singleselect.*
import kotlinx.android.synthetic.main.layer_emgender_singleselect.view.*
import kotlinx.android.synthetic.main.layer_emgender_singleselect_item.view.*
import test.sales.R
import test.sales.common.LayerFragment
import test.sales.common.SingleSelectableListAdapter
import test.sales.gen.bean.Employee

class EmgenderSingleselectLayer : LayerFragment() {
    override val width: Int = ViewGroup.LayoutParams.MATCH_PARENT
    override val height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
    private val events: EmgenderSingleselectEvents? get() = activity as? EmgenderSingleselectEvents
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layer_emgender_singleselect, container, false)
        view.items.apply {
            adapter = ItemsAdapter(
                    listOf(Employee.EMGENDER_MALE to "Male", Employee.EMGENDER_FEMALE to "Female"),
                    "") {
                events?.onDraftGender?.invoke(it)
                dismiss()
                events?.onCloseGender?.invoke()
                events?.onConfirmGender?.invoke(it)
            }
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        events?.onCloseGender?.invoke()
    }

    fun render(gender: String) {
        (items.adapter as ItemsAdapter).selected = gender
    }
}

interface EmgenderSingleselectEvents {
    val onDraftGender: ((String) -> Unit)?
    val onConfirmGender: ((String) -> Unit)?
    val onCloseGender: (() -> Unit)?
}

private class ItemsAdapter(
        itemList: List<Pair<String, String>> = listOf(), selected: String = "",
        val onChange: ((String) -> Unit)? = null) : SingleSelectableListAdapter<RecyclerView.ViewHolder, String>(itemList, selected) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layer_emgender_singleselect_item, parent, false)) {}
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
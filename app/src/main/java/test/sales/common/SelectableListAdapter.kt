package test.sales.common

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class SingleSelectableListAdapter<VH : RecyclerView.ViewHolder, T : Any>(itemList: List<Pair<T, String>> = listOf(), selected: T? = null) : RecyclerView.Adapter<VH>() {
    var itemList: List<Pair<T, String>> = itemList
        set(value) {
            val oldItemList = field
            field = value
            DiffUtil.calculateDiff(getCallback(oldItemList, value)).dispatchUpdatesTo(this)
        }
    var selected: T? = selected
        set(value) {
            val select = { o: T, yn: Boolean ->
                val position = itemList.indexOfFirst { item -> item.first == o }
                val holder = if (position > -1) recyclerView?.findViewHolderForAdapterPosition(position) else null
                holder?.let { updateSelectedStatus(it, yn) }
            }
            field?.let { select(it, false) }
            value?.let { select(it, true) }
            field = value
        }
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        updateSelectedStatus(holder, selected == itemList[position].first)
    }

    protected abstract fun updateSelectedStatus(holder: RecyclerView.ViewHolder, selected: Boolean)

    fun getCallback(list1: List<Pair<T, String>>, list2: List<Pair<T, String>>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return list1.size
            }

            override fun getNewListSize(): Int {
                return list2.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list1[oldItemPosition].first == list2[newItemPosition].first
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list1[oldItemPosition].first == list2[newItemPosition].first
                        && list1[oldItemPosition].second == list2[newItemPosition].second
            }
        }
    }
}

abstract class MultipleSelectableListAdapter<VH : RecyclerView.ViewHolder, T : Any>(itemList: List<Pair<T, String>> = listOf(), selectedList: List<T> = listOf()) : RecyclerView.Adapter<VH>() {
    var itemList: List<Pair<T, String>> = itemList
        set(value) {
            val oldItemList = field
            field = value
            DiffUtil.calculateDiff(getCallback(oldItemList, value)).dispatchUpdatesTo(this)
        }
    var selectedList: List<T> = selectedList
        set(value) {
            val select = { o: T, yn: Boolean ->
                val position = itemList.indexOfFirst { item -> item.first == o }
                val holder = if (position > -1) recyclerView?.findViewHolderForAdapterPosition(position) else null
                holder?.let { updateSelectedStatus(it, yn) }
            }
            (field - value).forEach { select(it, false) }
            (value - field).forEach { select(it, true) }
            field = value
        }
    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        updateSelectedStatus(holder, selectedList.contains(itemList[position].first))
    }

    protected abstract fun updateSelectedStatus(holder: RecyclerView.ViewHolder, selected: Boolean)

    fun getCallback(list1: List<Pair<T, String>>, list2: List<Pair<T, String>>): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return list1.size
            }

            override fun getNewListSize(): Int {
                return list2.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list1[oldItemPosition].first == list2[newItemPosition].first
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list1[oldItemPosition].first == list2[newItemPosition].first
                        && list1[oldItemPosition].second == list2[newItemPosition].second
            }
        }
    }
}
package test.sales.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import test.sales.R

/**
 * 一个小型通用组件，RecyclerView所用的Adapter
 * 当空列表的时候，显示Empty
 * 当有数据且hasMore时，显示数据+Footer
 * 当有数据且!hasMore时，显示数据
 */
const val TYPE_NORMAL = 0x00
const val TYPE_EMPTY = 0x01
const val TYPE_FOOTER = 0x02

class Item<B : Any?>(val type: Int, val item: B, val selected: Boolean)

abstract class EndlessListAdapter<B : Any> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: List<B> = listOf()
        private set
    var hasMore: Boolean = false
        private set
    var selecting: Boolean = false
    var selectedList: List<B> = listOf()
    var loadingMore: Boolean = false
    private val itemList: List<Item<B?>>
        get() {
            return if (dataList.isEmpty())
                listOf(Item(TYPE_EMPTY, null as B?, false))
            else
                dataList
                        .map { Item(TYPE_NORMAL, it as B?, selectedList.contains(it)) }
                        .let { if (hasMore) it + Item(TYPE_FOOTER, null as B?, false) else it }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPTY -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.common_empty, parent, false)) {}
            TYPE_FOOTER -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.common_footer, parent, false)) {}
            else -> object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.common_default, parent, false)) {}
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) 1 else if (hasMore) dataList.size + 1 else dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataList.isEmpty() -> TYPE_EMPTY
            position == dataList.size -> TYPE_FOOTER
            else -> TYPE_NORMAL
        }
    }

    fun set(dataList: List<B>, hasMore: Boolean, selecting: Boolean, selectedList: List<B>) {
        val oldItemList = itemList
        this.dataList = dataList
        this.hasMore = hasMore
        this.selecting = selecting
        this.selectedList = selectedList
        val newItemList = itemList
        DiffUtil.calculateDiff(getDiffUtilCallback(oldItemList, newItemList)).dispatchUpdatesTo(this)
    }

    protected abstract fun areItemsTheSame(oldItem: B, newItem: B): Boolean

    protected abstract fun areContentsTheSame(oldItem: B, newItem: B): Boolean

    private fun getDiffUtilCallback(oldItemList: List<Item<B?>>, newItemList: List<Item<B?>>): DiffUtil.Callback =
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldItemList.size
                override fun getNewListSize(): Int = newItemList.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItemList[oldItemPosition]
                    val newItem = newItemList[newItemPosition]
                    return oldItem.type == newItem.type &&
                            //要么都为null，要么都不为null且itemsTheSame
                            ((oldItem.item == null && newItem.item == null) || (oldItem.item != null && newItem.item != null && areItemsTheSame(oldItem.item, newItem.item)))
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    val oldItem = oldItemList[oldItemPosition]
                    val newItem = newItemList[newItemPosition]
                    return oldItem.type == newItem.type &&
                            //要么都为null，要么都不为null且itemsTheSame
                            ((oldItem.item == null && newItem.item == null) || (oldItem.item != null && newItem.item != null && areContentsTheSame(oldItem.item, newItem.item))) &&
                            oldItem.selected == newItem.selected
                }
            }
}

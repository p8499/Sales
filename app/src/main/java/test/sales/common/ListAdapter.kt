package test.sales.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import test.sales.R

abstract class ListAdapter<B : Any> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var dataList: List<B> = listOf()
        set(value) {
            val oldDataList = field
            field = value
            val newDataList = field
            DiffUtil.calculateDiff(getDiffUtilCallback(oldDataList, newDataList)).dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.common_default, parent, false)) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return dataList.size
    }

    protected abstract fun areItemsTheSame(oldItem: B, newItem: B): Boolean

    protected abstract fun areContentsTheSame(oldItem: B, newItem: B): Boolean

    private fun getDiffUtilCallback(oldDataList: List<B>, newDataList: List<B>): DiffUtil.Callback =
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldDataList.size
                override fun getNewListSize(): Int = newDataList.size
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = areItemsTheSame(oldDataList[oldItemPosition], newDataList[newItemPosition])
                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean = areContentsTheSame(oldDataList[oldItemPosition], newDataList[newItemPosition])
            }
}
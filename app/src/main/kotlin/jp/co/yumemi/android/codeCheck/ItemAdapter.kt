package jp.co.yumemi.android.codeCheck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

// Item ListのRecyclerViewのitemを処理する
class ItemAdapter(
    private val itemClickListener: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(diffUtil) {

    // ItemViewHolderはリストのitemにviewを作ってあげて、データを設定するために存在する
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val repositoryNameView: TextView = view.findViewById(R.id.repositoryNameView)


        // dataとviewと紐付け、Click Eventの処理関数を設定
        fun bind(item: Item, itemClickListener: (Item) -> Unit) {
            repositoryNameView.text = item.name
            itemView.setOnClickListener {
                itemClickListener(item)
            }
        }
    }



    // 新しくViewHolderを作成し、返す。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ItemViewHolder(view)
    }

    // dataとitemViewHolderと紐付け
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, itemClickListener)
    }
}


// itemの比較と更新用
val diffUtil = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }
}

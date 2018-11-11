package se.bylenny.tunin.list

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item.view.*

class SpotifyListAdapter(
    @LayoutRes private val layout: Int
) : RecyclerView.Adapter<SpotifyListAdapter.Holder>() {

    var list: List<Item> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(container: ViewGroup, type: Int): Holder =
        Holder(
            LayoutInflater.from(container.context).inflate(
                layout,
                container,
                false
            )
        )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: Holder, index: Int) = holder.bind(getItem(index))

    private fun getItem(index: Int): Item = list[index]

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Item) {
            itemView.title.text = item.name
            itemView.type.text = item.type
        }
    }

}

package se.bylenny.tunin.list

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

class SpotifyListAdapter(vararg factories: Pair<String, SpotifyViewHolder.Factory<*>>) : RecyclerView.Adapter<SpotifyViewHolder<*>>() {

    private val factories: Map<Int, SpotifyViewHolder.Factory<*>> = factories.map { it.first.hashCode() to it.second }.toMap()

    var list: List<ListItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    //override fun getItemId(position: Int): Long = getItem(position).uri.hashCode().toLong()

    override fun onCreateViewHolder(container: ViewGroup, type: Int): SpotifyViewHolder<*> =
        factories[type]!!.createViewHolder(container)

    override fun getItemViewType(position: Int): Int = getItem(position).type.hashCode()

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SpotifyViewHolder<*>, index: Int) {
        val item = getItem(index)
        holder.bind(item)
    }

    private fun getItem(index: Int): ListItem = list[index]

}

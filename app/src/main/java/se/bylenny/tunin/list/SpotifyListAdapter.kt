package se.bylenny.tunin.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

interface ListView {
    var list: List<ListItem>
    var listener: ClickListener?

    interface ClickListener {
        fun onClicked(item: ListItem)
    }
}

class SpotifyListAdapter(
    vararg factories: Pair<String, SpotifyViewHolder.Factory>
) : RecyclerView.Adapter<SpotifyViewHolder>(), View.OnClickListener, ListView {

    private val factories: Map<Int, SpotifyViewHolder.Factory> = factories.map { it.first.hashCode() to it.second }.toMap()

    override var listener: ListView.ClickListener? = null

    override var list: List<ListItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(container: ViewGroup, type: Int): SpotifyViewHolder =
        factories[type]!!.createViewHolder(container)

    override fun getItemViewType(position: Int): Int = getItem(position).type.hashCode()

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: SpotifyViewHolder, index: Int) {
        val item = getItem(index)
        holder.bind(item, this)
    }

    private fun getItem(index: Int): ListItem = list[index]

    override fun onClick(view: View) {
        (view.tag as? ListItem)?.let { item ->
            listener?.onClicked(item)
        }
    }
}

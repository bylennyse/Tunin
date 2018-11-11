package se.bylenny.tunin.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class SpotifyViewHolder<T: ListItem>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ListItem)

    interface Factory<T: ListItem> {
        fun createViewHolder(container: ViewGroup): SpotifyViewHolder<T>
    }
}
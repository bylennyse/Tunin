package se.bylenny.tunin.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

abstract class SpotifyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: ListItem, listener: View.OnClickListener)

    interface Factory {
        fun createViewHolder(container: ViewGroup): SpotifyViewHolder
    }
}
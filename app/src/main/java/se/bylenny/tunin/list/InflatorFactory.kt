package se.bylenny.tunin.list

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class InflatorFactory<T: ListItem>(
    @LayoutRes private val layout: Int,
    private val creator: (view: View) -> SpotifyViewHolder<T>
): SpotifyViewHolder.Factory<T> {
    override fun createViewHolder(container: ViewGroup): SpotifyViewHolder<T> =
        creator.invoke(LayoutInflater.from(container.context).inflate(
            layout,
            container,
            false
        ))
}
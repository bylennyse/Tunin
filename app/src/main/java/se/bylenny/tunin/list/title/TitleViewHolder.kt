package se.bylenny.tunin.list.title

import android.view.View
import kotlinx.android.synthetic.main.list_item_title.view.*
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.SpotifyViewHolder

class TitleViewHolder(view: View) : SpotifyViewHolder<TitleListItem>(view) {
    override fun bind(item: ListItem) {
        if (item !is TitleListItem) return
        itemView.title.text = item.name
    }
}
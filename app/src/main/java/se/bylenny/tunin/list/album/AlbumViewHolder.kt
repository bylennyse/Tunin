package se.bylenny.tunin.list.album

import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_album.view.*
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.SpotifyViewHolder

class AlbumViewHolder(view: View) : SpotifyViewHolder<AlbumListItem>(view) {
    override fun bind(item: ListItem) {
        if (item !is AlbumListItem) return
        itemView.title.text = item.name
        itemView.type.text = "Album"
        Picasso.with(itemView.context).load(item.image).into(itemView.image)
    }
}
package se.bylenny.tunin.list.artist

import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_artist.view.*
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.SpotifyViewHolder

class ArtistViewHolder(view: View) : SpotifyViewHolder(view) {
    override fun bind(item: ListItem, listener: View.OnClickListener) {
        if (item !is ArtistListItem) return
        itemView.title.text = item.name
        Picasso.with(itemView.context).load(item.image).into(itemView.image)
        itemView.container.tag = item
        itemView.container.setOnClickListener(listener)
    }
}
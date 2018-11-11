package se.bylenny.tunin.list.track

import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_track.view.*
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.SpotifyViewHolder

class TrackViewHolder(view: View) : SpotifyViewHolder(view) {
    override fun bind(item: ListItem, listener: View.OnClickListener) {
        if (item !is TrackListItem) return
        itemView.title.text = item.name
        itemView.artist.text = item.artists.joinToString(", ")
        itemView.album.text = item.album
        itemView.misc.text = "disk ${item.diskNr}, track ${item.trackNr}"
        Picasso.with(itemView.context).load(item.image).into(itemView.image)
        itemView.container.tag = item
        itemView.container.setOnClickListener(listener)
    }
}
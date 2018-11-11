package se.bylenny.tunin.list.track

import android.view.View
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_track.view.*
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.SpotifyViewHolder

class TrackViewHolder(view: View) : SpotifyViewHolder<TrackListItem>(view) {
    override fun bind(item: ListItem) {
        if (item !is TrackListItem) return
        itemView.title.text = item.name
        itemView.artist.text = item.artists.joinToString { ", " }
        itemView.album.text = item.album
        itemView.misc.text = "Disc : ${item.discNr} Track : ${item.trackNr}"
    }
}
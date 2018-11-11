package se.bylenny.tunin.list.artist

import se.bylenny.tunin.list.ListItem

data class ArtistListItem(
    override val uri: String,
    val name: String,
    val image: String?
) : ListItem {
    override val type: Int = "artist".hashCode()
}

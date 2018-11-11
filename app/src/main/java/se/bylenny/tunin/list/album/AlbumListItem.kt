package se.bylenny.tunin.list.album

import se.bylenny.tunin.list.ListItem

data class AlbumListItem(
    override val uri: String,
    val name: String,
    val image: String?
) : ListItem {
    override val type: Int = "album".hashCode()
}

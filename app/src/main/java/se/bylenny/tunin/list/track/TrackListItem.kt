package se.bylenny.tunin.list.track

import se.bylenny.tunin.list.ListItem

data class TrackListItem(
    override val uri: String,
    val name: String,
    val trackNr: Int,
    val diskNr: Int,
    val album: String?,
    val artists: List<String>,
    val image: String?
) : ListItem {
    override val type: Int = "track".hashCode()
}

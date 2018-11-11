package se.bylenny.tunin.list.title

import se.bylenny.tunin.list.ListItem

data class TitleListItem(
    val name: String
) : ListItem {
    override val uri: String = ""
    override val type: Int = "title".hashCode()
}

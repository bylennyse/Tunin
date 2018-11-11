package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Tracks(
    @Json(name = "href") val href: String? = null,
    @Json(name = "items") val items: List<Item> = emptyList(),
    @Json(name = "limit") val limit: Long = 0,
    @Json(name = "next") val next: String? = null,
    @Json(name = "offset") val offset: Long = 0,
    @Json(name = "previous") val previous: String? = null,
    @Json(name = "total") val total: Long = 0
)

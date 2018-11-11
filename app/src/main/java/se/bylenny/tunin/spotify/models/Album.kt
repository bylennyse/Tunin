package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Album(
    @Json(name = "album_type") val albumType: String? = null,
    @Json(name = "artists") val artists: List<Artist> = emptyList(),
    @Json(name = "available_markets") val availableMarkets: List<String> = emptyList(),
    @Json(name = "external_urls") val externalUrls: ExternalUrls? = null,
    @Json(name = "href") val href: String? = null,
    @Json(name = "id") val id: String? = null,
    @Json(name = "images") val images: List<Image> = emptyList(),
    @Json(name = "name") val name: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "release_date_precision") val releaseDatePrecision: String? = null,
    @Json(name = "total_tracks") val totalTracks: Long = 0,
    @Json(name = "type") val type: String? = null,
    @Json(name = "uri") val uri: String? = null
)
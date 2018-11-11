package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "type") val type: String,
    @Json(name = "uri") val uri: String,
    @Json(name = "images") val images: List<Image> = emptyList(),
    @Json(name = "href") val href: String? = null,
    @Json(name = "album") val album: Album? = null,
    @Json(name = "artists") val artists: List<Artist> = emptyList(),
    @Json(name = "available_markets") val availableMarkets: List<String> = emptyList(),
    @Json(name = "disc_number") val discNumber: Int = 0,
    @Json(name = "duration_ms") val durationMs: Long = 0,
    @Json(name = "explicit") val explicit: Boolean = false,
    @Json(name = "external_ids") val externalIds: ExternalIds? = null,
    @Json(name = "external_urls") val externalUrls: ExternalUrls? = null,
    @Json(name = "is_local") val isLocal: Boolean = false,
    @Json(name = "popularity") val popularity: Long = 0,
    @Json(name = "preview_url") val previewUrl: String? = null,
    @Json(name = "track_number") val trackNumber: Int = 0
)

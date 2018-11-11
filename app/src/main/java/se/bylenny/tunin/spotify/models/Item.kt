package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Item(
    @Json(name = "id")
    var id: String,
    @Json(name = "name")
    var name: String,
    @Json(name = "type")
    var type: String,
    @Json(name = "href")
    var href: String? = null,
    @Json(name = "album")
    var album: Album? = null,
    @Json(name = "artists")
    var artists: List<Artist> = emptyList(),
    @Json(name = "available_markets")
    var availableMarkets: List<String> = emptyList(),
    @Json(name = "disc_number")
    var discNumber: Long = 0,
    @Json(name = "duration_ms")
    var durationMs: Long = 0,
    @Json(name = "explicit")
    var explicit: Boolean = false,
    @Json(name = "external_ids")
    var externalIds: ExternalIds? = null,
    @Json(name = "external_urls")
    var externalUrls: ExternalUrls? = null,
    @Json(name = "is_local")
    var isLocal: Boolean = false,
    @Json(name = "popularity")
    var popularity: Long = 0,
    @Json(name = "preview_url")
    var previewUrl: String? = null,
    @Json(name = "track_number")
    var trackNumber: Long = 0,
    @Json(name = "uri")
    var uri: String? = null
)

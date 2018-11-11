package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyList(
    @Json(name = "albums") val albums: Albums? = null,
    @Json(name = "artists") val artists: Artists? = null,
    @Json(name = "tracks") val tracks: Tracks? = null
)
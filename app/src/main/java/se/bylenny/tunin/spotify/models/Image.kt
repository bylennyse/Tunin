package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image(
    @Json(name = "url") val url: String,
    @Json(name = "width") val width: Long = 0,
    @Json(name = "height") val height: Long = 0
)

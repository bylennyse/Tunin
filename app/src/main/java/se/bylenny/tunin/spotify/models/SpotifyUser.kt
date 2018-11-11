package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifyUser(
    @Json(name = "status") val status: Int,
    @Json(name = "message") val message: String
)
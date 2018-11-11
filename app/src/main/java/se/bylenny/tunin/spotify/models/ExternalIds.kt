package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json

data class ExternalIds(
    @Json(name = "isrc") val isrc: String
)

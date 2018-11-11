package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Followers(
    @Json(name = "href") val href: String? = null,
    @Json(name = "total") val total: Long = 0
)

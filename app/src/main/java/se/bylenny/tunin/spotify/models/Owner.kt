package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "display_name") val displayName: String? = null,
    @Json(name = "external_urls") val externalUrls: ExternalUrls? = null,
    @Json(name = "href") val href: String? = null,
    @Json(name = "id") val id: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "uri") val uri: String? = null
)

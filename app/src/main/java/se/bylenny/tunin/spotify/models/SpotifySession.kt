package se.bylenny.tunin.spotify.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SpotifySession(
    @Json(name = "access_token") val accessToken: String?,
    @Json(name = "refresh_token") val refreshToken: String?,
    @Json(name = "token_type") val type: String?,
    @Json(name = "scope") val scope: String?,
    @Json(name = "expires_in") val expireTime: Long?
) {
    val authentication: String
        get() = "bearer $accessToken"
}